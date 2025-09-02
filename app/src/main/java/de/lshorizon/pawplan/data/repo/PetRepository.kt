package de.lshorizon.pawplan.data.repo

import android.content.Context
import android.net.Uri
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import de.lshorizon.pawplan.data.FirebaseConfig
import de.lshorizon.pawplan.ui.screens.pets.Pet
import de.lshorizon.pawplan.ui.screens.pets.Species
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class PetRepository(private val context: Context) {
    init { FirebaseConfig.ensurePersistence() }
    private fun petsCollection(): CollectionReference {
        val uid = Firebase.auth.currentUser?.uid ?: "local"
        return Firebase.firestore.collection("users").document(uid).collection("pets")
    }

    fun observePets(): Flow<List<Pet>> = callbackFlow {
        val reg = petsCollection().addSnapshotListener { snap, _ ->
            val list = snap?.documents?.mapNotNull { d ->
                val id = (d.getLong("id") ?: 0L).toInt()
                val name = d.getString("name") ?: return@mapNotNull null
                val breed = d.getString("breed") ?: ""
                val birthdate = d.getString("birthdate") ?: ""
                val speciesStr = d.getString("species") ?: Species.DOG.name
                val species = runCatching { Species.valueOf(speciesStr) }.getOrDefault(Species.DOG)
                val imageUrl = d.getString("imageUrl")
                Pet(id, name, breed, birthdate, species, imageUrl, docId = d.id)
            }?.sortedBy { it.id } ?: emptyList()
            trySend(list)
        }
        awaitClose { reg.remove() }
    }

    suspend fun addOrUpdatePet(pet: Pet, imageUri: Uri?): Unit {
        val col = petsCollection()
        val maxId = col.get().await().documents.maxOfOrNull { (it.getLong("id") ?: 0L).toInt() } ?: 0
        val id = if (pet.id == 0) maxId + 1 else pet.id

        var imageUrl: String? = pet.imageUrl
        if (imageUri != null) {
            val ref = Firebase.storage.reference.child("users/${Firebase.auth.currentUser?.uid ?: "local"}/pets/$id.jpg")
            context.contentResolver.openInputStream(imageUri)?.use { input ->
                ref.putStream(input).await()
            }
            imageUrl = ref.downloadUrl.await().toString()
        }

        val data = mapOf(
            "id" to id,
            "name" to pet.name,
            "breed" to pet.breed,
            "birthdate" to pet.birthdate,
            "species" to pet.species.name,
            "imageUrl" to imageUrl
        )
        val doc = col.whereEqualTo("id", id).get().await().documents.firstOrNull()?.reference
        if (doc == null) col.add(data).await() else doc.set(data).await()
    }

    suspend fun deletePet(id: Int, docId: String? = null) {
        val col = petsCollection()
        if (docId != null) {
            runCatching { col.document(docId).delete().await() }
        } else {
            val docs = col.whereEqualTo("id", id).get().await().documents
            docs.forEach { it.reference.delete().await() }
        }
        // Optionally delete image
        runCatching {
            val ref = Firebase.storage.reference.child("users/${Firebase.auth.currentUser?.uid ?: "local"}/pets/$id.jpg")
            ref.delete().await()
        }
    }
}
