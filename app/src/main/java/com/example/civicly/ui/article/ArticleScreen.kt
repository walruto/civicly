package com.example.civicly.ui.article

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.civicly.data.Bill
import com.example.civicly.data.NewsArticle
import com.example.civicly.data.SupabaseClient
import com.example.civicly.data.toUserMessage
import com.example.civicly.ui.bills.UiState
import com.example.civicly.ui.theme.DeepCobalt
import com.example.civicly.ui.theme.OchreAmber
import com.example.civicly.ui.theme.OnSurfaceVariant
import com.example.civicly.ui.theme.OutlineVariant
import com.example.civicly.ui.theme.SlateNavy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ArticleContent(
    val id: String,
    val title: String,
    val sourceName: String,
    val date: String?,
    val category: String,
    val summary: String?,
    val relatedMeasureId: String?,
    val url: String?,
    val isPending: Boolean = false,
)

class ArticleViewModel(private val itemId: String? = null) : ViewModel() {
    private val _state = MutableStateFlow<UiState<ArticleContent>>(UiState.Loading)
    val state: StateFlow<UiState<ArticleContent>> = _state.asStateFlow()

    init { load() }

    fun load() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            _state.value = try {
                val idFilter = itemId?.let { "eq.$it" }
                // Try bills first (most content lives there), then articles, for the given id.
                // With no id (nav-bar shortcut, not a specific tap), fall back to "whatever's first".
                val bill = SupabaseClient.api.getBills(id = idFilter, limit = 1).firstOrNull()
                UiState.Data(
                    bill?.toArticleContent()
                        ?: SupabaseClient.api.getNewsArticles(id = idFilter, limit = 1).firstOrNull()?.toArticleContent()
                    ?: throw IllegalStateException(
                        if (itemId != null) "No bill or article found for id \"$itemId\"."
                        else "No articles or bills found in Supabase."
                    )
                )
            } catch (e: Exception) {
                UiState.Error(e.toUserMessage("Failed to load article"))
            }
        }
    }
}

@Composable
fun ArticleScreen(
    itemId: String? = null,
    onBack: () -> Unit = {},
    onOpenFeed: () -> Unit = {},
    onOpenSearch: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
) {
    // Keyed on itemId so tapping a different article/bill creates a fresh VM
    // that fetches that specific item, instead of reusing a stale loaded one.
    val vm = remember(itemId) { ArticleViewModel(itemId) }
    val state by vm.state.collectAsState()
    val article = when (val current = state) {
        is UiState.Data -> current.value
        else -> null
    }
    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 112.dp),
        ) {
            item {
                when {
                    article != null -> ArticleHero(article, onBack)
                    state is UiState.Error -> MessageHero("Article unavailable", (state as UiState.Error).message, onBack)
                    else -> MessageHero("Loading article", "Pulling the latest article from Supabase.", onBack)
                }
            }
            if (article != null) {
                item { ArticleActions(article) }
                item { ArticleBody(article) }
            }
        }
        ArticleBottomNav(
            modifier = Modifier.align(Alignment.BottomCenter),
            onOpenFeed = onOpenFeed,
            onOpenSearch = onOpenSearch,
            onOpenProfile = onOpenProfile,
        )
    }
}

@Composable
private fun ArticleHero(article: ArticleContent, onBack: () -> Unit) {
    val gradient = if (article.isPending) {
        listOf(Color(0xFF4A4636), Color(0xFF3A3728), Color(0xFF1E1C14))
    } else {
        listOf(Color(0xFF334155), SlateNavy, Color(0xFF091426))
    }
    Box(
        Modifier
            .fillMaxWidth()
            .height(360.dp)
            .background(Brush.verticalGradient(gradient)),
    ) {
        HeroButtons(onBack)
        Icon(
            if (article.isPending) Icons.Filled.Schedule else Icons.AutoMirrored.Filled.Article,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.16f),
            modifier = Modifier
                .align(Alignment.Center)
                .size(168.dp),
        )
        Column(
            Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp, 0.dp, 16.dp, 32.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    shape = CircleShape,
                    color = if (article.isPending) OchreAmber else DeepCobalt,
                    contentColor = Color.White,
                ) {
                    Text(
                        if (article.isPending) "PENDING" else article.category.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    )
                }
                Text(article.date.shortDate(), style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.75f))
            }
            Spacer(Modifier.height(12.dp))
            Text(
                article.title,
                style = MaterialTheme.typography.displayLarge,
                color = Color.White,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun MessageHero(title: String, body: String, onBack: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(360.dp)
            .background(SlateNavy),
    ) {
        HeroButtons(onBack)
        Column(
            Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp, 0.dp, 16.dp, 32.dp),
        ) {
            Text(title, style = MaterialTheme.typography.displayLarge, color = Color.White)
            Spacer(Modifier.height(8.dp))
            Text(body, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.75f))
        }
    }
}

