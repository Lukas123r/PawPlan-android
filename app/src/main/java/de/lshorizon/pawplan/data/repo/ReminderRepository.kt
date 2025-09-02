package de.lshorizon.pawplan.data.repo

import android.content.Context
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.channels.awaitClose
import de.lshorizon.pawplan.data.FirebaseConfig

data class ReminderEntity(val id: Int, val title: String, val time: String)

class ReminderRepository(private val context: Context) {
    init { FirebaseConfig.ensurePersistence() }
    private val uid: String get() = Firebase.auth.currentUser?.uid ?: "local"
    private val col get() = Firebase.firestore.collection("users").document(uid).collection("reminders")

    fun observe(): Flow<List<ReminderEntity>> = callbackFlow {
        val reg = col.addSnapshotListener { snap, _ ->
            val list = snap?.documents?.mapNotNull { d ->
                val id = (d.getLong("id") ?: 0L).toInt()
                val title = d.getString("title") ?: return@mapNotNull null
                val time = d.getString("time") ?: ""
                ReminderEntity(id, title, time)
            }?.sortedBy { it.id } ?: emptyList()
            trySend(list)
        }
        awaitClose { reg.remove() }
    }

    suspend fun add(title: String, time: String) {
        val maxId = col.get().await().documents.maxOfOrNull { (it.getLong("id") ?: 0L).toInt() } ?: 0
        val id = maxId + 1
        col.add(mapOf("id" to id, "title" to title, "time" to time)).await()
    }

    suspend fun delete(id: Int) {
        val docs = col.whereEqualTo("id", id).get().await().documents
        docs.forEach { it.reference.delete().await() }
    }
}
