package de.lshorizon.pawplan.ui.screens.auth.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.lshorizon.pawplan.ui.theme.GoogleButtonBorder
import de.lshorizon.pawplan.ui.theme.GoogleButtonText

@Composable
fun GoogleSignInButton(
    onClick: () -> Unit,
    text: String = "Sign in with"
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        border = BorderStroke(1.dp, GoogleButtonBorder)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text, color = GoogleButtonText)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = androidx.compose.ui.text.buildAnnotatedString {
                    pushStyle(style = androidx.compose.ui.text.SpanStyle(color = androidx.compose.ui.graphics.Color(0xFF4285F4)))
                    append("G")
                    pop()
                    pushStyle(style = androidx.compose.ui.text.SpanStyle(color = androidx.compose.ui.graphics.Color(0xFFDB4437)))
                    append("o")
                    pop()
                    pushStyle(style = androidx.compose.ui.text.SpanStyle(color = androidx.compose.ui.graphics.Color(0xFFF4B400)))
                    append("o")
                    pop()
                    pushStyle(style = androidx.compose.ui.text.SpanStyle(color = androidx.compose.ui.graphics.Color(0xFF4285F4)))
                    append("g")
                    pop()
                    pushStyle(style = androidx.compose.ui.text.SpanStyle(color = androidx.compose.ui.graphics.Color(0xFF0F9D58)))
                    append("l")
                    pop()
                    pushStyle(style = androidx.compose.ui.text.SpanStyle(color = androidx.compose.ui.graphics.Color(0xFFDB4437)))
                    append("e")
                    pop()
                },
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
