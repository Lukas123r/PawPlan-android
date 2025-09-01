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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

data class DocumentItem(val id: Int, val name: String, val date: String, val icon: ImageVector, val tint: Color)

private val sampleDocs = listOf(
    DocumentItem(1, "Vaccination_Bello.pdf", "2 days ago", Icons.Outlined.PictureAsPdf, AccentOrange),
    DocumentItem(2, "LabReport_Max.pdf", "1 week ago", Icons.Outlined.Description, PrimaryBlue),
    DocumentItem(3, "X-Ray_Luna.jpg", "Today", Icons.Outlined.Image, SecondaryGreen)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentsScreen(navController: NavController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text("Documents", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 8.dp))
        }
        items(sampleDocs) { d ->
            val tint = colorFor(documentCategoryFor(d.name))
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Icon(d.icon, contentDescription = null, tint = tint)
                    Text(d.name, style = MaterialTheme.typography.titleMedium)
                    Text(d.date, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
