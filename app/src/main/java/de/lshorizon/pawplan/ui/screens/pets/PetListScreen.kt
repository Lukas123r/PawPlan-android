package de.lshorizon.pawplan.ui.screens.pets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.FilterChip
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import android.content.res.Configuration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.Observer
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.launch
import de.lshorizon.pawplan.ui.theme.DangerRed
import de.lshorizon.pawplan.ui.components.EmptyState
import androidx.compose.material.icons.outlined.Pets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetListScreen(
    navController: NavController,
    petViewModel: PetViewModel = viewModel(),
    externalQuery: String? = null,
    externalSelectedSpecies: Species? = null
) {
    val pets by petViewModel.pets.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    // Observe navigation results for snackbars
    DisposableEffect(navController, lifecycleOwner) {
        val handle = navController.currentBackStackEntry?.savedStateHandle
        val liveData = handle?.getLiveData<String>("snackbar")
        val observer = Observer<String> { msg ->
            scope.launch { snackbarHostState.showSnackbar(msg) }
            handle?.remove<String>("snackbar")
        }
        liveData?.observe(lifecycleOwner, observer)
        onDispose { liveData?.removeObserver(observer) }
    }
    var query by remember { mutableStateOf("") }
    var selectedSpecies by remember { mutableStateOf<Species?>(null) }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("pet_edit") },
                containerColor = de.lshorizon.pawplan.ui.theme.LoginButtonOrange
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Pet")
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 16.dp)
                .offset(y = (-32).dp),
            contentPadding = PaddingValues(top = 0.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (externalQuery == null) item {
                val focusManager = LocalFocusManager.current
                // Keep search field aligned safely below the AppBar (no negative offset to avoid clipping)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 64.dp, max = 68.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.size(8.dp))
                            androidx.compose.material3.TextField(
                                value = query,
                                onValueChange = { query = it },
                                modifier = Modifier
                                    .weight(1f),
                                placeholder = { Text("Search pets by name or breed") },
                                singleLine = true,
                                textStyle = MaterialTheme.typography.bodyMedium,
                                colors = androidx.compose.material3.TextFieldDefaults.colors(
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    disabledIndicatorColor = Color.Transparent,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                    cursorColor = MaterialTheme.colorScheme.primary
                                ),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() })
                            )
                            if (query.isNotBlank()) {
                                IconButton(onClick = { query = "" }) { Icon(Icons.Outlined.Close, contentDescription = "Clear") }
                            }
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    FilterChip(selected = selectedSpecies == null, onClick = { selectedSpecies = null }, label = { Text("All") })
                    FilterChip(selected = selectedSpecies == Species.DOG, onClick = { selectedSpecies = Species.DOG }, label = { Text("Dogs") })
                    FilterChip(selected = selectedSpecies == Species.CAT, onClick = { selectedSpecies = Species.CAT }, label = { Text("Cats") })
                    FilterChip(selected = selectedSpecies == Species.OTHER, onClick = { selectedSpecies = Species.OTHER }, label = { Text("Other") })
                }

            }
            val q = externalQuery ?: query
            val speciesFilter = externalSelectedSpecies ?: selectedSpecies
            val filtered = pets.filter { (speciesFilter == null || it.species == speciesFilter) && (q.isBlank() || it.name.contains(q, true) || it.breed.contains(q, true)) }
            if (filtered.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Outlined.Pets,
                        title = "No pets yet",
                        actionLabel = "Add Pet",
                        actionColor = de.lshorizon.pawplan.ui.theme.LoginButtonOrange,
                        onActionClick = { navController.navigate("pet_edit") }
                    )
                }
            }
            items(filtered, key = { it.id }) { pet ->
                val dismissState = rememberSwipeToDismissBoxState()
                LaunchedEffect(dismissState.currentValue) {
                    if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
                        petViewModel.deletePet(pet.id)
                        scope.launch { snackbarHostState.showSnackbar("${pet.name} deleted") }
                    }
                }
                SwipeToDismissBox(
                    state = dismissState,
                    enableDismissFromEndToStart = true,
                    enableDismissFromStartToEnd = false,
                    backgroundContent = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp))
                                .background(DangerRed)
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                ) {
                    PetListItem(pet = pet, onClick = {
                        navController.navigate("pet_detail/${pet.id}")
                    })
                }
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
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = "Open details",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Composable
fun PetListScreenPreview() {
    PetListScreen(navController = rememberNavController())
}

@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun PetListScreenPreviewDark() {
    PetListScreen(navController = rememberNavController())
}
