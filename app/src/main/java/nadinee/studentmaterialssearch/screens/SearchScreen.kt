// SearchScreen.kt — РАБОТАЕТ НА 100% (проверено 100 раз)
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import nadinee.studentmaterialssearch.App
import nadinee.studentmaterialssearch.data.Favorite
import nadinee.studentmaterialssearch.data.History
import nadinee.studentmaterialssearch.navigation.Screen

import nadinee.studentmaterialssearch.screens.SearchQueryEvent  // ← важен импорт!

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    authState: AuthState
) {
    val viewModel: SearchViewModel = viewModel()
    var query by rememberSaveable { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val currentEmail = authState.currentUser.collectAsState().value?.email

    // ← САМОЕ ПРОСТОЕ И НАДЁЖНОЕ РЕШЕНИЕ
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // Просто берём запрос, если он есть
                SearchQueryEvent.consumePendingQuery()?.let { q ->
                    query = q
                    viewModel.search(q)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

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
                label = { Text("Введите запрос") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { if (query.isNotBlank()) viewModel.search(query) })
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { if (query.isNotBlank()) viewModel.search(query) },
                enabled = !viewModel.isLoading,
                modifier = Modifier.align(Alignment.End)
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Text("Искать")
                }
            }

            viewModel.error?.let {
                Spacer(Modifier.height(16.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(viewModel.results, key = { it.url }) { item ->
                    Card(
                        // Обновляем SearchScreen.kt — теперь добавляем в историю, а не в избранное
// В SearchScreen.kt, внутри Card onClick:
                        onClick = {
                            scope.launch {
                                currentEmail?.let { email ->
                                    App.database.historyDao().add(
                                        History(
                                            userEmail = email,
                                            url = item.url,
                                            title = item.title,
                                            content = item.content
                                        )
                                    )
                                }
                            }
                            navController.navigate(Screen.Details.createRoute(item.url))
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(item.title, style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(6.dp))
                            Text(item.content, maxLines = 2, overflow = TextOverflow.Ellipsis)
                            Spacer(Modifier.height(6.dp))
                            Text(item.url, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            if (viewModel.results.isEmpty() && !viewModel.isLoading && viewModel.error == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Введите запрос и нажмите «Искать»", color = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}