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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.tooling.preview.Preview
import android.content.res.Configuration
import androidx.navigation.compose.rememberNavController
import de.lshorizon.pawplan.ui.theme.DangerRed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import de.lshorizon.pawplan.data.SettingsRepository
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.stringResource

@Composable
fun PetDetailScreen(navController: NavController, petId: Int, petViewModel: PetViewModel = viewModel()) {
    val pets by petViewModel.pets.collectAsState()
    val context = LocalContext.current
    val settings by remember { SettingsRepository(context) }.state.collectAsState(initial = de.lshorizon.pawplan.data.SettingsState())
    var showConfirm by remember { mutableStateOf(false) }
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
                val formatted = formatBirthdateDisplay(pet.birthdate)
                Text(text = "Birthdate: $formatted")
            }
            Text(text = "Species: ${pet.species}")

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { navController.navigate("pet_edit/${pet.id}") }, modifier = Modifier.weight(1f)) {
                    Text("Edit")
                }
                Button(
                    onClick = {
                        if (settings.confirmBeforeDelete) showConfirm = true else {
                            petViewModel.deletePet(pet.id)
                            navController.previousBackStackEntry?.savedStateHandle?.set("snackbar", "Pet deleted")
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed, contentColor = Color.White)
                ) {
                    Text("Delete")
                }
                if (showConfirm) {
                    AlertDialog(
                        onDismissRequest = { showConfirm = false },
                        confirmButton = {
                            TextButton(onClick = {
                                showConfirm = false
                                petViewModel.deletePet(pet.id)
                                navController.previousBackStackEntry?.savedStateHandle?.set("snackbar", "Pet deleted")
                                navController.popBackStack()
                            }) { Text(stringResource(id = de.lshorizon.pawplan.R.string.delete), color = DangerRed) }
                        },
                        dismissButton = {
                            TextButton(onClick = { showConfirm = false }) { Text(stringResource(id = de.lshorizon.pawplan.R.string.cancel)) }
                        },
                        title = { Text(stringResource(id = de.lshorizon.pawplan.R.string.delete_pet_title)) },
                        text = { Text(stringResource(id = de.lshorizon.pawplan.R.string.delete_pet_text, pet.name)) }
                    )
                }
            }
        } else {
            Text("Pet not found")
        }
    }
}

private fun formatBirthdateDisplay(raw: String): String {
    val sanitized = raw.replace('.', '-').replace('/', '-')
    val parts = sanitized.split('-')
    if (parts.size == 3) {
        val p1 = parts[0].trim()
        val p2 = parts[1].trim()
        val p3 = parts[2].trim()
        return if (p1.length == 4) {
            // YYYY-MM-DD -> DD-MM-YYYY
            "${p3.padStart(2, '0')}-${p2.padStart(2, '0')}-${p1}"
        } else {
            // assume DD-MM-YYYY
            "${p1.padStart(2, '0')}-${p2.padStart(2, '0')}-${p3}"
        }
    }
    return raw
}

@Preview(name = "Light Mode", showBackground = true)
@Composable
private fun PetDetailScreenPreview() {
    val nav = rememberNavController()
    PetDetailScreen(navController = nav, petId = 1)
}

@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun PetDetailScreenPreviewDark() {
    val nav = rememberNavController()
    PetDetailScreen(navController = nav, petId = 1)
}
