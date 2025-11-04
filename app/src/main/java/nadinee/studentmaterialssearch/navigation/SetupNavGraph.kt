package nadinee.studentmaterialssearch.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.*

import nadinee.studentmaterialssearch.screens.*

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Login : Screen("login", "Вход", Icons.Filled.Login)
    object Search : Screen("search", "Поиск", Icons.Filled.Search)
    object Account : Screen("account", "Профиль", Icons.Filled.AccountCircle)

    object Favorites : Screen("favorites", "Избранное", Icons.Filled.Star)
    object Details : Screen("details", "Детали", Icons.Filled.Search)
}

@Composable
fun SetupNavGraph(navController: NavHostController = rememberNavController()) {
    var isLoggedIn by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = { BottomNavBar(navController, isLoggedIn) }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Search.route,
            modifier = androidx.compose.ui.Modifier.padding(paddingValues)
        ) {
            composable(Screen.Search.route) {
                SearchScreen(onItemClick = { navController.navigate(Screen.Details.route) })
            }
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        isLoggedIn = true
                        navController.navigate(Screen.Search.route)
                    }
                )
            }
            if (isLoggedIn) {
                composable(Screen.Favorites.route) { FavoritesScreen() }
                composable(Screen.Account.route) { AccountScreen(onLogout = {
                    isLoggedIn = false
                    navController.navigate(Screen.Search.route)
                }) }
            }
            composable(Screen.Details.route) {
                DetailsScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}

@Composable
fun BottomNavBar(navController: NavHostController, isLoggedIn: Boolean) {
    val items = if (isLoggedIn)
        listOf(Screen.Search, Screen.Favorites, Screen.Account)
    else
        listOf(Screen.Search, Screen.Login)

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = currentDestination?.route == screen.route,
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

