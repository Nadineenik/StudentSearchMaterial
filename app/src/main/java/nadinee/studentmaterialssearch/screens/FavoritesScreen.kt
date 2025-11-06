// FavoritesScreen.kt
package nadinee.studentmaterialssearch.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import nadinee.studentmaterialssearch.App
import nadinee.studentmaterialssearch.data.Favorite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen() {
    var favorites by remember { mutableStateOf<List<Favorite>>(emptyList()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            favorites = App.database.favoriteDao().getAll()
        }
    }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Избранное") }) }
    ) { padding ->
        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Пока ничего не добавлено")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(favorites) { item ->
                    Card(
                        onClick = { /* Можно перейти в Details */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(item.title, style = MaterialTheme.typography.titleMedium)
                            Text(item.content, style = MaterialTheme.typography.bodySmall)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                item.url,
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}