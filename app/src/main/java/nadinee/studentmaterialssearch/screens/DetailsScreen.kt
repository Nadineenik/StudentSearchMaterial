// DetailsScreen.kt
package nadinee.studentmaterialssearch.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import nadinee.studentmaterialssearch.data.SearchResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(result: SearchResult?, onBack: () -> Unit) {
    val context = LocalContext.current

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
                Text("Результат не найден", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}