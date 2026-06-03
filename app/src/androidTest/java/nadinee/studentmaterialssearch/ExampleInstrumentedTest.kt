package nadinee.studentmaterialssearch

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun fullSearchFlowTest() {

        // 1️⃣ Переход через нижнюю панель на экран поиска
        composeTestRule
            .onNodeWithTag("bottom_search")
            .performClick()

        composeTestRule.waitForIdle()

        // 2️⃣ Проверяем, что поле ввода отображается
        composeTestRule
            .onNodeWithTag("searchInput")
            .assertIsDisplayed()

        // 3️⃣ Вводим текст
        composeTestRule
            .onNodeWithTag("searchInput")
            .performTextInput("Kotlin")

        composeTestRule
            .onNodeWithTag("searchInput")
            .assertTextContains("Kotlin")

        // 4️⃣ Замеряем время выполнения поиска
        val startTime = System.currentTimeMillis()

        composeTestRule
            .onNodeWithTag("searchButton")
            .performClick()

        // Ждём завершения UI
        composeTestRule.waitForIdle()

        val duration = System.currentTimeMillis() - startTime

        // 5️⃣ Проверка, что поиск уложился в 3 секунды
        assertTrue(
            "Поиск выполняется дольше 3 секунд: $duration ms",
            duration <= 3000
        )

        // 6️⃣ Проверяем, что появились результаты (или хотя бы LazyColumn)
        composeTestRule
            .onNodeWithText("Введите запрос и нажмите «Искать»")
            .assertDoesNotExist()
    }

    @Test
    fun searchButtonDisabledWhenQueryIsEmpty() {

        composeTestRule
            .onNodeWithTag("bottom_search")
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag("searchButton")
            .assertIsNotEnabled()
    }
    @Test
    fun longQueryDoesNotCrashApp() {

        composeTestRule
            .onNodeWithTag("bottom_search")
            .performClick()

        composeTestRule.waitForIdle()

        val longQuery = "A".repeat(300)

        composeTestRule
            .onNodeWithTag("searchInput")
            .performTextInput(longQuery)

        composeTestRule
            .onNodeWithTag("searchButton")
            .assertIsEnabled()

        composeTestRule
            .onNodeWithTag("searchButton")
            .performClick()

        composeTestRule.waitForIdle()

        // Проверяем, что экран не упал и поле всё ещё существует
        composeTestRule
            .onNodeWithTag("searchInput")
            .assertIsDisplayed()
    }
}