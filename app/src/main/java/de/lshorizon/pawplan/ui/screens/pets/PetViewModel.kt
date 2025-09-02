package de.lshorizon.pawplan.ui.screens.pets

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.lshorizon.pawplan.data.repo.PetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PetViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = PetRepository(app.applicationContext)
    private val _pets = MutableStateFlow<List<Pet>>(emptyList())
    val pets: StateFlow<List<Pet>> = _pets

    init {
        viewModelScope.launch {
            repo.observePets().collect { _pets.value = it }
        }
    }

    fun petById(id: Int): Pet? = _pets.value.firstOrNull { it.id == id }

    fun addOrUpdatePet(pet: Pet, imageUri: Uri?) {
        viewModelScope.launch { repo.addOrUpdatePet(pet, imageUri) }
    }

    suspend fun savePet(pet: Pet, imageUri: Uri?): Boolean {
        return try {
            repo.addOrUpdatePet(pet, imageUri)
            true
        } catch (t: Throwable) {
            false
        }
    }

    fun deletePet(id: Int) {
        val docId = _pets.value.firstOrNull { it.id == id }?.docId
        viewModelScope.launch { repo.deletePet(id, docId) }
    }

    fun optimisticAdd(pet: Pet): String {
        val tempId = "local-${System.currentTimeMillis()}"
        _pets.value = _pets.value + pet.copy(docId = tempId)
        return tempId
    }

    fun removeOptimistic(tempDocId: String) {
        _pets.value = _pets.value.filterNot { it.docId == tempDocId }
    }
}
