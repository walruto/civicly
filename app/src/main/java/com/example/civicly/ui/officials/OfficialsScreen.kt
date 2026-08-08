package com.example.civicly.ui.officials

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.civicly.ui.theme.OnSurfaceVariant
import com.example.civicly.ui.theme.OutlineVariant
import com.example.civicly.ui.theme.SlateNavy

data class Official(
    val name: String,
    val office: String,
    val district: String,
    val term: String,
    val websiteUrl: String,
    val photoUrl: String?,
)

private val OFFICIALS = listOf(
    Official("Phong La", "Assessor", "Countywide", "2023-2028",
        "https://www.acassessor.org/about-us/meet-the-assessor/", "https://acgov.org/img/LA.png"),
    Official("Melissa Wilk", "Auditor/Controller", "Countywide", "2023-2028",
        "https://auditor.alamedacountyca.gov/", "https://acgov.org/img/WILK.jpg"),
    Official("David Haubert", "Board of Supervisors", "District 1", "2024-2028",
        "https://district1.alamedacountyca.gov/", "https://acgov.org/img/HAUBERT.png"),
    Official("Elisa Márquez", "Board of Supervisors", "District 2", "2024-2026",
        "https://district2.alamedacountyca.gov/", "https://acgov.org/img/MARQUEZ.png"),
    Official("Lena Tam", "Board of Supervisors", "District 3", "2023-2026",
        "https://district3.alamedacountyca.gov/", null),
    Official("Nate Miley", "Board of Supervisors", "District 4", "2024-2028",
        "https://district4.alamedacountyca.gov/", "https://acgov.org/img/MILEY.png"),
    Official("Nikki Fortunato Bas", "Board of Supervisors", "District 5", "2024-2028",
        "https://district5.acgov.org/about/", null),
    Official("Ursula Jones Dickson", "District Attorney", "Countywide", "2025-2026",
        "https://da.alamedacountyca.gov/", null),
    Official("Yesenia Sanchez", "Sheriff/Coroner", "Countywide", "2023-2028",
        "https://www.alamedasheriff.gov/about-us/sheriff-bio", null),
    Official("Alysse Castro", "Superintendent of Schools", "Countywide", "2023-2026",
        "https://www.acoe.org/", "https://acgov.org/img/CASTRO.jpg"),
    Official("Henry C. Levy", "Treasurer/Tax Collector", "Countywide", "2023-2028",
        "https://treasurer.acgov.org/about-the-treasurer/", "https://acgov.org/img/LEVY.png"),
)

@Composable
fun OfficialsScreen(onBack: () -> Unit = {}) {
    val uriHandler = LocalUriHandler.current
    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item { OfficialsTopBar(onBack) }
            items(OFFICIALS, key = { it.name + it.office + it.district }) { official ->
                OfficialCard(official, onOpen = { uriHandler.openUri(official.websiteUrl) })
            }
        }
    }
}

@Composable
private fun OfficialsTopBar(onBack: () -> Unit) {
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
        Icon(Icons.Filled.Groups, contentDescription = null, tint = SlateNavy, modifier = Modifier.size(28.dp))
        Spacer(Modifier.width(12.dp))
        Text("Officials", style = MaterialTheme.typography.displayLarge, color = SlateNavy)
    }
}

@Composable
private fun OfficialCard(official: Official, onOpen: () -> Unit) {
    Surface(
        onClick = onOpen,
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.25f)),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OfficialAvatar(photoUrl = official.photoUrl, name = official.name)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.fillMaxWidth()) {
                Text(
                    official.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = SlateNavy,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    official.office,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SlateNavy,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    "${official.district} • Term ${official.term}",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun OfficialAvatar(photoUrl: String?, name: String) {
    val size = 64.dp
    Box(
        Modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        if (photoUrl != null) {
            AsyncImage(
                model = photoUrl,
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape),
            )
        } else {
            Icon(
                Icons.Filled.Person,
                contentDescription = null,
                tint = OnSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(32.dp),
            )
        }
    }
}
