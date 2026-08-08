package com.example.civicly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.civicly.ui.article.ArticleScreen
import com.example.civicly.ui.events.EventsScreen
import com.example.civicly.ui.feed.FeedScreen
import com.example.civicly.ui.newslist.NewsScreen
import com.example.civicly.ui.officials.OfficialsScreen
import com.example.civicly.ui.ordinances.OrdinancesScreen
import com.example.civicly.ui.personalization.PersonalizationScreen
import com.example.civicly.ui.profile.ProfileScreen
import com.example.civicly.ui.search.SearchScreen
import com.example.civicly.ui.splash.SplashScreen
import com.example.civicly.ui.theme.CiviclyTheme

private enum class Route {
    Splash, Personalize, Feed, Search, Article, Profile,
    Ordinances, Officials, News, Events,
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CiviclyTheme {
                var route by remember { mutableStateOf(Route.Splash) }
                var selectedArticleId by remember { mutableStateOf<String?>(null) }
                var userName by remember { mutableStateOf<String?>(null) }
                var userLocation by remember { mutableStateOf<String?>(null) }
                when (route) {
                    Route.Splash -> SplashScreen(onGetStarted = { route = Route.Personalize })
                    Route.Personalize -> PersonalizationScreen(
                        initialName = userName.orEmpty(),
                        initialDistrict = userLocation ?: "98104",
                        onContinue = { name, district ->
                            userName = name
                            userLocation = district
                            route = Route.Feed
                        },
                        onSkip = { route = Route.Feed },
                    )
                    Route.Feed -> FeedScreen(
                        onOpenSearch = { route = Route.Search },
                        onOpenArticle = { id -> selectedArticleId = id; route = Route.Article },
                        onOpenProfile = { route = Route.Profile },
                    )
                    Route.Search -> SearchScreen(
                        onOpenFeed = { route = Route.Feed },
                        onOpenArticle = { id -> selectedArticleId = id; route = Route.Article },
                        onOpenProfile = { route = Route.Profile },
                        onOpenOrdinances = { route = Route.Ordinances },
                        onOpenOfficials = { route = Route.Officials },
                        onOpenNews = { route = Route.News },
                        onOpenEvents = { route = Route.Events },
                    )
                    Route.Article -> ArticleScreen(
                        itemId = selectedArticleId,
                        onBack = { route = Route.Feed },
                        onOpenFeed = { route = Route.Feed },
                        onOpenSearch = { route = Route.Search },
                        onOpenProfile = { route = Route.Profile },
                    )
                    Route.Profile -> ProfileScreen(
                        userName = userName,
                        userLocation = userLocation,
                        onOpenFeed = { route = Route.Feed },
                        onOpenSearch = { route = Route.Search },
                        onOpenArticle = { id -> selectedArticleId = id; route = Route.Article },
                    )
                    Route.Ordinances -> OrdinancesScreen(onBack = { route = Route.Search })
                    Route.Officials -> OfficialsScreen(onBack = { route = Route.Search })
                    Route.News -> NewsScreen(onBack = { route = Route.Search })
                    Route.Events -> EventsScreen(onBack = { route = Route.Search })
                }
            }
        }
    }
}
