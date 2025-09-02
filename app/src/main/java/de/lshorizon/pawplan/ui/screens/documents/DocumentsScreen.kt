package de.lshorizon.pawplan.ui.screens.documents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.FloatingActionButton
import de.lshorizon.pawplan.ui.components.EmptyState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.Color
import de.lshorizon.pawplan.ui.theme.AccentOrange
import de.lshorizon.pawplan.ui.theme.PrimaryBlue
import de.lshorizon.pawplan.ui.theme.SecondaryGreen
import de.lshorizon.pawplan.ui.theme.documentCategoryFor
import de.lshorizon.pawplan.ui.theme.colorFor
import androidx.compose.ui.tooling.preview.Preview
import android.content.res.Configuration
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import kotlinx.coroutines.launch

// List is provided by DocumentViewModel via Firestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentsScreen(navController: NavController) {
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
                onClick = { navController.navigate("document_upload") },
                containerColor = de.lshorizon.pawplan.ui.theme.PrimaryBlue
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Document")
            }
        }
    ) { padding ->
        val vm: DocumentViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
        val docs = vm.docs.collectAsState().value
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .offset(y = (-32).dp),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text("Documents", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 8.dp))
            }
            if (docs.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Outlined.Description,
                        title = "No documents yet",
                        actionLabel = "Upload Document",
                        actionColor = PrimaryBlue,
                        onActionClick = { navController.navigate("document_upload") }
                    )
                }
            }
            items(docs) { d ->
                val tint = colorFor(documentCategoryFor(d.name))
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.clickable { navController.navigate("document_detail/${d.id}") }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        val icon = when {
                            d.mimeType?.startsWith("image") == true -> Icons.Outlined.Image
                            d.name.lowercase().endsWith(".pdf") -> Icons.Outlined.PictureAsPdf
                            else -> Icons.Outlined.Description
                        }
                        Icon(icon, contentDescription = null, tint = tint)
                        Text(d.name, style = MaterialTheme.typography.titleMedium)
                        if (d.createdAt > 0L) {
                            Text(
                                java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(java.util.Date(d.createdAt)),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Composable
private fun DocumentsScreenPreview() {
    DocumentsScreen(navController = rememberNavController())
}

@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun DocumentsScreenPreviewDark() {
    DocumentsScreen(navController = rememberNavController())
}
