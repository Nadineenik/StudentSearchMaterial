// DetailsScreen.kt — ФИНАЛЬНАЯ ВЕРСИЯ: история + избранное + 2 кнопки открытия
package nadinee.studentmaterialssearch.screens

import AuthState
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import nadinee.studentmaterialssearch.App
import nadinee.studentmaterialssearch.data.Favorite
import nadinee.studentmaterialssearch.data.History
import nadinee.studentmaterialssearch.data.SearchResult
import nadinee.studentmaterialssearch.navigation.Screen
import java.net.URLDecoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(navController: NavController, authState: AuthState) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val currentEmail = authState.currentUser.collectAsState().value?.email

    var isFavorite by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<SearchResult?>(null) }

    val urlArg = navController.currentBackStackEntry?.arguments?.getString("url") ?: ""
    val url = try { URLDecoder.decode(urlArg, "UTF-8") } catch (e: Exception) { urlArg }

    LaunchedEffect(url, currentEmail) {
        scope.launch {
            currentEmail?.let { email ->
                val favorite = App.database.favoriteDao().getByUrl(url, email)
                result = favorite?.let { SearchResult(it.title, it.url, it.content) }
                isFavorite = favorite != null
            }

            if (result == null) {
                currentEmail?.let { email ->
                    val history = App.database.historyDao().getAllForUser(email).find { it.url == url }
                    result = history?.let { SearchResult(it.title, it.url, it.content) }
                }
            }

            if (result == null) {
                result = SearchResult("Статья", url, "Содержание недоступно")
            }

            // Добавляем в историю
            currentEmail?.let { email ->
                App.database.historyDao().add(
                    History(
                        userEmail = email,
                        url = url,
                        title = result!!.title,
                        content = result!!.content
                    )
                )
            }
        }
    }

    if (result == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(result!!.title, maxLines = 1) },
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
            Spacer(Modifier.height(24.dp))

            // КНОПКА ИЗБРАННОГО — ТОЛЬКО ВРУЧНУЮ!
            currentEmail?.let { email ->
                Button(
                    onClick = {
                        scope.launch {
                            if (isFavorite) {
                                App.database.favoriteDao().remove(url, email)
                            } else {
                                App.database.favoriteDao().add(
                                    Favorite(
                                        url = url,
                                        userEmail = email,
                                        title = result!!.title,
                                        content = result!!.content
                                    )
                                )
                            }
                            isFavorite = !isFavorite
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFavorite) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(if (isFavorite) "Удалить из избранного" else "Добавить в избранное")
                }
                Spacer(Modifier.height(16.dp))
            }

            // КНОПКА ОТКРЫТЬ В ПРИЛОЖЕНИИ
            Button(
                onClick = { navController.navigate(Screen.WebView.createRoute(url)) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Открыть в приложении")
            }

            Spacer(Modifier.height(12.dp))

            // КНОПКА ОТКРЫТЬ В БРАУЗЕРЕ — НОВАЯ!
            OutlinedButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.OpenInBrowser, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Открыть в браузере")
            }
        }
    }
}