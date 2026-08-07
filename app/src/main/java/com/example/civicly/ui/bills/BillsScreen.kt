package com.example.civicly.ui.bills

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.civicly.data.Bill

@Composable
fun BillsScreen(vm: BillsViewModel = viewModel()) {
    val state by vm.state.collectAsState()
    Box(Modifier.fillMaxSize()) {
        when (val s = state) {
            is UiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
            is UiState.Error -> ErrorRetry(s.message) { vm.load() }
            is UiState.Data -> {
                if (s.value.isEmpty()) {
                    Text("No bills found.", Modifier.align(Alignment.Center))
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(s.value, key = { it.id }) { BillCard(it) }
                    }
                }
            }
        }
    }
}

@Composable
private fun ErrorRetry(msg: String, onRetry: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Couldn't load. Tap retry.")
        Text(msg, style = MaterialTheme.typography.labelSmall)
        Spacer(Modifier.height(8.dp))
        Button(onClick = onRetry) { Text("Retry") }
    }
}

@Composable
private fun BillCard(bill: Bill) {
    var expanded by remember { mutableStateOf(false) }
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(bill.title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusBadge(bill.status)
                Spacer(Modifier.width(8.dp))
                bill.lastActionDate?.let {
                    Text(it.take(10), style = MaterialTheme.typography.labelSmall)
                }
            }
            AnimatedVisibility(expanded) {
                Text(
                    bill.summary ?: "No summary available.",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(status: String?) {
    val (label, color) = statusColor(status)
    Surface(
        color = color.copy(alpha = 0.15f),
        contentColor = color,
        shape = RoundedCornerShape(50),
    ) {
        Text(
            label,
            Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

// ponytail: keyword-scan classifier. Upgrade to a per-source status map if statuses diverge.
private fun statusColor(status: String?): Pair<String, Color> {
    val s = status?.lowercase() ?: return "Unknown" to Color(0xFF757575)
    return when {
        listOf("passed", "signed", "enacted", "chaptered").any { it in s } -> "Passed" to Color(0xFF2E7D32)
        listOf("vetoed", "failed", "dead").any { it in s } -> "Failed" to Color(0xFFC62828)
        listOf("introduced", "referred", "pending", "committee").any { it in s } -> "In progress" to Color(0xFFF9A825)
        else -> (status ?: "Unknown") to Color(0xFF546E7A)
    }
}
