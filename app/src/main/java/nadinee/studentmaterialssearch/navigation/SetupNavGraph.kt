// SetupNavGraph.kt — ГАРАНТИРОВАННО РАБОТАЕТ НА ЛЮБОЙ ВЕРСИИ!
package nadinee.studentmaterialssearch.navigation

import AuthState
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.*
import androidx.navigation.compose.*
import kotlinx.coroutines.flow.collectLatest  // ← ЭТО РАБОТАЕТ ВЕЗДЕ!
import nadinee.studentmaterialssearch.App

import nadinee.studentmaterialssearch.data.SearchResult
import nadinee.studentmaterialssearch.screens.*
import java.net.URLDecoder
import java.net.URLEncoder


sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Login : Screen("login", "Вход", Icons.Filled.Login)
    object Search : Screen("search", "Поиск", Icons.Filled.Search)
    object Account : Screen("account", "Профиль", Icons.Filled.AccountCircle)
    object Favorites : Screen("favorites", "Избранное", Icons.Filled.Star)
    object Details : Screen("details/{url}", "Результат") {
        fun createRoute(url: String) = "details/${URLEncoder.encode(url, "UTF-8")}"
    }
    object WebView : Screen("webview/{url}", "Браузер") {
        fun createRoute(url: String) = "webview/${URLEncoder.encode(url, "UTF-8")}"
    }
}

@Composable
fun SetupNavGraph(
    authState: AuthState,
    navController: NavHostController = rememberNavController()
) {
    var isLoggedIn by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        authState.isLoggedIn.collectLatest { value: Boolean ->
            isLoggedIn = value
        }
    }


    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = { BottomNavBar(navController, isLoggedIn, currentRoute) }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = if (isLoggedIn) Screen.Search.route else Screen.Login.route,
            modifier = Modifier.padding(paddingValues)
        ) {
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



            composable(Screen.Account.route) {
                AccountScreen(
                    onLogout = {
                        authState.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }


            composable(Screen.Search.route) {
                SearchScreen(navController = navController)
            }

            composable(Screen.Favorites.route) {
                FavoritesScreen(navController = navController)
            }

            composable(
                route = Screen.Details.route,
                arguments = listOf(navArgument("url") { type = NavType.StringType })
            ) {
                DetailsScreen(navController = navController)
            }

            composable(
                route = Screen.WebView.route,
                arguments = listOf(navArgument("url") { type = NavType.StringType })
            ) { backStackEntry ->
                val encodedUrl = backStackEntry.arguments?.getString("url") ?: ""
                val url = try { URLDecoder.decode(encodedUrl, "UTF-8") } catch (e: Exception) { encodedUrl }
                WebViewScreen(url = url, navController = navController)
            }
        }
    }
}

@Composable
fun BottomNavBar(
    navController: NavHostController,
    isLoggedIn: Boolean,
    currentRoute: String?
) {
    val items = if (isLoggedIn) {
        listOf(Screen.Search, Screen.Favorites, Screen.Account)
    } else {
        listOf(Screen.Search, Screen.Login)
    }

    NavigationBar {
        items.forEach { screen ->
            val selected = when {
                currentRoute?.startsWith("details") == true -> screen == Screen.Search
                else -> currentRoute == screen.route
            }

            NavigationBarItem(
                icon = { screen.icon?.let { Icon(it, contentDescription = screen.title) } },
                label = { Text(screen.title) },
                selected = selected,
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