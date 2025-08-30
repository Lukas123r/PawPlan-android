package de.lshorizon.pawplan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
// import androidx.activity.enableEdgeToEdge // Vorerst auskommentiert, kann zu Problemen mit Splash Screen führen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import de.lshorizon.pawplan.navigation.PawPlanNavHost // Importiere PawPlanNavHost
import de.lshorizon.pawplan.ui.theme.PawPlanTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen() // Splash Screen installieren
        // enableEdgeToEdge() // Vorerst auskommentiert, kann zu Problemen mit Splash Screen führen, prüfen wir später
        setContent {
            PawPlanTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PawPlanNavHost() // PawPlanNavHost als Startpunkt der UI setzen
                }
            }
        }
    }
}
