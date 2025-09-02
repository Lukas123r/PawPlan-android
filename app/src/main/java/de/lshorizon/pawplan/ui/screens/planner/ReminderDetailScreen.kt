package de.lshorizon.pawplan.ui.screens.planner

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import de.lshorizon.pawplan.ui.theme.DangerRed
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun ReminderDetailScreen(navController: NavController, reminderId: Int?) {
    var showConfirm by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Reminder Detail", style = MaterialTheme.typography.titleLarge)
        Text(text = "ID: ${reminderId ?: -1}")
        Text(text = "This is a placeholder for reminder details.")
        Button(
            onClick = { showConfirm = true },
            colors = ButtonDefaults.buttonColors(containerColor = DangerRed, contentColor = Color.White),
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Delete Reminder")
        }
        if (showConfirm) {
            AlertDialog(
                onDismissRequest = { showConfirm = false },
                confirmButton = {
                    TextButton(onClick = {
                        showConfirm = false
                        // TODO: implement actual deletion when backend exists
                        navController.previousBackStackEntry?.savedStateHandle?.set("snackbar", "Reminder deleted")
                        navController.popBackStack()
                    }) {
                        Text("Delete", color = DangerRed)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirm = false }) {
                        Text("Cancel")
                    }
                },
                title = { Text("Delete reminder?") },
                text = { Text("Are you sure you want to delete this reminder?") }
            )
        }
    }
}
