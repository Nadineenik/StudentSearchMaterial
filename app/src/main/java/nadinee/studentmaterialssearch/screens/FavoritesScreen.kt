// FavoritesScreen.kt — ФИНАЛЬНАЯ ВЕРСИЯ (без savedStateHandle!)
package nadinee.studentmaterialssearch.screens

import AuthState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import nadinee.studentmaterialssearch.App
import nadinee.studentmaterialssearch.data.Favorite
import nadinee.studentmaterialssearch.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    navController: NavController,
    authState: AuthState
) {
    val currentUserEmail = authState.currentUser.collectAsState().value?.email
    var favorites by remember { mutableStateOf<List<Favorite>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(currentUserEmail) {
        if (currentUserEmail != null) {
            scope.launch {
                favorites = App.database.favoriteDao().getAllForUser(currentUserEmail)
                isLoading = false
            }
        }
    }

    Scaffold(topBar = { CenterAlignedTopAppBar(title = { Text("Избранное") }) }) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when {
                currentUserEmail == null -> Text("Не авторизован", Modifier.align(Alignment.Center))
                isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                favorites.isEmpty() -> Text("Пока ничего не добавлено", Modifier.align(Alignment.Center))
                else -> {
                    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(favorites, key = { it.url }) { fav ->
                            Card(onClick = { navController.navigate(Screen.Details.createRoute(fav.url)) }, modifier = Modifier.fillMaxWidth()) {
                                Column(Modifier.padding(16.dp)) {
                                    Text(fav.title, style = MaterialTheme.typography.titleMedium)
                                    Spacer(Modifier.height(4.dp))
                                    Text(fav.content, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                    Spacer(Modifier.height(4.dp))
                                    Text(fav.url, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}