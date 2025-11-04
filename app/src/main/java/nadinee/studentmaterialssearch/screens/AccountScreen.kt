package nadinee.studentmaterialssearch.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(onLogout: () -> Unit) {  // ✅ вот здесь параметр добавлен
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Мой профиль") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Имя пользователя: student@example.com", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = { onLogout() },  // ✅ теперь кнопка выхода вызывает callback
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Выйти из аккаунта")
            }
        }
    }
}
