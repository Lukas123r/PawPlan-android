package de.lshorizon.pawplan.ui.screens.pets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.tooling.preview.Preview
import android.content.res.Configuration
import androidx.navigation.compose.rememberNavController

@Composable
fun EditPetScreen(navController: NavController, petViewModel: PetViewModel = viewModel(), petId: Int?) {
    val existing = petId?.let { petViewModel.petById(it) }
    var name = remember { mutableStateOf("") }
    var breed = remember { mutableStateOf("") }
    var birthdate = remember { mutableStateOf("") }
    var species = remember { mutableStateOf(Species.DOG) }

    LaunchedEffect(existing) {
        if (existing != null) {
            name.value = existing.name
            breed.value = existing.breed
            birthdate.value = existing.birthdate
            species.value = existing.species
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = if (existing == null) "New Pet" else "Edit Pet", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(value = name.value, onValueChange = { name.value = it }, label = { Text("Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = breed.value, onValueChange = { breed.value = it }, label = { Text("Breed") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = birthdate.value, onValueChange = { birthdate.value = it }, label = { Text("Birthdate (YYYY-MM-DD)") }, singleLine = true, modifier = Modifier.fillMaxWidth())

        // TODO: Replace with proper species selector and photo picker
        OutlinedTextField(value = species.value.name, onValueChange = { /* no-op */ }, label = { Text("Species (DOG/CAT/OTHER)") }, singleLine = true, enabled = false, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { navController.popBackStack() }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)) { Text("Cancel") }
            Button(onClick = {
                if (existing == null) {
                    petViewModel.addPet(name.value.trim(), breed.value.trim(), birthdate.value.trim(), species.value, imageUrl = null)
                } else {
                    petViewModel.updatePet(existing.id, name.value.trim(), breed.value.trim(), birthdate.value.trim(), species.value, imageUrl = null)
                }
                navController.popBackStack()
            }, modifier = Modifier.weight(1f)) { Text("Save") }
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Composable
private fun EditPetScreenNewPreview() {
    val vm = PetViewModel()
    val nav = rememberNavController()
    EditPetScreen(navController = nav, petViewModel = vm, petId = null)
}

@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun EditPetScreenNewPreviewDark() {
    val vm = PetViewModel()
    val nav = rememberNavController()
    EditPetScreen(navController = nav, petViewModel = vm, petId = null)
}

@Preview(name = "Light Mode", showBackground = true)
@Composable
private fun EditPetScreenEditPreview() {
    val vm = PetViewModel()
    val nav = rememberNavController()
    EditPetScreen(navController = nav, petViewModel = vm, petId = 1)
}

@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun EditPetScreenEditPreviewDark() {
    val vm = PetViewModel()
    val nav = rememberNavController()
    EditPetScreen(navController = nav, petViewModel = vm, petId = 1)
}
