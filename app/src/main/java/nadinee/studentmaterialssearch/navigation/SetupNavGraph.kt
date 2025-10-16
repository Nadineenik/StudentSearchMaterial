package nadinee.studentmaterialssearch.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import nadinee.studentmaterialssearch.screens.*

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Account : Screen("account", "Мой аккаунт", Icons.Filled.AccountCircle)
    object Search : Screen("search", "Поиск", Icons.Filled.Search)
    object Favorites : Screen("favorites", "Избранное", Icons.Filled.Star)
    object Details : Screen("details", "Детали", Icons.Filled.Search)
}

@Composable
fun SetupNavGraph(navController: NavHostController = rememberNavController()) {
    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Search.route,
            modifier = androidx.compose.ui.Modifier.padding(paddingValues)
        ) {
            composable(Screen.Account.route) { AccountScreen() }
            composable(Screen.Search.route) {
                SearchScreen(onItemClick = { navController.navigate(Screen.Details.route) })
            }
            composable(Screen.Favorites.route) { FavoritesScreen() }
            composable(Screen.Details.route) {
                DetailsScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}

@Composable
fun BottomNavBar(navController: NavHostController) {
    val items = listOf(
        Screen.Account,
        Screen.Search,
        Screen.Favorites
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
