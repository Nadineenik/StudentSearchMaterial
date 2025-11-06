// AccountScreen.kt
package nadinee.studentmaterialssearch.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import nadinee.studentmaterialssearch.App
import nadinee.studentmaterialssearch.R
import nadinee.studentmaterialssearch.data.User
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(onLogout: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var user by remember { mutableStateOf<User?>(null) }
    var isEditing by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var interestsInput by remember { mutableStateOf("") }
    var selectedInterests by remember { mutableStateOf(setOf<String>()) }

    // Загружаем пользователя
    LaunchedEffect(Unit) {
        scope.launch {
            val userDao = App.database.userDao()
            val users = userDao.getAllUsers()
            user = users.firstOrNull()
            user?.let {
                name = it.name
                email = it.email
                password = it.password
                selectedInterests = it.interests.split(",").filter { s -> s.isNotBlank() }.toSet()
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Мой профиль") },
                actions = {
                    IconButton(onClick = { isEditing = !isEditing }) {
                        Icon(
                            if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                            contentDescription = if (isEditing) "Сохранить" else "Редактировать"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Аватар
            Icon(
                painter = painterResource(R.drawable.avatar),
                contentDescription = "Аватар",
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(24.dp))

            if (user != null) {
                if (isEditing) {
                    // === РЕДАКТИРОВАНИЕ ===
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Имя") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        enabled = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Пароль") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = interestsInput,
                        onValueChange = { interestsInput = it },
                        label = { Text("Интересы (через запятую)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            val newInterests = interestsInput.split(",").map { it.trim() }.filter { it.isNotBlank() }
                            selectedInterests = newInterests.toSet()
                            interestsInput = ""
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Добавить")
                    }

                    Spacer(Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(selectedInterests.toList()) { interest ->
                            InputChip(
                                selected = true,
                                onClick = { selectedInterests = selectedInterests - interest },
                                label = { Text(interest) }
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = {
                            scope.launch {
                                val userDao = App.database.userDao()
                                userDao.updateProfile(
                                    email = email,
                                    name = name,
                                    interests = selectedInterests.joinToString(",")
                                )
                                isEditing = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Сохранить")
                    }
                } else {
                    // === ПРОСМОТР ===
                    Text("Привет, $name!", style = MaterialTheme.typography.titleLarge, fontSize = 22.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(email, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(16.dp))

                    if (selectedInterests.isNotEmpty()) {
                        Text("Интересы:", style = MaterialTheme.typography.labelLarge)
                        Spacer(Modifier.height(8.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(selectedInterests.toList()) { interest ->
                                FilledTonalButton(onClick = { }) {
                                    Text(interest)
                                }
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    }

                    Button(
                        onClick = onLogout,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Выйти из аккаунта")
                    }
                }
            } else {
                CircularProgressIndicator()
            }
        }
    }
}