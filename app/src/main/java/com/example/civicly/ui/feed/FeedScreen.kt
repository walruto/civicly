package com.example.civicly.ui.feed

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.civicly.R
import com.example.civicly.data.Bill
import com.example.civicly.ui.bills.BillsViewModel
import com.example.civicly.ui.bills.UiState
import com.example.civicly.ui.theme.BiasCenter
import com.example.civicly.ui.theme.BiasLeft
import com.example.civicly.ui.theme.BiasRight
import com.example.civicly.ui.theme.ChipActive
import com.example.civicly.ui.theme.OchreAmber
import com.example.civicly.ui.theme.OnSurfaceVariant
import com.example.civicly.ui.theme.OutlineVariant
import com.example.civicly.ui.theme.SlateNavy

// Feed pulls real bills from Supabase via BillsViewModel for the news list.
// Poll/Townhall cards below remain mock content — no matching table/UI wired yet.

private val CHIPS = listOf("All News", "Town Halls", "Zoning & Transit", "Public Safety")

private val FILTER_MENU_ITEMS = listOf(
    "Women's Rights",
    "Candidates",
    "Women's Health",
    "Bills Passed Recently",
    "Problems in District",
)

@Composable
fun FeedScreen(
    onOpenSearch: () -> Unit = {},
    onOpenArticle: (String?) -> Unit = {},
    onOpenProfile: () -> Unit = {},
    billsVm: BillsViewModel = viewModel(),
) {
    val billsState by billsVm.state.collectAsState()
    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 0.dp, bottom = 120.dp),
        ) {
            item { FeedHeader(onOpenProfile) }
            item { DateLocationBlock() }
            item { SearchBar(onOpenSearch) }
            item { ChipsRow() }
            item {
                Column(
                    Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    FilterMoreButton()
                    when (val s = billsState) {
                        is UiState.Data -> s.value.take(5).forEach { bill ->
                            NewsCard(bill) { onOpenArticle(bill.id) }
                        }
                        is UiState.Error -> NewsCardMessage("Couldn't load bills", s.message)
                        UiState.Loading -> NewsCardMessage("Loading bills…", "Pulling the latest measures from Supabase.")
                    }
                    PollCard()
                    AudioTownhallCard()
                }
            }
        }
        FloatingPillNav(
            modifier = Modifier.align(Alignment.BottomCenter),
            onOpenSearch = onOpenSearch,
            onOpenArticle = { onOpenArticle(null) },
            onOpenProfile = onOpenProfile,
        )
    }
}

@Composable
private fun FeedHeader(onOpenProfile: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(Color(0xCCF3F4F6))
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Image(
            painter = painterResource(R.drawable.civicly_logo),
            contentDescription = "Civicly",
            modifier = Modifier.height(36.dp),
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box {
                Surface(
                    onClick = {},
                    shape = CircleShape,
                    color = Color.Transparent,
                    modifier = Modifier.size(40.dp),
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            Icons.Filled.Notifications,
                            contentDescription = "Notifications",
                            tint = OnSurfaceVariant,
                        )
                    }
                }
                Box(
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 8.dp, end = 8.dp)
                        .size(8.dp)
                        .background(OchreAmber, CircleShape),
                )
            }
            Spacer(Modifier.width(8.dp))
            Box(
                Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable(onClick = onOpenProfile),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.Person, contentDescription = "Profile", tint = OnSurfaceVariant)
            }
        }
    }
}

@Composable
private fun DateLocationBlock() {
    Column(
        Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text("April 24", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.LocationOn, contentDescription = null, tint = SlateNavy, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(6.dp))
            Text(
                "District 4, Seattle",
                style = MaterialTheme.typography.headlineSmall,
                color = SlateNavy,
            )
        }
    }
}

@Composable
private fun SearchBar(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .height(52.dp),
        shadowElevation = 2.dp,
    ) {
        Row(
            Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Filled.Search, contentDescription = null, tint = OnSurfaceVariant)
            Spacer(Modifier.width(12.dp))
            Text(
                "Search ordinances, officials, or news...",
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant.copy(alpha = 0.7f),
            )
        }
    }
}

@Composable
private fun ChipsRow() {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(CHIPS) { label ->
            val active = label == "All News"
            Chip(label, active)
        }
    }
}

@Composable
private fun Chip(label: String, active: Boolean) {
    Surface(
        onClick = {},
        shape = CircleShape,
        color = if (active) ChipActive else Color.White,
        contentColor = if (active) Color.White else SlateNavy,
        shadowElevation = 2.dp,
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
        )
    }
}

@Composable
private fun FilterMoreButton() {
    var open by remember { mutableStateOf(false) }
    Box {
        Surface(
            onClick = { open = true },
            shape = CircleShape,
            color = Color.White,
            border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.2f)),
            shadowElevation = 1.dp,
            modifier = Modifier.size(40.dp),
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(Icons.Filled.MoreHoriz, contentDescription = "Filters", tint = SlateNavy)
            }
        }
        DropdownMenu(
            expanded = open,
            onDismissRequest = { open = false },
            containerColor = Color.White.copy(alpha = 0.95f),
            shape = RoundedCornerShape(16.dp),
        ) {
            FILTER_MENU_ITEMS.forEach { label ->
                DropdownMenuItem(
                    text = { Text(label, style = MaterialTheme.typography.bodyMedium, color = SlateNavy) },
                    onClick = { open = false },
                )
            }
        }
    }
}

