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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.civicly.data.Bill
import com.example.civicly.data.NewsArticle
import com.example.civicly.ui.bills.BillsViewModel
import com.example.civicly.ui.bills.UiState
import com.example.civicly.ui.news.NewsItem
import com.example.civicly.ui.news.NewsViewModel
import com.example.civicly.ui.theme.OnSurfaceVariant
import com.example.civicly.ui.theme.OutlineVariant
import com.example.civicly.ui.theme.SlateNavy

private val TRENDING = listOf("#ZoningReform", "#TransitExtension", "#ParkRenewal", "#CityBudget")

private data class Category(val label: String, val icon: ImageVector, val tint: Color, val fg: Color)

private sealed interface Hit {
    val id: String
    val title: String
    val subtitle: String
    val kind: String
    data class BillHit(val bill: Bill) : Hit {
        override val id = bill.id
        override val title = bill.officialTitle ?: bill.id
        override val subtitle = bill.plainSummary
            ?: listOfNotNull(bill.city, bill.county).joinToString(", ").ifBlank { "Bill" }
        override val kind = "Bill"
    }
    data class ArticleHit(val article: NewsArticle) : Hit {
        override val id = article.id
        override val title = article.title
        override val subtitle = article.summary ?: article.sourceName ?: "News"
        override val kind = "News"
    }
}

@Composable
fun SearchScreen(
    onOpenFeed: () -> Unit = {},
    onOpenArticle: (String?) -> Unit = {},
    onOpenProfile: () -> Unit = {},
    onOpenOrdinances: () -> Unit = {},
    onOpenOfficials: () -> Unit = {},
    onOpenNews: () -> Unit = {},
    onOpenEvents: () -> Unit = {},
    billsVm: BillsViewModel = viewModel(),
    newsVm: NewsViewModel = viewModel(),
) {
    val billsState by billsVm.state.collectAsState()
    val newsState by newsVm.state.collectAsState()
    var query by remember { mutableStateOf("") }
    val recents = remember { mutableStateListOf("City council meeting minutes", "Waste collection schedule") }

    val trimmed = query.trim()
    val hits = remember(trimmed, billsState, newsState) {
        if (trimmed.isEmpty()) emptyList() else searchAll(trimmed, billsState, newsState)
    }
    val loading = trimmed.isNotEmpty() &&
        (billsState is UiState.Loading || newsState is UiState.Loading)

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 112.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            item { SearchHeader(onOpenProfile) }
            item {
                GlassSearchField(
                    query = query,
                    onQueryChange = { query = it },
                    onSubmit = {
                        val q = query.trim()
                        if (q.isNotEmpty()) {
                            recents.remove(q)
                            recents.add(0, q)
                            while (recents.size > 8) recents.removeAt(recents.lastIndex)
                        }
                    },
                    onClear = { query = "" },
                )
            }
            if (trimmed.isEmpty()) {
                item { Section("Trending Topics") { TrendingChips(onPick = { query = it.removePrefix("#") }) } }
                item { RecentSearches(recents = recents, onPick = { query = it }) }
                item {
                    Section("Explore Categories") {
                        CategoryGrid(categories(), onPick = { label ->
                            when (label) {
                                "Ordinances" -> onOpenOrdinances()
                                "Officials" -> onOpenOfficials()
                                "News" -> onOpenNews()
                                "Events" -> onOpenEvents()
                            }
                        })
                    }
                }
            } else {
                when {
                    loading -> item { ResultMessage("Searching…", "Fetching bills and news.") }
                    hits.isEmpty() -> item { ResultMessage("No matches", "Nothing found for \"$trimmed\".") }
                    else -> {
                        item {
                            Text(
                                "${hits.size} result${if (hits.size == 1) "" else "s"} for \"$trimmed\"",
                                style = MaterialTheme.typography.labelMedium,
                                color = OnSurfaceVariant,
                            )
                        }
                        items(hits, key = { it.kind + ":" + it.id }) { hit ->
                            ResultCard(hit, onClick = { onOpenArticle(hit.id) })
                        }
                    }
                }
            }
        }
        SearchBottomNav(
            modifier = Modifier.align(Alignment.BottomCenter),
            onOpenFeed = onOpenFeed,
            onOpenArticle = { onOpenArticle(null) },
            onOpenProfile = onOpenProfile,
        )
    }
}

private fun searchAll(
    q: String,
    billsState: UiState<List<Bill>>,
    newsState: UiState<List<NewsItem>>,
): List<Hit> {
    val bills = (billsState as? UiState.Data)?.value.orEmpty()
    val news = (newsState as? UiState.Data)?.value.orEmpty()
    val billHits = bills.filter { b ->
        b.matches(q)
    }.map { Hit.BillHit(it) }
    val articleHits = news.filter { it.article.matches(q) }.map { Hit.ArticleHit(it.article) }
    return billHits + articleHits
}

private fun Bill.matches(q: String): Boolean {
    val fields = listOfNotNull(
        officialTitle, plainSummary, id, city, county, billType, jurisdictionLevel,
    ) + (topicTags.orEmpty())
    return fields.any { it.contains(q, ignoreCase = true) }
}

