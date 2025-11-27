// ui/theme/Theme.kt — ФИНАЛЬНАЯ РАБОЧАЯ ВЕРСИЯ (БЕЗ ОШТЫРЬ!)
package nadinee.studentmaterialssearch.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import nadinee.studentmaterialssearch.data.ThemePreferences

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun StudentMaterialsSearchTheme(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val systemIsDark = isSystemInDarkTheme()

    // ← ЧИТАЕМ ПРЕДПОЧТЕНИЯ ТОЛЬКО ВНУТРИ @Composable!
    val userChoice = ThemePreferences.getTheme(context)
    val isDark = userChoice ?: systemIsDark

    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (isDark) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        isDark -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}