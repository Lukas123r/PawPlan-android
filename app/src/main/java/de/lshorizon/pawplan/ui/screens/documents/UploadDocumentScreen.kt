package de.lshorizon.pawplan.ui.screens.documents

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import de.lshorizon.pawplan.data.repo.DocumentRepository
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast

@Composable
fun UploadDocumentScreen(navController: NavController) {
    val pickedImageUri = remember { mutableStateOf<Uri?>(null) }
    val pickedPdfUri = remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current
    val repo = remember { DocumentRepository(ctx) }
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        pickedImageUri.value = uri
    }
    val pdfPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        pickedPdfUri.value = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Upload Document", style = MaterialTheme.typography.titleLarge)

        Button(
            onClick = {
                imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = de.lshorizon.pawplan.ui.theme.PrimaryBlue, contentColor = Color.White)
        ) { Text("Pick Image") }

        Button(
            onClick = {
                pdfPicker.launch(arrayOf("application/pdf"))
            },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = de.lshorizon.pawplan.ui.theme.RegisterButtonBlue, contentColor = Color.White)
        ) { Text("Pick PDF") }

        if (pickedImageUri.value != null) { Text("Selected image: ${pickedImageUri.value}") }
        if (pickedPdfUri.value != null) { Text("Selected PDF: ${pickedPdfUri.value}") }

        val canUpload = (pickedImageUri.value != null || pickedPdfUri.value != null) && !isUploading
        Button(
            onClick = {
                val uri = pickedImageUri.value ?: pickedPdfUri.value
                if (uri != null) {
                    isUploading = true
                    scope.launch {
                        val mime = if (pickedImageUri.value != null) "image/*" else "application/pdf"
                        val name = uri.lastPathSegment
                        val result = runCatching { repo.uploadDocument(uri, name, mime) }
                        isUploading = false
                        if (result.isSuccess) {
                            navController.previousBackStackEntry?.savedStateHandle?.set("snackbar", "Document uploaded")
                            navController.popBackStack()
                        } else {
                            val msg = result.exceptionOrNull()?.message ?: "Upload failed"
                            Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            },
            enabled = canUpload,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = de.lshorizon.pawplan.ui.theme.SecondaryGreen, contentColor = Color.White, disabledContainerColor = Color.Gray)
        ) { Text(if (isUploading) "Uploading..." else "Upload") }
    }
}
