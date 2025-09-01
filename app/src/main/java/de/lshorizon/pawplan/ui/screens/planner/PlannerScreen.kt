package de.lshorizon.pawplan.ui.screens.planner

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.outlined.DirectionsWalk
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.SportsEsports
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.Color
import de.lshorizon.pawplan.ui.theme.AccentOrange
import de.lshorizon.pawplan.ui.theme.PrimaryBlue
import de.lshorizon.pawplan.ui.theme.SecondaryGreen
import androidx.navigation.compose.rememberNavController

data class Reminder(val id: Int, val title: String, val time: String, val icon: ImageVector, val tint: Color)

val sampleReminders = listOf(
    Reminder(1, "Feed Bello", "8:00 AM", Icons.Outlined.Restaurant, AccentOrange),
    Reminder(2, "Walk Lucy", "9:00 AM", Icons.AutoMirrored.Outlined.DirectionsWalk, PrimaryBlue),
    Reminder(3, "Vet appointment for Max", "11:30 AM", Icons.Outlined.MedicalServices, SecondaryGreen),
    Reminder(4, "Play with Luna", "4:00 PM", Icons.Outlined.SportsEsports, PrimaryBlue)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlannerScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Planner") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Navigate to add reminder screen */ },
                containerColor = de.lshorizon.pawplan.ui.theme.LoginButtonOrange
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Reminder")
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
            items(sampleReminders) {
                ReminderListItem(reminder = it, onClick = {
                    // TODO: Navigate to reminder detail screen
                })
            }
        }
    }
}

@Composable
fun ReminderListItem(reminder: Reminder, onClick: () -> Unit) {
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
            Icon(reminder.icon, contentDescription = null, tint = reminder.tint)
            androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.size(12.dp))
            Column {
                Text(text = reminder.title, style = MaterialTheme.typography.headlineSmall)
                Text(text = reminder.time, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlannerScreenPreview() {
    PlannerScreen(navController = rememberNavController())
}
