// LoginScreen.kt — ФИНАЛЬНАЯ ВЕРСИЯ БЕЗ ОШИБОК
package nadinee.studentmaterialssearch.screens

import AuthState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
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
    //var messageColor by remember { mutableStateOf(MaterialTheme.colorScheme.primary) }
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
        topBar = { CenterAlignedTopAppBar(title = { Text("Вход / Регистрация") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it.trim() },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        message = "Заполните email и пароль"
                        //messageColor = MaterialTheme.colorScheme.error
                        return@Button
                    }

                    scope.launch {
                        try {
                            val existingUser = withContext(Dispatchers.IO) {
                                userDao.getUser(email, password)
                            }

                            when {
                                existingUser != null -> {
                                    // Уже был — вход
                                    authState.login(existingUser)
                                    message = "Добро пожаловать!"
                                    //messageColor = MaterialTheme.colorScheme.primary
                                    onLoginSuccess()
                                }
                                else -> {
                                    // Проверяем, есть ли такой email
                                    val userWithEmail = withContext(Dispatchers.IO) {
                                        userDao.getAllUsers().find { it.email == email }
                                    }

                                    if (userWithEmail != null) {
                                        message = "Неверный пароль"
                                        //messageColor = MaterialTheme.colorScheme.error
                                    } else {
                                        // Новый пользователь
                                        val newUser = User(
                                            email = email,
                                            password = password,
                                            name = email.split("@").firstOrNull() ?: "Пользователь"
                                        )
                                        withContext(Dispatchers.IO) {
                                            userDao.insert(newUser)
                                        }
                                        authState.login(newUser)
                                        message = "Регистрация успешна! Добро пожаловать!"
                                        //messageColor = MaterialTheme.colorScheme.primary
                                        onLoginSuccess()
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            message = "Ошибка: ${e.message}"
                            //messageColor = MaterialTheme.colorScheme.error
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Войти / Зарегистрироваться")
            }

            Spacer(Modifier.height(16.dp))

            if (message.isNotEmpty()) {
                Text(
                    text = message,
                    color = if (message.contains("пароль", ignoreCase = true) ||
                        message.contains("Ошибка", ignoreCase = true)) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
            }
        }
    }
}