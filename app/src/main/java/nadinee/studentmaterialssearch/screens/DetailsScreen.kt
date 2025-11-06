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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import nadinee.studentmaterialssearch.App
import nadinee.studentmaterialssearch.data.Favorite
import nadinee.studentmaterialssearch.data.SearchResult


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(result: SearchResult?, onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isFavorite by remember { mutableStateOf(false) }

    // Проверяем, в избранном ли
    LaunchedEffect(result?.url) {
        result?.url?.let { url ->
            scope.launch {
                val favorites = App.database.favoriteDao().getAll()
                isFavorite = favorites.any { it.url == url }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(result?.title ?: "Детали") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            if (result != null) {
                Text(result.title, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                Text(result.content, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(16.dp))

                // КНОПКА ИЗБРАННОЕ
                Button(
                    onClick = {
                        scope.launch {
                            val favorite = Favorite(
                                url = result.url,
                                title = result.title,
                                content = result.content
                            )
                            if (isFavorite) {
                                App.database.favoriteDao().remove(result.url)
                            } else {
                                App.database.favoriteDao().add(favorite)
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
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(result.url))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Открыть ссылку")
                }
            } else {
                Text("Результат не найден")
            }
        }
    }
}