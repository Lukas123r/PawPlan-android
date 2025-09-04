package de.lshorizon.pawplan.ui.screens.documents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import de.lshorizon.pawplan.data.SettingsRepository
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import de.lshorizon.pawplan.ui.theme.DangerRed
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun DocumentDetailScreen(navController: NavController, documentId: Int?) {
    var showConfirm by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val settings by remember { SettingsRepository(context) }.state.collectAsState(initial = de.lshorizon.pawplan.data.SettingsState())
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Document Detail", style = MaterialTheme.typography.titleLarge)
        Text(text = "ID: ${documentId ?: -1}")
        Text(text = "This is a placeholder for document details.")
        Button(
            onClick = {
                if (settings.confirmBeforeDelete) showConfirm = true else {
                    // TODO: implement actual deletion when backend exists
                    navController.previousBackStackEntry?.savedStateHandle?.set("snackbar", "Document deleted")
                    navController.popBackStack()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = DangerRed, contentColor = Color.White),
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Delete Document")
        }
        if (showConfirm) {
            AlertDialog(
                onDismissRequest = { showConfirm = false },
                confirmButton = {
                    TextButton(onClick = {
                        showConfirm = false
                        // TODO: implement actual deletion when backend exists
                        navController.previousBackStackEntry?.savedStateHandle?.set("snackbar", "Document deleted")
                        navController.popBackStack()
                    }) { Text(stringResource(id = de.lshorizon.pawplan.R.string.delete), color = DangerRed) }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirm = false }) { Text(stringResource(id = de.lshorizon.pawplan.R.string.cancel)) }
                },
                title = { Text(stringResource(id = de.lshorizon.pawplan.R.string.delete_document_title)) },
                text = { Text(stringResource(id = de.lshorizon.pawplan.R.string.delete_document_text)) }
            )
        }
    }
}
