package com.example.civicly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.civicly.ui.bills.BillsScreen
import com.example.civicly.ui.match.MatchQuizScreen
import com.example.civicly.ui.match.MatchResultsScreen
import com.example.civicly.ui.match.MatchViewModel
import com.example.civicly.ui.news.NewsScreen
import com.example.civicly.ui.search.SearchScreen
import com.example.civicly.ui.theme.CiviclyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CiviclyTheme { App() }
        }
    }
}

// ponytail: 4 destinations mapped to existing routes — "Community" → match, "Explore" → bills.
// Split into dedicated Community/Explore screens when either grows beyond a repurposed alias.
private data class NavItem(val route: String, val label: String, val icon: ImageVector, val isCenter: Boolean = false)

private val NAV_ITEMS = listOf(
    NavItem("news", "News", Icons.Filled.Newspaper),
    NavItem("match", "Community", Icons.Filled.Groups),
    NavItem("search", "Search", Icons.Filled.Search, isCenter = true),
    NavItem("bills", "Explore", Icons.Filled.GridView),
)

private const val MATCH_QUIZ = "match/quiz"
private const val MATCH_RESULTS = "match/results"

@Composable
private fun App() {
    val nav = rememberNavController()
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { GlassmorphicHeader(title = "Search", leadingIcon = Icons.Filled.Search) },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            NavHost(
                navController = nav,
                startDestination = "news",
                modifier = Modifier.fillMaxSize(),
            ) {
                composable("bills") { BillsScreen() }
                composable("news") { NewsScreen() }
                composable("search") { SearchScreen() }

                navigation(startDestination = MATCH_QUIZ, route = "match") {
                    composable(MATCH_QUIZ) { entry ->
                        val parent = remember(entry) { nav.getBackStackEntry("match") }
                        val vm: MatchViewModel = viewModel(parent)
                        MatchQuizScreen(vm, onFinished = { nav.navigate(MATCH_RESULTS) })
                    }
                    composable(MATCH_RESULTS) { entry ->
                        val parent = remember(entry) { nav.getBackStackEntry("match") }
                        val vm: MatchViewModel = viewModel(parent)
                        MatchResultsScreen(vm, onRetake = {
                            vm.reset()
                            nav.popBackStack(MATCH_QUIZ, inclusive = false)
                        })
                    }
                }
            }
            FloatingPillNav(
                nav = nav,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
                    .fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun FloatingPillNav(nav: NavController, modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    val current by nav.currentBackStackEntryAsState()
    val currentRoutes = current?.destination?.hierarchy?.mapNotNull { it.route }?.toSet().orEmpty()

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(32.dp),
        color = scheme.surface.copy(alpha = 0.85f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
        shadowElevation = 8.dp,
    ) {
        Row(
            Modifier.fillMaxWidth().height(64.dp).padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            NAV_ITEMS.forEach { item ->
                val selected = item.route in currentRoutes
                val onClick: () -> Unit = {
                    nav.navigate(item.route) {
                        popUpTo(nav.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
                if (item.isCenter) CenterAction(item, onClick)
                else PillNavItem(item, selected, onClick)
            }
        }
    }
}

@Composable
private fun PillNavItem(item: NavItem, selected: Boolean, onClick: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        modifier = Modifier.size(48.dp),
        shape = CircleShape,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                item.icon,
                contentDescription = item.label,
                tint = if (selected) scheme.primary else scheme.onSurfaceVariant.copy(alpha = 0.7f),
            )
        }
    }
}

@Composable
private fun CenterAction(item: NavItem, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        shadowElevation = 4.dp,
        modifier = Modifier.size(48.dp),
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Icon(item.icon, contentDescription = item.label)
        }
    }
}

// ponytail: translucent fill only, no real GPU blur (RenderEffect needs API 31+ and a fallback path).
// Add BlurEffect when minSdk moves past 31 or product wants the frost badly enough to write the fallback.
@Composable
private fun GlassmorphicHeader(
    title: String,
    leadingIcon: ImageVector,
    onProfileClick: () -> Unit = {},
) {
    val scheme = MaterialTheme.colorScheme
    Row(
        Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.6f))
            .border(BorderStroke(1.dp, scheme.outlineVariant.copy(alpha = 0.4f)))
            .statusBarsPadding()
            .height(64.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(leadingIcon, contentDescription = null, tint = scheme.primary)
            Spacer(Modifier.width(12.dp))
            Text(title, style = MaterialTheme.typography.displayLarge, color = scheme.primary)
        }
        Surface(
            onClick = onProfileClick,
            shape = CircleShape,
            color = scheme.surfaceVariant,
            modifier = Modifier.size(36.dp),
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(Icons.Filled.Person, contentDescription = "Profile", tint = scheme.onSurfaceVariant)
            }
        }
    }
}
