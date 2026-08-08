package com.example.civicly.ui.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

// ponytail: static demo data + fake translucency (no real GPU blur).
// Wire trending/recents to a VM and add RenderEffect blur when Android 12+ policy allows.
private val TRENDING = listOf("#ZoningReform", "#TransitExtension", "#ParkRenewal", "#CityBudget")

private data class Category(val label: String, val icon: ImageVector, val tint: Color, val fg: Color)

@Composable
fun SearchScreen() {
    var query by remember { mutableStateOf("") }
    val recents = remember { mutableStateListOf("City council meeting minutes", "Waste collection schedule") }
    val scheme = MaterialTheme.colorScheme
    val categories = listOf(
        Category("Ordinances", Icons.Filled.Gavel, scheme.primary, scheme.onPrimary),
        Category("Officials", Icons.Filled.Groups, Color(0xFFFFDCC3), Color(0xFF2F1500)),
        Category("News", Icons.AutoMirrored.Filled.Article, Color(0xFFD8E3FB), Color(0xFF111C2D)),
        Category("Events", Icons.Filled.Event, scheme.surfaceVariant, scheme.onSurfaceVariant),
    )

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        item { GlassSearchField(query, { query = it }) }
        item { Section("Trending Topics") { TrendingChips() } }
        item {
            Section(
                title = "Recent Searches",
                trailing = { if (recents.isNotEmpty()) TextButton(onClick = { recents.clear() }) { Text("Clear all") } },
            ) {
                Column {
                    recents.forEach { term ->
                        RecentRow(term) { recents.remove(term) }
                    }
                }
            }
        }
        item { Section("Explore Categories") { CategoryGrid(categories) } }
    }
}

@Composable
private fun Section(
    title: String,
    trailing: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Column {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(title, style = MaterialTheme.typography.headlineSmall)
            trailing?.invoke()
        }
        Spacer(Modifier.height(12.dp))
        content()
    }
}

@Composable
private fun GlassSearchField(value: String, onChange: (String) -> Unit) {
    val scheme = MaterialTheme.colorScheme
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Search ordinances, officials, or news...") },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = scheme.outline) },
        singleLine = true,
        shape = RoundedCornerShape(50),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color.White.copy(alpha = 0.5f),
            focusedContainerColor = Color.White.copy(alpha = 0.8f),
            unfocusedBorderColor = scheme.outlineVariant.copy(alpha = 0.4f),
            focusedBorderColor = scheme.primary.copy(alpha = 0.5f),
        ),
    )
}

@Composable
private fun TrendingChips() {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(TRENDING) { tag -> GlassChip(tag) }
    }
}

@Composable
private fun GlassChip(label: String) {
    val scheme = MaterialTheme.colorScheme
    Surface(
        shape = RoundedCornerShape(50),
        color = Color.White.copy(alpha = 0.5f),
        contentColor = scheme.onSurfaceVariant,
        border = BorderStroke(1.dp, scheme.outlineVariant.copy(alpha = 0.5f)),
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
    }
}

@Composable
private fun RecentRow(term: String, onRemove: () -> Unit) {
    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable {}
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Filled.History,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.size(12.dp))
            Text(
                term,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            IconButton(onClick = onRemove) {
                Icon(Icons.Filled.Close, contentDescription = "Remove", tint = MaterialTheme.colorScheme.outline)
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
    }
}

// ponytail: two static rows beat LazyVerticalGrid + a scroll-container-in-container dance for 4 fixed cells.
@Composable
private fun CategoryGrid(items: List<Category>) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        items.chunked(2).forEach { pair ->
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                pair.forEach { cat ->
                    Box(Modifier.weight(1f)) { CategoryCard(cat) }
                }
            }
        }
    }
}

@Composable
private fun CategoryCard(cat: Category) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                Modifier
                    .size(48.dp)
                    .background(cat.tint, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(cat.icon, contentDescription = null, tint = cat.fg)
            }
            Spacer(Modifier.height(12.dp))
            Text(
                cat.label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
