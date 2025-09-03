package de.lshorizon.pawplan.data

import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class UserProfileRepository {
    private val auth = Firebase.auth
    private val db = Firebase.firestore

    // Returns ONLY the Firestore-stored name. No fallback.
    suspend fun getUserName(): String {
        val user = auth.currentUser ?: return ""
        return try {
            val snap = db.collection("users").document(user.uid).get().await()
            val fsName = snap.getString("name")
            fsName ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    suspend fun setUserName(name: String) {
        val user = auth.currentUser ?: return
        // Update Firestore profile document (merge)
        db.collection("users").document(user.uid)
            .set(mapOf("name" to name.trim()), SetOptions.merge())
            .await()
        // Update FirebaseAuth displayName for convenience
        val req = userProfileChangeRequest { displayName = name.trim() }
        user.updateProfile(req).await()
    }

    fun getAuthDisplayName(): String {
        return auth.currentUser?.displayName ?: ""
    }
}
