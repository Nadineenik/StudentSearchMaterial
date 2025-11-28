// MainActivity.kt — ФИНАЛЬНАЯ ВЕРСИЯ!
package nadinee.studentmaterialssearch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import nadinee.studentmaterialssearch.data.ThemePreferences
import nadinee.studentmaterialssearch.navigation.SetupNavGraph
import nadinee.studentmaterialssearch.ui.theme.LocalAppTheme
import nadinee.studentmaterialssearch.ui.theme.LocalThemeUpdater
import nadinee.studentmaterialssearch.ui.theme.StudentMaterialsSearchTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authState = ViewModelProvider(this)[AuthState::class.java]

        setContent {
            val context = LocalContext.current
            val systemIsDark = isSystemInDarkTheme()
            var isDark by remember { mutableStateOf(ThemePreferences.getTheme(context) ?: systemIsDark) }

            // Обновляем при смене системной темы (если пользователь не выбирал вручную)
            LaunchedEffect(systemIsDark) {
                if (ThemePreferences.getTheme(context) == null) {
                    isDark = systemIsDark
                }
            }

            CompositionLocalProvider(
                LocalThemeUpdater provides { newValue ->
                    isDark = newValue
                    ThemePreferences.setDarkTheme(context, newValue)
                }
            ) {
                StudentMaterialsSearchTheme(isDark = isDark) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        SetupNavGraph(authState = authState)
                    }
                }
            }
        }
    }
}