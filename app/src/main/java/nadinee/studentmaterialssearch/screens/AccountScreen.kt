// AccountScreen.kt — ФИНАЛЬНАЯ, РАБОЧАЯ, НЕ ЛОМАЕТ ИНТЕРЕСЫ
package nadinee.studentmaterialssearch.screens

import AuthState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import nadinee.studentmaterialssearch.App
import nadinee.studentmaterialssearch.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    authState: AuthState,
    onLogout: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val currentUser by authState.currentUser.collectAsState()

    var isEditing by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var interestsInput by remember { mutableStateOf("") }

    // ← КЛЮЧЕВОЙ МОМЕНТ: состояние интересов НЕ привязано к currentUser!
    var selectedInterests by remember { mutableStateOf<Set<String>>(emptySet()) }

    // Загружаем данные при появлении пользователя
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            name = user.name
            email = user.email
            selectedInterests = user.interests
                .split(",")
                .map { it.trim() }
                .filter { it.isNotBlank() }
                .toSet()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(",my профиль") },
                actions = {
                    IconButton(onClick = {
                        if (isEditing) {
                            scope.launch {
                                val finalPassword = if (password.isBlank()) {
                                    currentUser?.password ?: return@launch
                                } else password

                                App.database.userDao().updateProfile(
                                    email = email,
                                    name = name,
                                    password = finalPassword,
                                    interests = selectedInterests.joinToString(",")
                                )

                                // Обновляем AuthState
                                currentUser?.let {
                                    authState.login(it.copy(
                                        name = name,
                                        interests = selectedInterests.joinToString(",")
                                    ))
                                }
                            }
                        }
                        isEditing = !isEditing
                    }) {
                        Icon(
                            imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                            contentDescription = if (isEditing) "Сохранить" else "Редактировать"
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (currentUser == null) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.avatar),
                contentDescription = "Аватар",
                modifier = Modifier.size(120.dp)
            )
            Spacer(Modifier.height(32.dp))

            if (isEditing) {
                // === РЕДАКТИРОВАНИЕ ===
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Имя") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(value = email, onValueChange = {}, label = { Text("Email") }, enabled = false, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Новый пароль (оставьте пустым — не менять)") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(14.dp))

                OutlinedTextField(
                    value = interestsInput,
                    onValueChange = { interestsInput = it },
                    label = { Text("Интересы (через запятую)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))

                // Чипы — красиво и не сжимаются
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(selectedInterests.toList()) { interest ->
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.height(36.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = interest,
                                    fontSize = 14.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Удалить",
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clickable { selectedInterests = selectedInterests - interest }
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = {
                        val newOnes = interestsInput.split(",").map { it.trim() }.filter { it.isNotBlank() }
                        selectedInterests = selectedInterests + newOnes
                        interestsInput = ""
                    },
                    modifier = Modifier.align(Alignment.End)
                ) { Text("Добавить") }

            } else {
                // === ПРОСМОТР ===
                Text("Привет, $name!", style = MaterialTheme.typography.titleLarge, fontSize = 24.sp)
                Spacer(Modifier.height(8.dp))
                Text(email, style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(24.dp))

                if (selectedInterests.isNotEmpty()) {
                    Text("Интересы:", style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(selectedInterests.toList()) { interest ->
                            FilledTonalButton(onClick = {}) { Text(interest) }
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                }

                Button(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
                    Text("Выйти из аккаунта")
                }
            }
        }
    }
}