// LoginScreen.kt — ФИНАЛЬНАЯ РАБОЧАЯ ВЕРСИЯ
package nadinee.studentmaterialssearch.screens

import AuthState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nadinee.studentmaterialssearch.App
import nadinee.studentmaterialssearch.data.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    authState: AuthState,
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var dbReady by remember { mutableStateOf(false) }

    // Инициализация БД
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            App.database // просто вызываем — lazy инициализируется
        }
        dbReady = true
    }

    if (!dbReady) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val userDao = App.database.userDao()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Вход / Регистрация") })
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
            // Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            // Пароль
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))

            // Кнопка входа / регистрации
            Button(
                onClick = {
                    scope.launch {
                        try {
                            val user = withContext(Dispatchers.IO) {
                                val existing = userDao.getUser(email, password)
                                if (existing != null) {
                                    existing
                                } else {
                                    val newUser = User(
                                        email = email,
                                        password = password,
                                        name = email.split("@").firstOrNull() ?: "Пользователь"
                                    )
                                    userDao.insert(newUser)
                                    newUser
                                }
                            }
                            authState.login(user)
                            onLoginSuccess()
                            message = "Добро пожаловать!"
                        } catch (e: Exception) {
                            message = "Ошибка: ${e.message}"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Войти / Зарегистрироваться")
            }

            Spacer(Modifier.height(12.dp))
            if (message.isNotEmpty()) {
                Text(
                    text = message,
                    color = if (message.contains("Ошибка")) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
            }
        }
    }
}