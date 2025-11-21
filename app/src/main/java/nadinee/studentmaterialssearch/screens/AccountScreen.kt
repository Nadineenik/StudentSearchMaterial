// AccountScreen.kt — обновлённая версия с защитой от сжатия чипов
package nadinee.studentmaterialssearch.screens

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
import nadinee.studentmaterialssearch.data.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(onLogout: () -> Unit) {
    val scope = rememberCoroutineScope()

    var user by remember { mutableStateOf<User?>(null) }
    var isEditing by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var interestsInput by remember { mutableStateOf("") }
    var selectedInterests by remember { mutableStateOf(setOf<String>()) }

    // Загрузка данных
    LaunchedEffect(Unit) {
        scope.launch {
            val loadedUser = App.database.userDao().getAllUsers().firstOrNull()
            user = loadedUser
            loadedUser?.let {
                name = it.name
                email = it.email
                password = it.password
                selectedInterests = it.interests
                    .split(",")
                    .map { s -> s.trim() }
                    .filter { it.isNotBlank() }
                    .toSet()
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Мой профиль") },
                actions = {
                    IconButton(onClick = {
                        if (isEditing) {
                            scope.launch {
                                val finalPassword =
                                    if (password.isBlank()) user?.password ?: "" else password

                                App.database.userDao().updateProfile(
                                    email = email,
                                    name = name,
                                    password = finalPassword,
                                    interests = selectedInterests.joinToString(",")
                                )

                                val updated = App.database.userDao().getAllUsers().firstOrNull()
                                user = updated
                                updated?.let {
                                    name = it.name
                                    password = it.password
                                    selectedInterests = it.interests
                                        .split(",")
                                        .map { s -> s.trim() }
                                        .filter { it.isNotBlank() }
                                        .toSet()
                                }
                            }
                        }
                        isEditing = !isEditing
                    }) {
                        Icon(
                            imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                            contentDescription = null
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
            Image(
                painter = painterResource(R.drawable.avatar),
                contentDescription = "Аватар",
                modifier = Modifier.size(120.dp)
            )

            Spacer(Modifier.height(32.dp))

            if (user != null) {
                if (isEditing) {
                    // === РЕДАКТИРОВАНИЕ ===
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Имя") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(10.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = {},
                        label = { Text("Email") },
                        enabled = false,
                        modifier = Modifier.fillMaxWidth()
                    )
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

                    Spacer(Modifier.height(10.dp))

                    // === ЧИПЫ (жёсткая ширина + читаемость) ===
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        items(selectedInterests.toList()) { interest ->
                            // Surface с минимальной шириной и высотой — не даст чипам сжиматься
                            Surface(
                                shape = MaterialTheme.shapes.medium,
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                tonalElevation = 2.dp,
                                modifier = Modifier
                                    .widthIn(min = 100.dp)   // ← Жёсткая минимальная ширина
                                    .height(20.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .padding(horizontal = 10.dp)
                                        .fillMaxHeight()
                                ) {
                                    // Текст не будет переноситься и покажет троеточие, если не влезает
                                    Text(
                                        text = interest,
                                        fontSize = 10.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier
                                            .weight(1f)
                                    )

                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Удалить",
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clickable { selectedInterests = selectedInterests - interest }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    Button(
                        onClick = {
                            val newOnes = interestsInput.split(",")
                                .map { it.trim() }
                                .filter { it.isNotBlank() }
                            selectedInterests = selectedInterests + newOnes
                            interestsInput = ""
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Добавить")
                    }


                } else {
                    // === ПРОСМОТР ===
                    Text("Привет, $name!", style = MaterialTheme.typography.titleLarge, fontSize = 22.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(email, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(24.dp))

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