@Composable
private fun NewsCard(bill: Bill, onOpenArticle: () -> Unit) {
    Surface(
        onClick = onOpenArticle,
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 4.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                if (bill.imageUrl != null) {
                    AsyncImage(
                        model = bill.imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(16.dp)),
                    )
                } else {
                    ImagePlaceholder(size = 100.dp, corner = 16.dp)
                }
                Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = OnSurfaceVariant,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            listOfNotNull(bill.city, bill.county).joinToString(", ").ifBlank { "Statewide" },
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant,
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        bill.officialTitle ?: bill.id,
                        style = MaterialTheme.typography.titleMedium,
                        color = SlateNavy,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            // Bias split isn't tracked per-bill yet — this stays a placeholder until that data exists.
            BiasStack(l = 40, c = 50, r = 10)
        }
    }
}

@Composable
private fun NewsCardMessage(title: String, body: String) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 4.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = SlateNavy)
            Text(body, style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
        }
    }
}

@Composable
private fun PollCard() {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 4.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Civic Poll", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                Icon(Icons.Filled.MoreVert, contentDescription = null, tint = OnSurfaceVariant)
            }
            Spacer(Modifier.height(12.dp))
            Text(
                "Ordinance 204B: Public Park Hours Extension",
                style = MaterialTheme.typography.headlineSmall,
                color = SlateNavy,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "Should city parks remain open until 11:00 PM during summer months to accommodate late-shift workers?",
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant,
            )
            Spacer(Modifier.height(20.dp))
            PollBar(label = "Agree", pct = 62, filled = true)
            Spacer(Modifier.height(12.dp))
            PollBar(label = "Disagree", pct = 38, filled = false)
            Spacer(Modifier.height(20.dp))
            BiasStack(l = 80, c = 15, r = 5)
        }
    }
}

@Composable
private fun PollBar(label: String, pct: Int, filled: Boolean) {
    val trackColor = if (filled) BiasLeft.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant
    val fillColor = if (filled) BiasLeft else OutlineVariant.copy(alpha = 0.5f)
    val textColor = if (filled) Color.White else SlateNavy
    Box(
        Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(trackColor),
    ) {
        Box(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth(pct / 100f)
                .background(fillColor),
        )
        Row(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium, color = textColor)
            Text("$pct%", style = MaterialTheme.typography.bodyMedium, color = textColor)
        }
    }
}

@Composable
private fun BiasStack(l: Int, c: Int, r: Int) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        BiasSegment("L $l%", BiasLeft, Modifier.weight(1f))
        BiasSegment("C $c%", BiasCenter, Modifier.weight(1f))
        BiasSegment("R $r%", BiasRight, Modifier.weight(1f))
    }
}

@Composable
private fun BiasSegment(label: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier
            .height(28.dp)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = Color.White)
    }
}

@Composable
private fun AudioTownhallCard() {
    Surface(
        onClick = {},
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 4.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Townhall Recap", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                Text("Yesterday", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant.copy(alpha = 0.7f))
            }
            Spacer(Modifier.height(16.dp))
            Text(
                "District 4 Monthly Q&A: Public Safety Strategies",
                style = MaterialTheme.typography.titleMedium,
                color = SlateNavy,
            )
            Spacer(Modifier.height(16.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF8F9FA))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    onClick = {},
                    shape = CircleShape,
                    color = ChipActive,
                    contentColor = Color.White,
                    modifier = Modifier.size(48.dp),
                    shadowElevation = 3.dp,
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = "Play")
                    }
                }
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape)
                            .background(OutlineVariant.copy(alpha = 0.4f)),
                    ) {
                        Box(
                            Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(0.33f)
                                .background(ChipActive, CircleShape),
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text("12:04", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                        Text("-45:21", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun FloatingPillNav(
    modifier: Modifier = Modifier,
    onOpenSearch: () -> Unit,
    onOpenArticle: () -> Unit,
    onOpenProfile: () -> Unit,
) {
    Surface(
        modifier = modifier
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .fillMaxWidth()
            .height(64.dp),
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.85f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
        shadowElevation = 12.dp,
    ) {
        Row(
            Modifier.fillMaxSize().padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            NavIcon(Icons.Filled.Home, selected = true, onClick = {})
            NavIcon(Icons.Filled.Search, selected = false, onClick = onOpenSearch)
            NavIcon(Icons.AutoMirrored.Filled.Article, selected = false, onClick = onOpenArticle)
            NavIcon(Icons.Filled.Person, selected = false, onClick = onOpenProfile)
        }
    }
}

@Composable
private fun NavIcon(icon: ImageVector, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = if (selected) SlateNavy else Color.Transparent,
        contentColor = if (selected) Color.White else SlateNavy.copy(alpha = 0.5f),
        shape = CircleShape,
        shadowElevation = if (selected) 4.dp else 0.dp,
        modifier = Modifier.size(if (selected) 48.dp else 44.dp),
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Icon(icon, contentDescription = null)
        }
    }
}

@Composable
private fun ImagePlaceholder(size: androidx.compose.ui.unit.Dp, corner: androidx.compose.ui.unit.Dp) {
    Box(
        Modifier
            .size(size)
            .clip(RoundedCornerShape(corner))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        Icon(Icons.Filled.LocationOn, contentDescription = null, tint = OnSurfaceVariant.copy(alpha = 0.4f))
    }
}
