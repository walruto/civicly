package com.example.civicly.ui.news

import android.content.Intent
import androidx.core.net.toUri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.civicly.ui.bills.UiState

@Composable
fun NewsScreen(vm: NewsViewModel = viewModel()) {
    val state by vm.state.collectAsState()
    val ctx = LocalContext.current
    Box(Modifier.fillMaxSize()) {
        when (val s = state) {
            is UiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
            is UiState.Error -> Column(
                Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("Couldn't load. Tap retry.")
                Text(s.message, style = MaterialTheme.typography.labelSmall)
                Spacer(Modifier.height(8.dp))
                Button(onClick = { vm.load() }) { Text("Retry") }
            }
            is UiState.Data -> {
                if (s.value.isEmpty()) {
                    Text("No articles found.", Modifier.align(Alignment.Center))
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(s.value, key = { it.article.id }) { item ->
                            ArticleCard(item) {
                                ctx.startActivity(
                                    Intent(Intent.ACTION_VIEW, item.article.url.toUri())
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ArticleCard(item: NewsItem, onOpen: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() },
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(item.article.title, style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(item.article.sourceDomain, style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.width(8.dp))
                BiasChip(item.bias?.lean)
            }
        }
    }
}

@Composable
private fun BiasChip(lean: String?) {
    val (label, color) = biasColor(lean)
    Surface(
        color = color.copy(alpha = 0.18f),
        contentColor = color,
        shape = RoundedCornerShape(50),
    ) {
        Text(
            label,
            Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

// Indigo -> violet -> magenta scale, deliberately not literal red/blue party colors.
private fun biasColor(lean: String?): Pair<String, Color> = when (lean?.lowercase()) {
    "left" -> "Left" to Color(0xFF283593)
    "lean-left", "lean_left", "leanleft" -> "Lean Left" to Color(0xFF5C6BC0)
    "center" -> "Center" to Color(0xFF7E57C2)
    "lean-right", "lean_right", "leanright" -> "Lean Right" to Color(0xFFAD1457)
    "right" -> "Right" to Color(0xFF880E4F)
    else -> "Unrated" to Color(0xFF757575)
}
