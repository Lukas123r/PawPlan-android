package de.lshorizon.pawplan.data

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import java.util.concurrent.atomic.AtomicBoolean

object FirebaseConfig {
    private val initialized = AtomicBoolean(false)

    fun ensurePersistence() {
        if (initialized.compareAndSet(false, true)) {
            Firebase.firestore.firestoreSettings = firestoreSettings {
                isPersistenceEnabled = true
            }
        }
    }
}

