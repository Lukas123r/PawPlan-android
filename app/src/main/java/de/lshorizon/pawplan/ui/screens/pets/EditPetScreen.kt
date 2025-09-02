package de.lshorizon.pawplan.ui.screens.pets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import de.lshorizon.pawplan.ui.theme.SecondaryGreen
import de.lshorizon.pawplan.ui.theme.AccentOrange
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.FilterChip
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import android.provider.MediaStore
import android.content.ContentValues
import android.graphics.Bitmap
import de.lshorizon.pawplan.ui.theme.LoginButtonOrange
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.io.IOException
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.firebase.auth.ktx.auth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.mutableFloatStateOf

@Composable
fun EditPetScreen(navController: NavController, petViewModel: PetViewModel = viewModel(), petId: Int?) {
    val existing = petId?.let { petViewModel.petById(it) }
    var name = remember { mutableStateOf("") }
    var breed = remember { mutableStateOf("") }
    var day = remember { mutableStateOf("") }
    var month = remember { mutableStateOf("") }
    var year = remember { mutableStateOf("") }
    var species = remember { mutableStateOf(Species.DOG) }
    var imageUri = remember { mutableStateOf<Uri?>(null) }
    var showPhotoDialog = remember { mutableStateOf(false) }

    val context = LocalContext.current
    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) imageUri.value = uri
    }
    val takePhotoPreview = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            saveBitmapToMediaStore(context, bitmap)?.let { saved -> imageUri.value = saved }
        }
    }

    LaunchedEffect(existing) {
        if (existing != null) {
            name.value = existing.name
            breed.value = existing.breed
            // Parse existing birthdate (supports YYYY-MM-DD or DD-MM-YYYY)
            val raw = existing.birthdate
            val parts = raw.replace('.', '-').replace('/', '-').split('-')
            if (parts.size == 3) {
                val p1 = parts[0].trim()
                val p2 = parts[1].trim()
                val p3 = parts[2].trim()
                if (p1.length == 4) {
                    // YYYY-MM-DD -> set to DD-MM-YYYY inputs
                    year.value = p1
                    month.value = p2
                    day.value = p3
                } else {
                    // Assume DD-MM-YYYY
                    day.value = p1
                    month.value = p2
                    year.value = p3
                }
            }
            species.value = existing.species
            if (!existing.imageUrl.isNullOrBlank()) {
                runCatching { Uri.parse(existing.imageUrl) }.getOrNull()?.let { imageUri.value = it }
            }
        }
    }

    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val uploading = remember { mutableStateOf(false) }
    val progress = remember { mutableFloatStateOf(0f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SnackbarHost(hostState = snackbar)
        Text(text = if (existing == null) "New Pet" else "Edit Pet", style = MaterialTheme.typography.headlineMedium)

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Header icon
                Icon(
                    imageVector = Icons.Outlined.Pets,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp).align(Alignment.CenterHorizontally)
                )

                // Photo picker / preview
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showPhotoDialog.value = true }
                        .align(Alignment.CenterHorizontally),
                    contentAlignment = Alignment.Center
                ) {
                    val uri = imageUri.value
                    if (uri != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(context).data(uri).crossfade(true).build(),
                            contentDescription = "Pet photo",
                            modifier = Modifier.matchParentSize(),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(
                                imageVector = Icons.Outlined.AddAPhoto,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(28.dp)
                            )
                            Text("Add/Take Photo", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                if (showPhotoDialog.value) {
                    AlertDialog(
                        onDismissRequest = { showPhotoDialog.value = false },
                        title = { Text("Add/Take Photo") },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                TextButton(onClick = {
                                    showPhotoDialog.value = false
                                    photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                }) { Text("Pick from gallery") }
                                TextButton(onClick = {
                                    showPhotoDialog.value = false
                                    takePhotoPreview.launch(null)
                                }) { Text("Take photo") }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showPhotoDialog.value = false }) { Text("Cancel") }
                        }
                    )
                }

                OutlinedTextField(value = name.value, onValueChange = { name.value = it }, label = { Text("Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = breed.value, onValueChange = { breed.value = it }, label = { Text("Breed") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = day.value,
                onValueChange = { input -> day.value = input.filter { it.isDigit() }.take(2) },
                label = { Text("Day") },
                singleLine = true,
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = month.value,
                onValueChange = { input -> month.value = input.filter { it.isDigit() }.take(2) },
                label = { Text("Month") },
                singleLine = true,
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = year.value,
                onValueChange = { input -> year.value = input.filter { it.isDigit() }.take(4) },
                label = { Text("Year") },
                singleLine = true,
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    val chipColors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                        selectedContainerColor = LoginButtonOrange,
                        selectedLabelColor = Color.White
                    )
                    FilterChip(selected = species.value == Species.DOG, onClick = { species.value = Species.DOG }, label = { Text("Dog") }, colors = chipColors)
                    FilterChip(selected = species.value == Species.CAT, onClick = { species.value = Species.CAT }, label = { Text("Cat") }, colors = chipColors)
                    FilterChip(selected = species.value == Species.OTHER, onClick = { species.value = Species.OTHER }, label = { Text("Other") }, colors = chipColors)
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { navController.popBackStack() }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)) { Text("Cancel") }
            Button(onClick = {
                val dd = day.value.padStart(2, '0')
                val mm = month.value.padStart(2, '0')
                val yyyy = year.value
                val birthdateFormatted = listOf(dd, mm, yyyy).joinToString("-")
                val pet = if (existing == null) {
                    Pet(0, name.value.trim(), breed.value.trim(), birthdateFormatted, species.value, imageUrl = null)
                } else {
                    existing.copy(name = name.value.trim(), breed = breed.value.trim(), birthdate = birthdateFormatted, species = species.value)
                }
                scope.launch {
                    try {
                        var finalPet = pet
                        if (imageUri.value != null) {
                            // Upload mit Fortschritt
                            uploading.value = true
                            progress.floatValue = 0f
                            val ctx = context
                            val ref = Firebase.storage.reference
                                .child("users/${Firebase.auth.currentUser?.uid ?: "local"}/pets/${System.currentTimeMillis()}.jpg")
                            ctx.contentResolver.openInputStream(imageUri.value!!)?.use { input ->
                                val uploadTask = ref.putStream(input)
                                uploadTask.addOnProgressListener { snap: com.google.firebase.storage.UploadTask.TaskSnapshot ->
                                    val total = (snap.totalByteCount).coerceAtLeast(1L)
                                    progress.floatValue = snap.bytesTransferred.toFloat() / total.toFloat()
                                }.await()
                            }
                            val url = ref.downloadUrl.await().toString()
                            finalPet = finalPet.copy(imageUrl = url)
                            uploading.value = false
                        }
                        val tempId = petViewModel.optimisticAdd(finalPet)
                        navController.popBackStack()
                        val ok = petViewModel.savePet(finalPet, imageUri = null)
                        if (!ok) {
                            petViewModel.removeOptimistic(tempId)
                            navController.previousBackStackEntry?.savedStateHandle?.set("snackbar", "Failed to save pet")
                        }
                    } catch (t: Throwable) {
                        uploading.value = false
                        snackbar.showSnackbar(t.message ?: "Upload failed")
                    }
                }
            }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = SecondaryGreen, contentColor = Color.White)) { Text("Save") }
        }

        if (uploading.value) {
            LinearProgressIndicator(progress = progress.floatValue, modifier = Modifier.fillMaxWidth())
            Text(text = "Uploading image… ${(progress.floatValue * 100).toInt()}%")
        }
    }
}

private fun saveBitmapToMediaStore(context: android.content.Context, bitmap: Bitmap): Uri? {
    val name = "pet_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.jpg"
    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, name)
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
    }
    return try {
        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        if (uri != null) {
            context.contentResolver.openOutputStream(uri)?.use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
        }
        uri
    } catch (t: Throwable) {
        null
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Composable
private fun EditPetScreenNewPreview() {
    val nav = rememberNavController()
    EditPetScreen(navController = nav, petViewModel = viewModel(), petId = null)
}

@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun EditPetScreenNewPreviewDark() {
    val nav = rememberNavController()
    EditPetScreen(navController = nav, petViewModel = viewModel(), petId = null)
}

@Preview(name = "Light Mode", showBackground = true)
@Composable
private fun EditPetScreenEditPreview() {
    val nav = rememberNavController()
    EditPetScreen(navController = nav, petViewModel = viewModel(), petId = 1)
}

@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun EditPetScreenEditPreviewDark() {
    val nav = rememberNavController()
    EditPetScreen(navController = nav, petViewModel = viewModel(), petId = 1)
}
