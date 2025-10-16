package nadinee.studentmaterialssearch.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import nadinee.studentmaterialssearch.screens.SearchScreen
import nadinee.studentmaterialssearch.screens.DetailsScreen

sealed class Screen(val route: String) {
    object Search : Screen("search")
    object Details : Screen("details")
}

@Composable
fun SetupNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Screen.Search.route
    ) {
        composable(Screen.Search.route) {
            SearchScreen(onItemClick = { navController.navigate(Screen.Details.route) })
        }
        composable(Screen.Details.route) {
            DetailsScreen(onBack = { navController.popBackStack() })
        }
    }
}
