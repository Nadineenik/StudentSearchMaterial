// SearchScreen.kt — ФИНАЛЬНАЯ РАБОЧАЯ ВЕРСИЯ
package nadinee.studentmaterialssearch.screens

import AuthState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import nadinee.studentmaterialssearch.App
import nadinee.studentmaterialssearch.data.Favorite
import nadinee.studentmaterialssearch.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    authState: AuthState
) {
    val viewModel: SearchViewModel = viewModel()
    var query by rememberSaveable { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    // Текущий пользователь
    val currentEmail = authState.currentUser.collectAsState().value?.email

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Поиск") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Поле ввода
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Введите запрос") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = { if (query.isNotBlank()) viewModel.search(query) }
                )
            )

            Spacer(Modifier.height(12.dp))

            // Кнопка поиска
            Button(
                onClick = { if (query.isNotBlank()) viewModel.search(query) },
                enabled = !viewModel.isLoading,
                modifier = Modifier.align(Alignment.End)
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Искать")
                }
            }

            // Ошибка
            viewModel.error?.let {
                Spacer(Modifier.height(16.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(16.dp))

            // Результаты поиска
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(viewModel.results, key = { it.url }) { item ->
                    Card(
                        onClick = {
                            // Автоматически сохраняем в избранное при открытии
                            currentEmail?.let { email ->
                                scope.launch {
                                    App.database.favoriteDao().add(
                                        Favorite(
                                            url = item.url,
                                            userEmail = email,           // ВОТ ЭТО ГЛАВНОЕ!
                                            title = item.title,
                                            content = item.content
                                        )
                                    )
                                }
                            }
                            // Переходим в детали
                            navController.navigate(Screen.Details.createRoute(item.url))
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = item.content,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = item.url,
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            // Пустое состояние
            if (viewModel.results.isEmpty() && !viewModel.isLoading && viewModel.error == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Введите запрос и нажмите «Искать»",
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}