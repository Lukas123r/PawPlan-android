package de.lshorizon.pawplan.ui.screens.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.Vaccines
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import de.lshorizon.pawplan.ui.screens.pets.PetAvatar
import de.lshorizon.pawplan.ui.screens.pets.PetViewModel
import de.lshorizon.pawplan.ui.theme.AccentOrange
import de.lshorizon.pawplan.ui.theme.LoginButtonOrange
import de.lshorizon.pawplan.ui.theme.PrimaryBlue
import de.lshorizon.pawplan.ui.theme.SecondaryGreen
import de.lshorizon.pawplan.ui.theme.WarningYellow
import de.lshorizon.pawplan.ui.theme.reminderCategoryFor
import de.lshorizon.pawplan.ui.theme.colorFor
import de.lshorizon.pawplan.ui.components.EmptyState
import de.lshorizon.pawplan.ui.screens.planner.ReminderViewModel
import de.lshorizon.pawplan.ui.components.EmptyState
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Pets

// Live data is used from repositories via screens elsewhere; Home shows summaries or empty states.

@Composable
fun HomeScreen(
    onAddPet: () -> Unit,
    onAddReminder: () -> Unit,
    onUploadDocument: () -> Unit,
    onOpenPetList: () -> Unit,
    onOpenDocuments: () -> Unit,
    onOpenPetDetail: (Int) -> Unit = {}
) {
    val petViewModel: PetViewModel = viewModel()
    val pets = petViewModel.pets.collectAsState().value
    val reminderViewModel: ReminderViewModel = viewModel()
    val reminders = reminderViewModel.items.collectAsState().value

    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { SectionTitle("Upcoming Reminders") }
        if (reminders.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.Outlined.Event,
                    title = "No reminders yet",
                    actionLabel = "Add Reminder",
                    actionColor = SecondaryGreen,
                    onActionClick = onAddReminder
                )
            }
        } else {
            items(reminders.take(5)) { r ->
                val tint = colorFor(reminderCategoryFor(r.title))
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val icon = when (reminderCategoryFor(r.title)) {
                            de.lshorizon.pawplan.ui.theme.ReminderCategory.VACCINATION -> Icons.Outlined.Vaccines
                            de.lshorizon.pawplan.ui.theme.ReminderCategory.VET -> Icons.Outlined.MedicalServices
                            de.lshorizon.pawplan.ui.theme.ReminderCategory.DEWORMING -> Icons.Outlined.Event
                            de.lshorizon.pawplan.ui.theme.ReminderCategory.GROOMING -> Icons.Outlined.Event
                            de.lshorizon.pawplan.ui.theme.ReminderCategory.WALK -> Icons.Outlined.Event
                            de.lshorizon.pawplan.ui.theme.ReminderCategory.FEEDING -> Icons.Outlined.Event
                            else -> Icons.Outlined.Event
                        }
                        Icon(icon, contentDescription = null, tint = tint)
                        Spacer(Modifier.size(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(r.title, style = MaterialTheme.typography.titleMedium)
                            Text(r.time, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        item { SectionTitle("Recently Added Documents") }
        item {
            EmptyState(
                icon = Icons.Outlined.Description,
                title = "No documents yet",
                actionLabel = "Upload Document",
                actionColor = PrimaryBlue,
                onActionClick = onUploadDocument
            )
        }

        item { SectionTitle("My Pets") }
        if (pets.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.Outlined.Pets,
                    title = "No pets yet",
                    actionLabel = "Add Pet",
                    actionColor = LoginButtonOrange,
                    onActionClick = onAddPet
                )
            }
        } else items(pets) { p ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenPetDetail(p.id) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PetAvatar(p)
                    Spacer(Modifier.size(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(p.name, style = MaterialTheme.typography.titleMedium)
                        val subtitle = if (p.breed.isNotBlank()) p.breed else p.species.name
                        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(
                        imageVector = Icons.Outlined.ChevronRight,
                        contentDescription = "Open details",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item { SectionTitle("Quick Actions") }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onAddPet,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = de.lshorizon.pawplan.ui.theme.RegisterButtonBlue, contentColor = Color.White)
                ) { Icon(Icons.Outlined.Pets, null); Spacer(Modifier.size(8.dp)); Text("New Pet") }

                Button(
                    onClick = onAddReminder,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LoginButtonOrange, contentColor = Color.White)
                ) { Icon(Icons.Outlined.Event, null); Spacer(Modifier.size(8.dp)); Text("New Reminder") }

                Button(
                    onClick = onUploadDocument,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) { Icon(Icons.Outlined.Description, null); Spacer(Modifier.size(8.dp)); Text("Upload Document") }
            }
        }

        item { HorizontalDivider() }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text("Tap the profile icon above for account & settings", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 8.dp))
}

@Preview(name = "Light Mode", showBackground = true)
@Composable
private fun HomeScreenPreview() {
    HomeScreen(
        onAddPet = {},
        onAddReminder = {},
        onUploadDocument = {},
        onOpenPetList = {},
        onOpenDocuments = {},
        onOpenPetDetail = {}
    )
}

@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun HomeScreenPreviewDark() {
    HomeScreen(
        onAddPet = {},
        onAddReminder = {},
        onUploadDocument = {},
        onOpenPetList = {},
        onOpenDocuments = {},
        onOpenPetDetail = {}
    )
}
