package de.lshorizon.pawplan.data.repo

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import de.lshorizon.pawplan.data.FirebaseConfig
import de.lshorizon.pawplan.data.SettingsRepository
import de.lshorizon.pawplan.R

class DocumentRepository(private val context: Context) {
    init { FirebaseConfig.ensurePersistence() }
    private val uid: String get() = Firebase.auth.currentUser?.uid ?: "local"
    private val docsCol get() = Firebase.firestore.collection("users").document(uid).collection("documents")

    suspend fun uploadDocument(uri: Uri, displayName: String?, mimeType: String?): Unit {
        val settings = SettingsRepository(context).state.first()
        if (settings.wifiOnlyUploads && !isOnWifi()) {
            val msg = context.getString(R.string.upload_wifi_only_message)
            // Show a quick hint and abort
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            throw IllegalStateException(msg)
        }
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

    private fun isOnWifi(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }
}
