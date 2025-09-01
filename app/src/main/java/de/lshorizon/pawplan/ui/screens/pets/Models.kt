package de.lshorizon.pawplan.ui.screens.pets

enum class Species { DOG, CAT, OTHER }

data class Pet(
    val id: Int,
    val name: String,
    val breed: String,
    val birthdate: String = "",
    val species: Species = Species.DOG,
    val imageUrl: String? = null
)

