// Полный обновлённый LoginScreen.kt
package nadinee.studentmaterialssearch.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nadinee.studentmaterialssearch.App
import nadinee.studentmaterialssearch.App.Companion.database
import nadinee.studentmaterialssearch.data.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val currentOnLoginSuccess by rememberUpdatedState(onLoginSuccess)

    // Ждём готовности БД
    // Внутри LoginScreen
    var dbReady by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            database // просто вызов — инициализирует lazy
            withContext(Dispatchers.Main) { dbReady = true }
        }
    }

    if (!dbReady) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val userDao = App.database.userDao()  // Теперь безопасно

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
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль") },
                visualTransformation = PasswordVisualTransformation(),  // ← СКРЫТЬ ТОЧКАМИ
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    scope.launch {
                        try {
                            val result = withContext(Dispatchers.IO) {
                                val user = userDao.getUser(email, password)
                                if (user != null) "Успешный вход!"
                                else {
                                    userDao.insert(User(email, password, name = email.split("@").first())) // ← Имя по умолчанию
                                    "Новый пользователь зарегистрирован!"
                                }
                            }
                            message = result
                            currentOnLoginSuccess()
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
            Text(message, color = MaterialTheme.colorScheme.primary)
        }
    }
}