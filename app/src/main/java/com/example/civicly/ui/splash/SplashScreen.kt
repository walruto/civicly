package com.example.civicly.ui.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.civicly.R
import com.example.civicly.ui.theme.OnSurfaceVariant
import com.example.civicly.ui.theme.OutlineVariant
import com.example.civicly.ui.theme.SlateNavy
import com.example.civicly.ui.theme.SlateNavyDeep
import kotlinx.coroutines.delay

private const val SPLASH_DELAY_MS = 2000L

@Composable
fun SplashScreen(onGetStarted: () -> Unit) {
    var reveal by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(SPLASH_DELAY_MS)
        reveal = true
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        Image(
            painter = painterResource(R.drawable.civicly_logo),
            contentDescription = "Civicly",
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.6f),
        )
        AnimatedVisibility(
            visible = reveal,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 }),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            OnboardingPanel(onGetStarted)
        }
    }
}

@Composable
private fun OnboardingPanel(onGetStarted: () -> Unit) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        shadowElevation = 32.dp,
        tonalElevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 32.dp,
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                clip = false,
                ambientColor = Color.Black.copy(alpha = 0.4f),
                spotColor = Color.Black.copy(alpha = 0.4f),
            ),
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 40.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(R.drawable.civicly_logo),
                contentDescription = "Civicly",
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(56.dp),
            )
            Spacer(Modifier.height(24.dp))
            Text(
                "Civic Engagement,\nSimplified.",
                style = MaterialTheme.typography.headlineMedium,
                color = SlateNavy,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Stay informed, connect with your district, and make your voice heard in your local community.",
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = onGetStarted,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SlateNavyDeep,
                    contentColor = Color.White,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
            ) {
                Text("Get Started", style = MaterialTheme.typography.labelLarge)
            }
            Spacer(Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                PageDot(active = true)
                PageDot(active = false)
                PageDot(active = false)
            }
        }
    }
}

@Composable
private fun PageDot(active: Boolean) {
    Box(
        Modifier
            .width(24.dp)
            .height(4.dp)
            .clip(CircleShape)
            .background(if (active) SlateNavy else OutlineVariant.copy(alpha = 0.3f)),
    )
}
