package de.lshorizon.pawplan.data.repo

import android.content.Context
import android.net.Uri
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import de.lshorizon.pawplan.data.FirebaseConfig

class DocumentRepository(private val context: Context) {
    init { FirebaseConfig.ensurePersistence() }
    private val uid: String get() = Firebase.auth.currentUser?.uid ?: "local"
    private val docsCol get() = Firebase.firestore.collection("users").document(uid).collection("documents")

    suspend fun uploadDocument(uri: Uri, displayName: String?, mimeType: String?): Unit {
        val name = displayName ?: "doc_${System.currentTimeMillis()}"
        val ref = Firebase.storage.reference.child("users/$uid/documents/$name")
        val md = StorageMetadata.Builder().setContentType(mimeType ?: "application/octet-stream").build()
        context.contentResolver.openInputStream(uri)?.use { input ->
            ref.putStream(input, md).await()
        }
        val url = ref.downloadUrl.await().toString()
        val meta = mapOf(
            "name" to name,
            "url" to url,
            "mimeType" to (mimeType ?: ""),
            "createdAt" to System.currentTimeMillis()
        )
        docsCol.add(meta).await()
    }
}
