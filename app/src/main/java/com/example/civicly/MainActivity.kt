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
import com.example.civicly.ui.feed.FeedScreen
import com.example.civicly.ui.personalization.PersonalizationScreen
import com.example.civicly.ui.profile.ProfileScreen
import com.example.civicly.ui.search.SearchScreen
import com.example.civicly.ui.splash.SplashScreen
import com.example.civicly.ui.theme.CiviclyTheme

private enum class Route { Splash, Personalize, Feed, Search, Article, Profile }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CiviclyTheme {
                var route by remember { mutableStateOf(Route.Splash) }
                var selectedArticleId by remember { mutableStateOf<String?>(null) }
                when (route) {
                    Route.Splash -> SplashScreen(onGetStarted = { route = Route.Personalize })
                    Route.Personalize -> PersonalizationScreen(
                        onContinue = { route = Route.Feed },
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
                    )
                    Route.Article -> ArticleScreen(
                        itemId = selectedArticleId,
                        onBack = { route = Route.Feed },
                        onOpenFeed = { route = Route.Feed },
                        onOpenSearch = { route = Route.Search },
                        onOpenProfile = { route = Route.Profile },
                    )
                    Route.Profile -> ProfileScreen(
                        onOpenFeed = { route = Route.Feed },
                        onOpenSearch = { route = Route.Search },
                        onOpenArticle = { id -> selectedArticleId = id; route = Route.Article },
                    )
                }
            }
        }
    }
}
