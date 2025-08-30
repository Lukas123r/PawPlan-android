package de.lshorizon.pawplan.ui.screens.pets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

data class Pet(val id: Int, val name: String, val breed: String)

val samplePets = listOf(
    Pet(1, "Bello", "Golden Retriever"),
    Pet(2, "Lucy", "Labrador"),
    Pet(3, "Max", "German Shepherd"),
    Pet(4, "Luna", "Beagle")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetListScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("My Pets") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO: Navigate to add pet screen */ }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Pet")
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(samplePets) { pet ->
                PetListItem(pet = pet, onClick = {
                    // TODO: Navigate to pet detail screen
                })
            }
        }
    }
}

@Composable
fun PetListItem(pet: Pet, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = pet.name, style = MaterialTheme.typography.headlineSmall)
                Text(text = pet.breed, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PetListScreenPreview() {
    PetListScreen(navController = rememberNavController())
}