package de.lshorizon.pawplan.ui.screens.pets

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class PetViewModel : ViewModel() {
    private val _pets = MutableStateFlow<List<Pet>>(listOf(
        Pet(1, "Bello", "Golden Retriever", birthdate = "2021-05-01", species = Species.DOG),
        Pet(2, "Lucy", "Labrador", birthdate = "2022-01-15", species = Species.DOG),
        Pet(3, "Max", "Domestic Shorthair", birthdate = "2020-09-30", species = Species.CAT),
        Pet(4, "Luna", "Beagle", birthdate = "2023-03-12", species = Species.DOG)
    ))
    val pets: StateFlow<List<Pet>> = _pets

    fun petById(id: Int): Pet? = _pets.value.firstOrNull { it.id == id }

    fun addPet(name: String, breed: String, birthdate: String, species: Species, imageUrl: String?) {
        val nextId = (_pets.value.maxOfOrNull { it.id } ?: 0) + 1
        _pets.update { it + Pet(nextId, name, breed, birthdate, species, imageUrl) }
    }

    fun updatePet(id: Int, name: String, breed: String, birthdate: String, species: Species, imageUrl: String?) {
        _pets.update { list -> list.map { if (it.id == id) it.copy(name = name, breed = breed, birthdate = birthdate, species = species, imageUrl = imageUrl) else it } }
    }

    fun deletePet(id: Int) {
        _pets.update { it.filterNot { p -> p.id == id } }
    }
}

