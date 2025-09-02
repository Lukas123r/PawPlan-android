package de.lshorizon.pawplan.ui.screens.planner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun AddReminderScreen(navController: NavController, vm: ReminderViewModel = viewModel()) {
    val title = remember { mutableStateOf("") }
    val time = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("New Reminder", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(value = title.value, onValueChange = { title.value = it }, label = { Text("Title") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = time.value, onValueChange = { time.value = it }, label = { Text("Time") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Button(
            onClick = {
                if (title.value.isNotBlank()) {
                    vm.add(title.value.trim(), time.value.trim())
                    navController.previousBackStackEntry?.savedStateHandle?.set("snackbar", "Reminder added")
                    navController.popBackStack()
                }
            },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = de.lshorizon.pawplan.ui.theme.SecondaryGreen, contentColor = Color.White)
        ) { Text("Save") }
    }
}

