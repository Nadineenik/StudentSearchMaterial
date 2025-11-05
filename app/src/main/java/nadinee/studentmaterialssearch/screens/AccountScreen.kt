// AccountScreen.kt
package nadinee.studentmaterialssearch.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nadinee.studentmaterialssearch.App
import nadinee.studentmaterialssearch.data.User
import androidx.compose.ui.res.painterResource
import nadinee.studentmaterialssearch.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(onLogout: () -> Unit) {
    val context = LocalContext.current
    var email by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // Загружаем email из БД
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val userDao = App.database.userDao()
                // Берём первого (или любого) пользователя
                val users = userDao.getAllUsers() // ← НУЖЕН НОВЫЙ МЕТОД!
                email = users.firstOrNull()?.email
            } catch (e: Exception) {
                email = "unknown@example.com"
            }
        }
    }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Мой профиль") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // ТВОЙ ЭМОДЗИ-АВАТАР
            Icon(
                painter = painterResource(id = R.drawable.avatar),
                contentDescription = "Аватар",
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 16.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            // Приветствие
            Text(
                text = "Привет, ${email ?: "гость"}!",
                style = MaterialTheme.typography.titleLarge,
                fontSize = 20.sp
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Выйти из аккаунта")
            }
        }
    }
}