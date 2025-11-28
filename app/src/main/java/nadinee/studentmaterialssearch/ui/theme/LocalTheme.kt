// ui/theme/LocalTheme.kt
package nadinee.studentmaterialssearch.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf

// Глобальное состояние темы
val LocalAppTheme = compositionLocalOf { false } // isDark

// Для принудительного обновления (если нужно)
val LocalThemeUpdater = staticCompositionLocalOf<(Boolean) -> Unit> {
    { _ -> }
}