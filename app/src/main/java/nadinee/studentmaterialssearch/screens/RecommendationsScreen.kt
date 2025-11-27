// RecommendationsScreen.kt — ФИНАЛЬНАЯ РАБОЧАЯ ВЕРСИЯ
package nadinee.studentmaterialssearch.screens

import AuthState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import nadinee.studentmaterialssearch.App
import nadinee.studentmaterialssearch.data.Favorite
import nadinee.studentmaterialssearch.navigation.Screen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationsScreen(
    navController: NavController,
    authState: AuthState
) {
    val currentUser = authState.currentUser.collectAsState().value

    // Загружаем избранное только если пользователь авторизован
    val favorites by produceState(initialValue = emptyList<Favorite>()) {
        currentUser?.email?.let { email ->
            value = App.database.favoriteDao().getAllForUser(email)
        }
    }

    // === Формируем ключевые слова из интересов и избранного ===
    val interests = currentUser?.interests
        ?.split(",")
        ?.map { it.trim() }
        ?.filter { it.isNotEmpty() } ?: emptyList()

    val favoriteWords = favorites
        .flatMap { listOf(it.title, it.content) }
        .flatMap { it.split(" ", ",", ".", "!", "?", "-", "_") }
        .map { it.lowercase() }
        .filter { it.length > 4 && it.none { char -> char in "0123456789" } } // убираем короткие и с цифрами
        .groupingBy { it }
        .eachCount()
        .entries
        .sortedByDescending { it.value }
        .take(20)
        .map { it.key }

    // Объединяем и мешаем
    val recommendedQueries = (interests + favoriteWords)
        .filter { it.isNotEmpty() }
        .distinct()
        .shuffled()
        .take(12)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Рекомендации для вас") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (recommendedQueries.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Добавьте интересы в профиле", fontSize = 18.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("и сохраните материалы в избранное —", color = MaterialTheme.colorScheme.outline)
                        Text("и я подберу лучшие темы!", color = MaterialTheme.colorScheme.outline)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(recommendedQueries) { query ->
                        Card(
                            onClick = {
                                SearchQueryEvent.sendQuery(query)
                                navController.navigate(Screen.Search.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    text = query,
                                    style = MaterialTheme.typography.titleMedium,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}