private fun NewsArticle.matches(q: String): Boolean {
    val fields = listOfNotNull(title, summary, sourceName)
    return fields.any { it.contains(q, ignoreCase = true) }
}

@Composable
private fun SearchHeader(onOpenProfile: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(Color(0xCCF7F9FB))
            .statusBarsPadding()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Search, contentDescription = null, tint = SlateNavy, modifier = Modifier.size(32.dp))
            Spacer(Modifier.width(20.dp))
            Text("Search", style = MaterialTheme.typography.displayLarge, color = SlateNavy)
        }
        Surface(
            onClick = onOpenProfile,
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.size(52.dp),
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(Icons.Filled.Person, contentDescription = "Profile", tint = OnSurfaceVariant)
            }
        }
    }
}

@Composable
private fun GlassSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onClear: () -> Unit,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        placeholder = { Text("Search ordinances, officials, or news...") },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = MaterialTheme.colorScheme.outline) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(Icons.Filled.Close, contentDescription = "Clear", tint = OnSurfaceVariant)
                }
            }
        },
        singleLine = true,
        shape = CircleShape,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSubmit() }),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color.White.copy(alpha = 0.55f),
            focusedContainerColor = Color.White.copy(alpha = 0.85f),
            unfocusedBorderColor = OutlineVariant.copy(alpha = 0.35f),
            focusedBorderColor = SlateNavy.copy(alpha = 0.5f),
        ),
    )
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
            Text(title, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface)
            trailing?.invoke()
        }
        Spacer(Modifier.height(12.dp))
        content()
    }
}

@Composable
private fun TrendingChips(onPick: (String) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(TRENDING) { tag -> TopicChip(tag, onClick = { onPick(tag) }) }
    }
}

@Composable
private fun TopicChip(label: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.55f),
        border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.5f)),
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = OnSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
        )
    }
}

@Composable
private fun RecentSearches(recents: MutableList<String>, onPick: (String) -> Unit) {
    Section(
        title = "Recent Searches",
        trailing = {
            if (recents.isNotEmpty()) {
                TextButton(onClick = { recents.clear() }) {
                    Text("Clear all", color = SlateNavy)
                }
            }
        },
    ) {
        Column {
            recents.forEach { term ->
                RecentRow(term, onClick = { onPick(term) }, onRemove = { recents.remove(term) })
            }
        }
    }
}

@Composable
private fun RecentRow(term: String, onClick: () -> Unit, onRemove: () -> Unit) {
    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Filled.History, contentDescription = null, tint = OutlineVariant, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(16.dp))
            Text(
                term,
                style = MaterialTheme.typography.bodyLarge,
                color = OnSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            IconButton(onClick = onRemove) {
                Icon(Icons.Filled.Close, contentDescription = "Remove", tint = OutlineVariant)
            }
        }
        HorizontalDivider(color = OutlineVariant.copy(alpha = 0.25f))
    }
}

@Composable
private fun categories() = listOf(
    Category("Ordinances", Icons.Filled.Gavel, SlateNavy, Color.White),
    Category("Officials", Icons.Filled.Groups, Color(0xFFFFDCC3), Color(0xFF2F1500)),
    Category("News", Icons.AutoMirrored.Filled.Article, Color(0xFFD8E3FB), Color(0xFF111C2D)),
    Category("Events", Icons.Filled.Event, MaterialTheme.colorScheme.surfaceVariant, OnSurfaceVariant),
)

// ponytail: fixed 2x2 grid; LazyVerticalGrid is extra machinery for four static cards.
@Composable
private fun CategoryGrid(items: List<Category>, onPick: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        items.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                row.forEach { category ->
                    Box(Modifier.weight(1f)) {
                        CategoryCard(category, onClick = { onPick(category.label) })
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryCard(category: Category, onClick: () -> Unit) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                Modifier
                    .size(56.dp)
                    .background(category.tint, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(category.icon, contentDescription = null, tint = category.fg)
            }
            Spacer(Modifier.height(16.dp))
            Text(category.label, style = MaterialTheme.typography.titleSmall, color = Color.Black)
        }
    }
}

@Composable
private fun ResultCard(hit: Hit, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(hit.kind, style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                Icon(
                    if (hit is Hit.BillHit) Icons.Filled.Gavel else Icons.AutoMirrored.Filled.Article,
                    contentDescription = null,
                    tint = OnSurfaceVariant,
                    modifier = Modifier.size(16.dp),
                )
            }
            Text(
                hit.title,
                style = MaterialTheme.typography.titleMedium,
                color = SlateNavy,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                hit.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun ResultMessage(title: String, body: String) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = SlateNavy)
            Text(body, style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
        }
    }
}

@Composable
private fun SearchBottomNav(
    modifier: Modifier = Modifier,
    onOpenFeed: () -> Unit,
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
            NavIcon(Icons.Filled.Home, selected = false, onClick = onOpenFeed)
            NavIcon(Icons.Filled.Search, selected = true, onClick = {})
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
