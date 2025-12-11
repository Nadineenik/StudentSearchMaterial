// AccountScreen.kt — ФИНАЛЬНАЯ ЧИСТАЯ ВЕРСИЯ (ПЛАВНАЯ ТЕМА РАБОТАЕТ!)
package nadinee.studentmaterialssearch.screens

import AuthState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import nadinee.studentmaterialssearch.App
import nadinee.studentmaterialssearch.R
import nadinee.studentmaterialssearch.ui.theme.isAppInDarkTheme
import nadinee.studentmaterialssearch.ui.theme.LocalThemeUpdater

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    authState: AuthState,
    onLogout: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val currentUser by authState.currentUser.collectAsState()
    val context = LocalContext.current

    var isEditing by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var interestsInput by remember { mutableStateOf("") }
    var selectedInterests by remember { mutableStateOf<Set<String>>(emptySet()) }

    // ГЛОБАЛЬНАЯ ТЕМА — ЧИТАЕМ ИЗ CompositionLocal
    val isDark = isAppInDarkTheme()
    val updateTheme = LocalThemeUpdater.current

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
                title = { Text("Мой профиль") },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = if (isDark) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Switch(
                            checked = isDark,
                            onCheckedChange = { enabled ->
                                updateTheme(enabled)  // Плавно меняет тему по всему приложению!
                            }
                        )
                    }

                    IconButton(onClick = {
                        if (isEditing) {
                            scope.launch {
                                val finalPassword = if (password.isBlank()) currentUser?.password else password
                                finalPassword?.let {
                                    App.database.userDao().updateProfile(
                                        email = email,
                                        name = name,
                                        password = it,
                                        interests = selectedInterests.joinToString(",")
                                    )
                                    currentUser?.let { u ->
                                        authState.login(u.copy(name = name, interests = selectedInterests.joinToString(",")))
                                    }
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
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.avatar),
                contentDescription = "Аватар",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(120.dp)
            )
            Spacer(Modifier.height(22.dp))

            if (isEditing) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Имя") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(value = email, onValueChange = {}, label = { Text("Email") }, enabled = false, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Новый пароль") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = interestsInput,
                    onValueChange = { interestsInput = it },
                    label = { Text("Интересы через запятую") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(selectedInterests.toList()) { interest ->
                        InputChip(
                            selected = true,
                            onClick = { selectedInterests = selectedInterests - interest },
                            label = { Text(interest) },
                            trailingIcon = { Icon(Icons.Default.Close, "Удалить", modifier = Modifier.size(18.dp)) }
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = {
                        val newOnes = interestsInput.split(",").map { it.trim() }.filter { it.isNotBlank() }
                        if (newOnes.isNotEmpty()) {
                            selectedInterests += newOnes
                            interestsInput = ""
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) { Text("Добавить") }

            } else {
                Text("Привет, $name!", style = MaterialTheme.typography.titleLarge, fontSize = 28.sp)
                Spacer(Modifier.height(8.dp))
                Text(email, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(32.dp))

                if (selectedInterests.isNotEmpty()) {
                    Text("Интересы", style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.height(12.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(selectedInterests.toList()) { interest ->
                            FilledTonalButton(onClick = {}) { Text(interest) }
                        }
                    }
                    Spacer(Modifier.height(32.dp))
                }

                Button(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth()
                   // colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text("Выйти из аккаунта")//, color = MaterialTheme.colorScheme.onErrorContainer)
                }
            }
        }
    }
}