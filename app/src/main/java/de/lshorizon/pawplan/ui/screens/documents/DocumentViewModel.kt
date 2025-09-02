package de.lshorizon.pawplan.ui.screens.documents

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class DocItem(val id: String, val name: String, val mimeType: String?, val createdAt: Long)

class DocumentViewModel(app: Application) : AndroidViewModel(app) {
    private val _docs = MutableStateFlow<List<DocItem>>(emptyList())
    val docs: StateFlow<List<DocItem>> = _docs

    init {
        val uid = Firebase.auth.currentUser?.uid ?: "local"
        val col = Firebase.firestore.collection("users").document(uid).collection("documents")
        col.addSnapshotListener { snap, _ ->
            val list = snap?.documents?.map { d ->
                DocItem(
                    id = d.id,
                    name = d.getString("name") ?: d.id,
                    mimeType = d.getString("mimeType"),
                    createdAt = d.getLong("createdAt") ?: 0L
                )
            }?.sortedByDescending { it.createdAt } ?: emptyList()
            _docs.value = list
        }
    }
}