@Composable
private fun HeroButtons(onBack: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        RoundHeroButton(Icons.AutoMirrored.Filled.ArrowBack, "Back", onBack)
        RoundHeroButton(Icons.Filled.Share, "Share", {})
    }
}

@Composable
private fun RoundHeroButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.25f),
        contentColor = Color.White,
        modifier = Modifier.size(44.dp),
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Icon(icon, contentDescription = label)
        }
    }
}

@Composable
private fun ArticleActions(article: ArticleContent) {
    Surface(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.88f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
        shadowElevation = 4.dp,
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(Modifier.weight(1f)) {
                Text(article.sourceName, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = SlateNavy)
                Text(article.url ?: "Supabase article id: ${article.id}", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ActionButton(Icons.Filled.Bookmark)
                ActionButton(Icons.Filled.ThumbUp, filled = true)
            }
        }
    }
}

@Composable
private fun ActionButton(icon: ImageVector, filled: Boolean = false) {
    Surface(
        onClick = {},
        shape = CircleShape,
        color = if (filled) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent,
        border = if (filled) null else BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.4f)),
        contentColor = if (filled) DeepCobalt else SlateNavy,
        modifier = Modifier.size(44.dp),
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Icon(icon, contentDescription = null)
        }
    }
}

@Composable
private fun ArticleBody(article: ArticleContent) {
    Surface(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.88f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
        shadowElevation = 4.dp,
    ) {
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            if (article.isPending) {
                PendingNotice(article)
            } else {
                Text("Overview", style = MaterialTheme.typography.headlineSmall, color = SlateNavy)
                article.summary.paragraphs().forEach {
                    Text(it, style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                }
            }
            article.relatedMeasureId?.let {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.25f)),
                ) {
                    Text(
                        "Related measure: $it",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = SlateNavy,
                        modifier = Modifier.padding(16.dp),
                    )
                }
            }
            article.url?.let { DetailRow("URL", it) }
            DetailRow("Supabase ID", article.id)
        }
    }
}

@Composable
private fun PendingNotice(article: ArticleContent) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = OchreAmber.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, OchreAmber.copy(alpha = 0.4f)),
    ) {
        Row(
            Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(Icons.Filled.Schedule, contentDescription = null, tint = OchreAmber)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    "Official text not published yet",
                    style = MaterialTheme.typography.titleMedium,
                    color = SlateNavy,
                )
                Text(
                    "This measure has qualified for the ballot, but the official summary hasn't been " +
                        "released yet." +
                        (article.url?.let { " Check the source below for the latest updates." } ?: ""),
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = SlateNavy)
        Text(value, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
    }
}

@Composable
private fun ArticleBottomNav(
    modifier: Modifier = Modifier,
    onOpenFeed: () -> Unit,
    onOpenSearch: () -> Unit,
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
            NavIcon(Icons.Filled.Search, selected = false, onClick = onOpenSearch)
            NavIcon(Icons.AutoMirrored.Filled.Article, selected = true, onClick = {})
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

private fun String?.shortDate() = this?.substringBefore('T') ?: "Latest"

private fun String?.paragraphs() = this
    ?.split(". ")
    ?.filter { it.isNotBlank() }
    ?.map { if (it.endsWith(".")) it else "$it." }
    ?.ifEmpty { listOf("No article summary is available yet.") }
    ?: listOf("No article summary is available yet.")

private fun NewsArticle.toArticleContent() = ArticleContent(
    id = id,
    title = title,
    sourceName = sourceName ?: "Civicly",
    date = publishedAt,
    category = relatedMeasureId ?: "Civic Update",
    summary = summary,
    relatedMeasureId = relatedMeasureId,
    url = url,
)

private fun Bill.toArticleContent(): ArticleContent {
    val pending = dataStatus?.contains("needs source", ignoreCase = true) == true ||
        plainSummary?.startsWith("Pending", ignoreCase = true) == true
    return ArticleContent(
        id = id,
        title = officialTitle ?: id,
        sourceName = listOfNotNull(city, county).joinToString(", ").ifBlank { "Civicly" },
        date = electionDate ?: lastVerified,
        category = topicTags?.firstOrNull() ?: billType ?: "Civic Update",
        // When pending, the individual fields are just repeats of "Pending" — skip
        // joining them into a garbled sentence and let ArticleBody show one clean message instead.
        summary = if (pending) null else listOfNotNull(plainSummary, fiscalImpactSummary, yesVoteMeans, noVoteMeans)
            .joinToString(" "),
        relatedMeasureId = id,
        url = sourceUrl,
        isPending = pending,
    )
}
