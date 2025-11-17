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
import nadinee.studentmaterialssearch.data.SearchResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onItemClick: (SearchResult) -> Unit
) {
    // ← Исправлено: правильный импорт viewModel()
    val viewModel: SearchViewModel = viewModel()

    var query by rememberSaveable { mutableStateOf("") }

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
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (query.isNotBlank()) {
                            viewModel.search(query)
                        }
                    }
                ),
                singleLine = true  // ← Хорошая практика
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    if (query.isNotBlank()) {
                        viewModel.search(query)
                    }
                },
                enabled = !viewModel.isLoading,
                modifier = Modifier.align(Alignment.End)
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Искать")
                }
            }

            // Ошибка
            viewModel.error?.let { errorMsg ->
                Spacer(Modifier.height(16.dp))
                Text(errorMsg, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(
                    items = viewModel.results,
                    key = { it.url }  // ← Уникальный ключ для стабильности
                ) { item ->
                    Card(
                        onClick = { onItemClick(item) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = item.content,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 2,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = item.url,
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            // Если список пустой — покажем подсказку
            if (viewModel.results.isEmpty() && !viewModel.isLoading && viewModel.error == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Введите запрос и нажмите «Искать»", color = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}