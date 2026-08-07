package com.example.civicly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
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

private sealed class Tab(val route: String, val label: String, val icon: ImageVector) {
    object Bills : Tab("bills", "Bills", Icons.AutoMirrored.Filled.List)
    object News : Tab("news", "News", Icons.Filled.Info)
    object Match : Tab("match", "Match", Icons.Filled.CheckCircle)
}

private const val MATCH_QUIZ = "match/quiz"
private const val MATCH_RESULTS = "match/results"

@Composable
private fun App() {
    val nav = rememberNavController()
    val tabs = listOf(Tab.Bills, Tab.News, Tab.Match)
    Scaffold(
        bottomBar = {
            NavigationBar {
                val current by nav.currentBackStackEntryAsState()
                val destination = current?.destination
                tabs.forEach { tab ->
                    val selected = destination?.hierarchy?.any { it.route == tab.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            nav.navigate(tab.route) {
                                popUpTo(nav.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) },
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = Tab.Bills.route,
            modifier = Modifier.padding(padding),
        ) {
            composable(Tab.Bills.route) { BillsScreen() }
            composable(Tab.News.route) { NewsScreen() }

            navigation(startDestination = MATCH_QUIZ, route = Tab.Match.route) {
                composable(MATCH_QUIZ) { entry ->
                    val parent = remember(entry) { nav.getBackStackEntry(Tab.Match.route) }
                    val vm: MatchViewModel = viewModel(parent)
                    MatchQuizScreen(vm, onFinished = { nav.navigate(MATCH_RESULTS) })
                }
                composable(MATCH_RESULTS) { entry ->
                    val parent = remember(entry) { nav.getBackStackEntry(Tab.Match.route) }
                    val vm: MatchViewModel = viewModel(parent)
                    MatchResultsScreen(vm, onRetake = {
                        vm.reset()
                        nav.popBackStack(MATCH_QUIZ, inclusive = false)
                    })
                }
            }
        }
    }
}
