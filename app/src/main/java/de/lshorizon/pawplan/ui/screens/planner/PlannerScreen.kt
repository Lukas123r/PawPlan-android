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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import android.content.res.Configuration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.Color
import de.lshorizon.pawplan.ui.theme.AccentOrange
import de.lshorizon.pawplan.ui.theme.PrimaryBlue
import de.lshorizon.pawplan.ui.theme.SecondaryGreen
import androidx.navigation.compose.rememberNavController
import de.lshorizon.pawplan.ui.theme.reminderCategoryFor
import de.lshorizon.pawplan.ui.theme.colorFor
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Observer
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState
import de.lshorizon.pawplan.ui.components.EmptyState
import androidx.compose.material.icons.outlined.Event
import androidx.compose.foundation.layout.offset

private fun iconFor(title: String): ImageVector = when (reminderCategoryFor(title)) {
    de.lshorizon.pawplan.ui.theme.ReminderCategory.FEEDING -> Icons.Outlined.Restaurant
    de.lshorizon.pawplan.ui.theme.ReminderCategory.WALK -> Icons.AutoMirrored.Outlined.DirectionsWalk
    de.lshorizon.pawplan.ui.theme.ReminderCategory.VET -> Icons.Outlined.MedicalServices
    de.lshorizon.pawplan.ui.theme.ReminderCategory.GROOMING -> Icons.Outlined.MedicalServices
    else -> Icons.Outlined.SportsEsports
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlannerScreen(navController: NavController) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
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
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("reminder_add") },
                containerColor = de.lshorizon.pawplan.ui.theme.SecondaryGreen
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Reminder")
            }
        }
    ) {
        val vm: ReminderViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
        val reminders = vm.items.collectAsState().value
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .offset(y = (-32).dp),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text("Planner", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 8.dp))
            }
            if (reminders.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Outlined.Event,
                        title = "No reminders yet",
                        actionLabel = "Add Reminder",
                        actionColor = de.lshorizon.pawplan.ui.theme.SecondaryGreen,
                        onActionClick = { navController.navigate("reminder_add") }
                    )
                }
            }
            items(reminders) { rem ->
                ReminderListItem(reminder = rem, onClick = {
                    navController.navigate("planner_detail/${rem.id}")
                })
            }
        }
    }
}

@Composable
fun ReminderListItem(reminder: ReminderItem, onClick: () -> Unit) {
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
            val tint = colorFor(reminderCategoryFor(reminder.title))
            Icon(iconFor(reminder.title), contentDescription = null, tint = tint)
            androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.size(12.dp))
            Column {
                Text(text = reminder.title, style = MaterialTheme.typography.headlineSmall)
                Text(text = reminder.time, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Composable
fun PlannerScreenPreview() {
    PlannerScreen(navController = rememberNavController())
}

@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun PlannerScreenPreviewDark() {
    PlannerScreen(navController = rememberNavController())
}
