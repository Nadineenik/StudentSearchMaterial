package nadinee.studentmaterialssearch.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nadinee.studentmaterialssearch.data.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    val context = LocalContext.current
    val db = remember {
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "student_app.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    val userDao = db.userDao()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    // 🟢 безопасно храним последний колбэк, чтобы не упасть после ухода со страницы
    val currentOnLoginSuccess by rememberUpdatedState(onLoginSuccess)

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Вход / Регистрация") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    scope.launch {
                        try {
                            val result = withContext(Dispatchers.IO) {
                                val user = userDao.getUser(email, password)
                                if (user != null) {
                                    "Успешный вход!"
                                } else {
                                    userDao.insert(User(email, password))
                                    "Новый пользователь зарегистрирован!"
                                }
                            }

                            // ⚡ безопасно вызываем навигацию, только если экран активен
                            withContext(Dispatchers.Main) {
                                message = result
                                currentOnLoginSuccess()
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                message = "Ошибка: ${e.message}"
                            }
                            e.printStackTrace()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Войти / Зарегистрироваться")
            }

            Spacer(Modifier.height(12.dp))
            Text(message, color = MaterialTheme.colorScheme.primary)
        }
    }
}
