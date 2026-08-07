package com.example.civicly.ui.match

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.civicly.ui.bills.UiState

@Composable
fun MatchResultsScreen(vm: MatchViewModel, onRetake: () -> Unit) {
    val state by vm.candidates.collectAsState()
    val answers by vm.answers.collectAsState()

    LaunchedEffect(Unit) {
        if (state !is UiState.Data) vm.loadCandidates()
    }

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
                Text("Couldn't load candidates.")
                Text(s.message, style = MaterialTheme.typography.labelSmall)
                Spacer(Modifier.height(8.dp))
                Button(onClick = { vm.loadCandidates() }) { Text("Retry") }
            }
            is UiState.Data -> {
                val results = remember(s.value, answers) { vm.computeMatches(s.value) }
                LazyColumn(
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    item {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("Your matches", style = MaterialTheme.typography.titleMedium)
                            TextButton(onClick = onRetake) { Text("Retake quiz") }
                        }
                    }
                    items(results, key = { it.candidate.id }) { ResultCard(it, vm.questions) }
                }
            }
        }
    }
}

@Composable
private fun ResultCard(result: MatchResult, questions: List<Question>) {
    var expanded by remember { mutableStateOf(false) }
    ElevatedCard(
        Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(result.candidate.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    listOfNotNull(result.candidate.office, result.candidate.party)
                        .joinToString(" • "),
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            MatchBadge(result.scorePct)
        }
        AnimatedVisibility(expanded) {
            Column(Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp)) {
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))
                Text("Why this match", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(4.dp))
                questions.forEach { q ->
                    val theirPos = result.candidate.positions?.get(q.issueKey) ?: "—"
                    val agree = q.issueKey in result.matchedIssues
                    Row(Modifier.padding(vertical = 2.dp)) {
                        Text(
                            if (agree) "✓ " else "✗ ",
                            color = if (agree) matchTeal else Color(0xFF9E9E9E),
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            "${q.issueKey.replaceFirstChar { it.uppercase() }}: ",
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(theirPos.replace('_', ' '), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun MatchBadge(pct: Int) {
    val color = matchColor(pct)
    Surface(
        color = color.copy(alpha = 0.15f),
        contentColor = color,
        shape = RoundedCornerShape(50),
    ) {
        Text(
            "$pct%",
            Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

// Teal scale, deliberately non-partisan.
private val matchTeal = Color(0xFF00897B)
private fun matchColor(pct: Int): Color = when {
    pct >= 80 -> Color(0xFF00695C)
    pct >= 60 -> Color(0xFF00897B)
    pct >= 40 -> Color(0xFF26A69A)
    else -> Color(0xFF9E9E9E)
}
