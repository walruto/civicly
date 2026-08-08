package com.example.civicly.ui.ordinances

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.example.civicly.ui.theme.DeepCobalt
import com.example.civicly.ui.theme.OnSurfaceVariant
import com.example.civicly.ui.theme.OutlineVariant
import com.example.civicly.ui.theme.SlateNavy

data class Ordinance(
    val number: String,
    val topic: String,
    val adopted: String,
    val area: String,
    val summary: String,
    val sourceUrl: String,
)

private val ORDINANCES = listOf(
    Ordinance(
        "2026-14", "Fireworks Ban", "2026-06-02", "Unincorporated Alameda County",
        "Prohibits the use of fireworks in unincorporated Alameda County.",
        "https://library.municode.com/ca/alameda_county/codes/code_of_ordinances",
    ),
    Ordinance(
        "2025-9", "Just Cause Evictions", "2025-02-04", "Unincorporated Alameda County",
        "Adds just-cause eviction protections for covered rental units, including notice, relocation assistance, and penalties.",
        "https://library.municode.com/ca/Alameda_County/ordinances/code_of_ordinances?nodeId=1348520",
    ),
    Ordinance(
        "2025-63", "Sidewalk Vendors", "2025-11-13", "Alameda County",
        "Updates county rules regulating sidewalk vendors and related ordinance-code sections.",
        "https://library.municode.com/ca/alameda_county/codes/code_of_ordinances",
    ),
    Ordinance(
        "2025-37", "Fire Severity Map", "2025-06-17", "Alameda County",
        "Replaces the county's previous fire-severity-map ordinance with an updated Chapter 6.05.",
        "https://library.municode.com/ca/alameda_county/ordinances/code_of_ordinances?nodeId=2025",
    ),
)

@Composable
fun OrdinancesScreen(onBack: () -> Unit = {}) {
    val uriHandler = LocalUriHandler.current
    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item { OrdinancesTopBar(onBack) }
            items(ORDINANCES, key = { it.number }) { ord ->
                OrdinanceCard(ord, onViewSource = { uriHandler.openUri(ord.sourceUrl) })
            }
        }
    }
}

@Composable
private fun OrdinancesTopBar(onBack: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(Color(0xCCF7F9FB))
            .statusBarsPadding()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = SlateNavy)
        }
        Spacer(Modifier.width(4.dp))
        Icon(Icons.Filled.Gavel, contentDescription = null, tint = SlateNavy, modifier = Modifier.size(28.dp))
        Spacer(Modifier.width(12.dp))
        Text("Ordinances", style = MaterialTheme.typography.displayLarge, color = SlateNavy)
    }
}

@Composable
private fun OrdinanceCard(ordinance: Ordinance, onViewSource: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.25f)),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                "Ord. ${ordinance.number} — ${ordinance.topic}",
                style = MaterialTheme.typography.titleMedium,
                color = SlateNavy,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetaChip(Icons.Filled.CalendarToday, "Adopted ${ordinance.adopted}")
                MetaChip(Icons.Filled.Place, ordinance.area)
            }
            Text(
                ordinance.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant,
            )
            Row(
                Modifier
                    .clickable(onClick = onViewSource)
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "View source",
                    style = MaterialTheme.typography.labelLarge,
                    color = DeepCobalt,
                )
                Spacer(Modifier.width(6.dp))
                Icon(
                    Icons.AutoMirrored.Filled.OpenInNew,
                    contentDescription = null,
                    tint = DeepCobalt,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

@Composable
private fun MetaChip(icon: ImageVector, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(14.dp))
        Spacer(Modifier.width(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
    }
}
