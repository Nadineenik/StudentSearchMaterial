// DetailsScreen.kt
package nadinee.studentmaterialssearch.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import nadinee.studentmaterialssearch.App
import nadinee.studentmaterialssearch.data.Favorite
import nadinee.studentmaterialssearch.data.SearchResult


// DetailsScreen.kt — ИСПРАВЛЕННАЯ ВЕРСИЯ
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    result: SearchResult? = null,
    url: String = "",           // ← ДОБАВИЛ
    onBack: () -> Unit = {}     // ← ДОБАВИЛ
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isFavorite by remember { mutableStateOf(false) }

    // Определяем, какой результат показывать
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
                                    content = displayResult.content.take(500) // ограничиваем длину
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
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Открыть в браузере")
            }
        }
    }
}