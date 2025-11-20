// DetailsScreen.kt — ФИНАЛЬНАЯ, ИДЕАЛЬНАЯ ВЕРСИЯ
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
import nadinee.studentmaterialssearch.navigation.Screen  // ← Только этот импорт!

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    result: SearchResult? = null,
    url: String = "",
    onBack: () -> Unit = {},
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    var isFavorite by remember { mutableStateOf(false) }

    val displayResult = result ?: SearchResult(
        title = "Внешняя ссылка",
        content = "Ссылка была открыта напрямую",
        url = url
    )

    LaunchedEffect(url) {
        scope.launch {
            val fav = App.database.favoriteDao().getByUrl(url)
            isFavorite = fav != null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(displayResult.title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text(displayResult.title, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text(displayResult.content, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(16.dp))

            // Кнопка избранного
            Button(
                onClick = {
                    scope.launch {
                        if (isFavorite) {
                            App.database.favoriteDao().remove(url)
                        } else {
                            App.database.favoriteDao().add(
                                Favorite(
                                    url = url,
                                    title = displayResult.title,
                                    content = displayResult.content.take(500)
                                )
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
                Icon(
                    if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(if (isFavorite) "Удалить из избранного" else "Добавить в избранное")
            }

            Spacer(Modifier.height(16.dp))

            // КНОПКА — ОТКРЫТЬ В ПРИЛОЖЕНИИ (WebView)
            Button(
                onClick = {
                    navController.navigate(Screen.WebView.createRoute(url))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Открыть в приложении")
            }
        }
    }
}