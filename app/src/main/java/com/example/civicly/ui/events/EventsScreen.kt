package com.example.civicly.ui.events

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
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocationOn
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

data class CivicEvent(
    val title: String,
    val dateTime: String,
    val location: String,
    val description: String,
    val sourceUrl: String,
)

private val EVENTS = listOf(
    CivicEvent(
        "Laurel StreetFair World Music Festival",
        "Saturday, Aug. 8, 2026 • 11:00 AM–7:00 PM",
        "MacArthur Blvd., between 35th Ave. and Maybelle Ave., Oakland",
        "Free, all-ages street festival with global music, food, artisan vendors, community groups, and activities across more than eight blocks.",
        "https://www.visitoakland.com/events/annual-events/laurel-streetfair/",
    ),
    CivicEvent(
        "Friday Nights at OMCA with Ashley Mehta",
        "Friday, Aug. 14, 2026 • 5:00–9:00 PM",
        "Oakland Museum of California, Oakland",
        "Free Friday-night community program featuring live music by Ashley Mehta, a BRIIZA DJ set, a locking dance lesson, food trucks, activities, and gallery chats. Museum gallery admission is separate.",
        "https://museumca.org/press/omca-announces-public-programs-and-events-for-august-2026/",
    ),
    CivicEvent(
        "Oakland Ballers vs. Missoula PaddleHeads",
        "Aug. 25–27, 2026",
        "Raimondi Park, 1800 Wood St., Oakland",
        "Three-game Oakland Ballers home series against the Missoula PaddleHeads, with a community-focused ballpark atmosphere and themed game nights.",
        "https://www.visitoakland.com/event/oakland-ballers-vs-missoula-paddleheads/34888/",
    ),
)

@Composable
fun EventsScreen(onBack: () -> Unit = {}) {
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
            item { EventsTopBar(onBack) }
            items(EVENTS, key = { it.sourceUrl }) { event ->
                EventCard(event, onViewSource = { uriHandler.openUri(event.sourceUrl) })
            }
        }
    }
}

@Composable
private fun EventsTopBar(onBack: () -> Unit) {
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
        Icon(Icons.Filled.Event, contentDescription = null, tint = SlateNavy, modifier = Modifier.size(28.dp))
        Spacer(Modifier.width(12.dp))
        Text("Events", style = MaterialTheme.typography.displayLarge, color = SlateNavy)
    }
}

@Composable
private fun EventCard(event: CivicEvent, onViewSource: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.25f)),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                event.title,
                style = MaterialTheme.typography.titleMedium,
                color = SlateNavy,
            )
            IconRow(Icons.Filled.CalendarToday, event.dateTime)
            IconRow(Icons.Filled.LocationOn, event.location)
            Text(
                event.description,
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
private fun IconRow(icon: ImageVector, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(14.dp))
        Spacer(Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
    }
}
