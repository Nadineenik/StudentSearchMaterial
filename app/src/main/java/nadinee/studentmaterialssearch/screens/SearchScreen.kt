// SearchScreen.kt — ФИНАЛЬНАЯ ВЕРСИЯ (автоматически сохраняет в избранное)
package nadinee.studentmaterialssearch.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import nadinee.studentmaterialssearch.App
import nadinee.studentmaterialssearch.data.Favorite
import nadinee.studentmaterialssearch.data.SearchResult
import nadinee.studentmaterialssearch.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController
) {
    val viewModel: SearchViewModel = viewModel()
    var query by rememberSaveable { mutableStateOf("") }
    val scope = rememberCoroutineScope()  // ← Добавили scope

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Поиск") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Запрос") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    if (query.isNotBlank()) viewModel.search(query)
                }),
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))
            Button(
                onClick = { if (query.isNotBlank()) viewModel.search(query) },
                enabled = !viewModel.isLoading,
                modifier = Modifier.align(Alignment.End)
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                } else Text("Искать")
            }

            viewModel.error?.let {
                Spacer(Modifier.height(16.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(viewModel.results, key = { it.url }) { item ->
                    Card(
                        onClick = {
                            // ← ВОТ ЭТО САМОЕ ВАЖНОЕ!
                            scope.launch {
                                // Автоматически сохраняем в избранное при открытии
                                App.database.favoriteDao().add(
                                    Favorite(
                                        url = item.url,
                                        title = item.title,
                                        content = item.content
                                    )
                                )
                            }
                            // Переходим в детали
                            navController.navigate(Screen.Details.createRoute(item.url))
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(item.title, style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                item.content,
                                maxLines = 2,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(item.url, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            if (viewModel.results.isEmpty() && !viewModel.isLoading && viewModel.error == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Введите запрос и нажмите «Искать»", color = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}