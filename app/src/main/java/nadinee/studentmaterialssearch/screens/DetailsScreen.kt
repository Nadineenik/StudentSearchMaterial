// DetailsScreen.kt — НАВСЕГДА РАБОЧАЯ ВЕРСИЯ (БЕЗ savedStateHandle!)
package nadinee.studentmaterialssearch.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import nadinee.studentmaterialssearch.App
import nadinee.studentmaterialssearch.data.Favorite
import nadinee.studentmaterialssearch.data.SearchResult
import nadinee.studentmaterialssearch.navigation.Screen
import java.net.URLDecoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    var isFavorite by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<SearchResult?>(null) }

    // Получаем URL
    val urlArg = navController.currentBackStackEntry?.arguments?.getString("url") ?: ""
    val url = try { URLDecoder.decode(urlArg, "UTF-8") } catch (e: Exception) { urlArg }

    // ВАЖНО: Загружаем данные из базы — ВСЕГДА!
    // Если пользователь открыл из поиска — он ДОЛЖЕН был добавить в избранное, или мы сохраняем автоматически
    LaunchedEffect(url) {
        scope.launch {
            val favorite = App.database.favoriteDao().getByUrl(url)
            result = favorite?.let {
                SearchResult(it.title, it.url, it.content)
            }
            isFavorite = favorite != null
        }
    }

    // Если результат ещё не загрузился
    if (result == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(result!!.title) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text(result!!.title, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text(result!!.content, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    scope.launch {
                        if (isFavorite) {
                            App.database.favoriteDao().remove(url)
                        } else {
                            App.database.favoriteDao().add(
                                Favorite(url, result!!.title, result!!.content)
                            )
                        }
                        isFavorite = !isFavorite
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder, null)
                Spacer(Modifier.width(8.dp))
                Text(if (isFavorite) "Удалить из избранного" else "Добавить в избранное")
            }

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { navController.navigate(Screen.WebView.createRoute(url)) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Открыть в приложении")
            }
        }
    }
}