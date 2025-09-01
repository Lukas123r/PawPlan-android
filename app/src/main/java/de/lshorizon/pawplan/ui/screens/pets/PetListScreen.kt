package de.lshorizon.pawplan.ui.screens.pets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest

enum class Species { DOG, CAT, OTHER }

data class Pet(
    val id: Int,
    val name: String,
    val breed: String,
    val species: Species = Species.DOG,
    val imageUrl: String? = null
)

val samplePets = listOf(
    Pet(1, "Bello", "Golden Retriever", species = Species.DOG, imageUrl = null),
    Pet(2, "Lucy", "Labrador", species = Species.DOG, imageUrl = null),
    Pet(3, "Max", "Domestic Shorthair", species = Species.CAT, imageUrl = null),
    Pet(4, "Luna", "Beagle", species = Species.DOG, imageUrl = null)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetListScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("My Pets") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Navigate to add pet screen incl. photo picker */ },
                containerColor = de.lshorizon.pawplan.ui.theme.LoginButtonOrange
            ) {
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
fun PetAvatar(pet: Pet, modifier: Modifier = Modifier) {
    val size = 48.dp
    if (pet.imageUrl != null) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(pet.imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Pet photo",
            modifier = modifier.size(size).clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    } else {
        val emoji = when (pet.species) {
            Species.DOG -> "🐶"
            Species.CAT -> "🐱"
            Species.OTHER -> "🐾"
        }
        Box(
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(emoji, fontSize = 24.sp)
        }
    }
}

@Composable
fun PetListItem(pet: Pet, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PetAvatar(pet, modifier = Modifier)
            androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.size(12.dp))
            Column(Modifier.weight(1f)) {
                Text(text = pet.name, style = MaterialTheme.typography.headlineSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = pet.breed, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PetListScreenPreview() {
    PetListScreen(navController = rememberNavController())
}
