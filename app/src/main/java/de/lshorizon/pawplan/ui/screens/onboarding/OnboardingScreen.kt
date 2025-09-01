package de.lshorizon.pawplan.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.lshorizon.pawplan.R
import de.lshorizon.pawplan.data.OnboardingRepository
import de.lshorizon.pawplan.ui.theme.AccentOrange
import kotlinx.coroutines.launch

private data class OnboardingPage(
    val title: String,
    val lines: List<String>,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val tint: Color
)

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val pages = listOf(
        OnboardingPage(
            title = stringResource(id = R.string.onb_welcome_title),
            lines = listOf(
                stringResource(id = R.string.onb_welcome_l1),
                stringResource(id = R.string.onb_welcome_l2)
            ),
            icon = Icons.Outlined.CheckCircle,
            tint = MaterialTheme.colorScheme.primary
        ),
        OnboardingPage(
            title = stringResource(id = R.string.onb_profiles_title),
            lines = listOf(
                stringResource(id = R.string.onb_profiles_l1),
                stringResource(id = R.string.onb_profiles_l2)
            ),
            icon = Icons.Outlined.Pets,
            tint = AccentOrange
        ),
        OnboardingPage(
            title = stringResource(id = R.string.onb_reminders_title),
            lines = listOf(
                stringResource(id = R.string.onb_reminders_l1),
                stringResource(id = R.string.onb_reminders_l2)
            ),
            icon = Icons.Outlined.Event,
            tint = MaterialTheme.colorScheme.tertiary
        ),
        OnboardingPage(
            title = stringResource(id = R.string.onb_docs_title),
            lines = listOf(
                stringResource(id = R.string.onb_docs_l1),
                stringResource(id = R.string.onb_docs_l2)
            ),
            icon = Icons.Outlined.Description,
            tint = MaterialTheme.colorScheme.primary
        ),
        OnboardingPage(
            title = stringResource(id = R.string.onb_get_started_title),
            lines = listOf(
                stringResource(id = R.string.onb_get_started_l1),
                stringResource(id = R.string.onb_get_started_l2)
            ),
            icon = Icons.Outlined.CheckCircle,
            tint = MaterialTheme.colorScheme.secondary
        )
    )

    val pagerState = rememberPagerState { pages.size }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 0.dp),
            modifier = Modifier.weight(1f)
        ) { page ->
            val p = pages[page]
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(p.icon, contentDescription = null, tint = p.tint, modifier = Modifier.size(72.dp))
                Text(p.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 16.dp))
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    p.lines.forEach { line ->
                        Text("• $line", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }
        }

        // Dots indicator
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            repeat(pages.size) { i ->
                val active = i == pagerState.currentPage
                Box(
                    modifier = Modifier
                        .size(if (active) 10.dp else 8.dp)
                        .clip(CircleShape)
                        .background(if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = {
                scope.launch {
                    OnboardingRepository(context).setOnboardingDone(true)
                    onFinish()
                }
            },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentOrange),
                border = BorderStroke(1.dp, AccentOrange)
            ) { Text(stringResource(id = R.string.onb_skip)) }

            val isLast = pagerState.currentPage == pages.lastIndex
            Button(
                onClick = {
                    scope.launch {
                        if (isLast) {
                            OnboardingRepository(context).setOnboardingDone(true)
                            onFinish()
                        } else {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentOrange)
            ) { Text(if (isLast) stringResource(id = R.string.onb_finish) else stringResource(id = R.string.onb_next)) }
        }
    }
}
