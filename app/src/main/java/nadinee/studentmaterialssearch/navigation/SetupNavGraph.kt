// Полный SetupNavGraph.kt
package nadinee.studentmaterialssearch.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import nadinee.studentmaterialssearch.AuthState
import nadinee.studentmaterialssearch.screens.*

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Login : Screen("login", "Вход", Icons.Filled.Login)
    object Search : Screen("search", "Поиск", Icons.Filled.Search)
    object Account : Screen("account", "Профиль", Icons.Filled.AccountCircle)
    object Favorites : Screen("favorites", "Избранное", Icons.Filled.Star)
    object Details : Screen("details", "Детали", Icons.Filled.Search)
}

@Composable
fun SetupNavGraph(
    authState: AuthState,
    navController: NavHostController = rememberNavController()
) {
    val isLoggedIn by authState.isLoggedIn  // ← Теперь работает!

    Scaffold(
        bottomBar = { BottomNavBar(navController, isLoggedIn) }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = if (isLoggedIn) Screen.Search.route else Screen.Login.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Search.route) {
                SearchScreen(onItemClick = { navController.navigate(Screen.Details.route) })
            }
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        authState.login()
                        navController.navigate(Screen.Search.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }
            if (isLoggedIn) {
                composable(Screen.Favorites.route) { FavoritesScreen() }
                composable(Screen.Account.route) { AccountScreen(onLogout = {
                    authState.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Search.route) { inclusive = true }
                    }
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
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}