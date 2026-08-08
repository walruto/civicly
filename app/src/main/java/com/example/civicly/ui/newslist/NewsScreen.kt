package com.example.civicly.ui.newslist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.Article
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

data class NewsArticle(
    val outlet: String,
    val title: String,
    val published: String,
    val area: String,
    val topic: String,
    val summary: String,
    val whyItMatters: String,
    val articleUrl: String,
    val imageUrl: String,
)

private val NEWS_ARTICLES = listOf(
    NewsArticle(
        "Alameda Post",
        "Oakland Alameda Water Shuttle Extended To 2028, Birthday Celebration July 31",
        "2026-07-27", "Alameda / Oakland", "Transportation",
        "The free Woodstock water shuttle will keep crossing the Oakland Estuary through June 2028 after strong ridership. In its first 24 months it carried about 251,000 passengers and 60,000 bicycles.",
        "Keeps a popular car-free connection between Alameda Landing and Jack London Square operating for two more years.",
        "https://alamedapost.com/news/oakland-alameda-water-shuttle-extended-to-2028-birthday-celebration-july-31/",
        "https://cdn.carmel-apartments.com/system/uploads/fae/image/asset/34157/level_3_alameda-water-shuttle.jpg",
    ),
    NewsArticle(
        "Alameda Post",
        "Central Avenue Construction Update – Traffic Signal Upgrades",
        "2026-07-27", "Alameda", "Road Safety",
        "Central Avenue is receiving signal upgrades at Eighth, Webster, and Sherman/Encinal, including protected turns, bicycle detection, accessible pedestrian signals, and updated timing.",
        "The changes are designed to reduce crashes and make a major Alameda corridor safer for drivers, cyclists, pedestrians, and transit riders.",
        "https://alamedapost.com/news/central-avenue-construction-update-traffic-signal-upgrades/",
        "https://stories.opengov.com/alamedaca/uploads/7c27b4fc0bcd-190923-123640_CoA-Infrastructure-Streetlights.jpg",
    ),
    NewsArticle(
        "Alameda Post",
        "Alameda County Announces \$6.7 Billion Balanced Budget for 2026-27",
        "2026-07-01", "Alameda County", "County Budget",
        "Supervisors adopted a \$6.7 billion FY 2026-27 budget that closes a projected \$184.8 million gap while preserving major services, including more than \$138 million for Alameda Health System.",
        "The county says it balanced the budget without major program reductions or layoffs despite state and federal funding uncertainty.",
        "https://alamedapost.com/news/alameda-county-announces-6-7-billion-balanced-budget-for-2026-27/",
        "https://www.acgov.org/government/news/images/renaming-ceremony4.jpg",
    ),
    NewsArticle(
        "KQED",
        "Berkeley Moves Forward With Bike Lanes on Hopkins Street, After Fierce Debate",
        "2026-07-30", "Berkeley", "Transportation",
        "Berkeley City Council voted 7-2 to move ahead with a Hopkins Street repaving plan featuring protected one-way bike lanes on both sides. Supporters cite safety and climate goals; opponents worry about parking and business access.",
        "It is a major local street-design decision balancing pedestrian and cyclist safety against parking and commercial concerns.",
        "https://www.kqed.org/news/12092868/berkeley-moves-forward-with-bike-lanes-on-hopkins-street-after-fierce-debate",
        "https://images.squarespace-cdn.com/content/v1/5b4e49db5b409b66d9846821/1630834470448-XYK3S0H1MESPE469OSUE/MilviaBikeway.jpg",
    ),
    NewsArticle(
        "KQED",
        "Little Fire in East Bay: Crews Halt Spread, No Structures Damaged, Pleasanton Mayor Says",
        "2026-07-22", "Pleasanton / Sunol", "Wildfire",
        "The Little Fire grew to just over 1,000 acres and was reported at 40% containment after crews stopped its forward spread. Several evacuation orders were downgraded or lifted, with no structures reported damaged.",
        "The fire affected eastern Alameda County communities and triggered evacuations near Pleasanton, Sunol, and I-680.",
        "https://www.kqed.org/news/12092116/little-fire-in-east-bay-crews-halt-spread-no-structures-damaged-pleasanton-mayor-says",
        "https://patch.com/img/cdn20/users/25839229/20220622/031711/styles/patch_image/public/image0-2___22150953711.jpg",
    ),
    NewsArticle(
        "KQED",
        "Alameda County Sells Black Housing Developers Abandoned East Oakland Lot for \$10",
        "2026-07-15", "East Oakland", "Housing",
        "Alameda County transferred a 15,000-square-foot tax-defaulted lot on MacArthur Boulevard to a community initiative and nonprofit housing developer for \$10 after the parcel accumulated roughly \$1.7 million in back taxes.",
        "County leaders hope the transfer can become a model for turning long-vacant properties into affordable housing and supporting long-term neighborhood stability.",
        "https://www.kqed.org/news/12090992/alameda-county-sells-black-housing-developers-abandoned-east-oakland-lot-for-10",
        "https://sfyimby.com/wp-content/uploads/2022/04/7300-MacArthur-Boulevard-image-via-Google-Satellite-1536x750.jpg",
    ),
)

@Composable
fun NewsScreen(onBack: () -> Unit = {}) {
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
            item { NewsTopBar(onBack) }
            items(NEWS_ARTICLES, key = { it.articleUrl }) { article ->
                NewsArticleCard(article, onOpen = { uriHandler.openUri(article.articleUrl) })
            }
        }
    }
}

@Composable
private fun NewsTopBar(onBack: () -> Unit) {
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
        Icon(Icons.AutoMirrored.Filled.Article, contentDescription = null, tint = SlateNavy, modifier = Modifier.size(28.dp))
        Spacer(Modifier.width(12.dp))
        Text("News", style = MaterialTheme.typography.displayLarge, color = SlateNavy)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun NewsArticleCard(article: NewsArticle, onOpen: () -> Unit) {
    Surface(
        onClick = onOpen,
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 4.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            AsyncImage(
                model = article.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp)),
            )
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(article.outlet, style = MaterialTheme.typography.labelMedium, color = SlateNavy)
                Text(article.published, style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
            }
            Text(
                article.title,
                style = MaterialTheme.typography.titleMedium,
                color = SlateNavy,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Tag(article.area)
                Tag(article.topic)
            }
            Text(
                article.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant,
            )
        }
    }
}

@Composable
private fun Tag(label: String) {
    Surface(
        shape = RoundedCornerShape(50),
        color = Color.White,
        border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.4f)),
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = OnSurfaceVariant,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}
