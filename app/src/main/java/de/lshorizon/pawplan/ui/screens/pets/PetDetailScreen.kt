package de.lshorizon.pawplan.ui.screens.pets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.tooling.preview.Preview
import android.content.res.Configuration
import androidx.navigation.compose.rememberNavController

@Composable
fun PetDetailScreen(navController: NavController, petId: Int, petViewModel: PetViewModel = viewModel()) {
    val pets by petViewModel.pets.collectAsState()
    val pet = pets.firstOrNull { it.id == petId }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (pet != null) {
            Text(text = pet.name, style = MaterialTheme.typography.headlineMedium)
            Text(text = "Breed: ${pet.breed}", style = MaterialTheme.typography.bodyLarge)
            if (pet.birthdate.isNotBlank()) {
                Text(text = "Birthdate: ${pet.birthdate}")
            }
            Text(text = "Species: ${pet.species}")

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { navController.navigate("pet_edit/${pet.id}") }, modifier = Modifier.weight(1f)) {
                    Text("Edit")
                }
                Button(
                    onClick = {
                        petViewModel.deletePet(pet.id)
                        navController.popBackStack()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error, contentColor = MaterialTheme.colorScheme.onError)
                ) {
                    Text("Delete")
                }
            }
        } else {
            Text("Pet not found")
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Composable
private fun PetDetailScreenPreview() {
    val vm = PetViewModel()
    val nav = rememberNavController()
    PetDetailScreen(navController = nav, petId = 1, petViewModel = vm)
}

@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun PetDetailScreenPreviewDark() {
    val vm = PetViewModel()
    val nav = rememberNavController()
    PetDetailScreen(navController = nav, petId = 1, petViewModel = vm)
}
