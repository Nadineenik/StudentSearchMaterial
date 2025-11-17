// screens/SearchViewModel.kt
package nadinee.studentmaterialssearch.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nadinee.studentmaterialssearch.data.SearchResult
import nadinee.studentmaterialssearch.network.SerpStackClient

class SearchViewModel : ViewModel() {
    var results by mutableStateOf<List<SearchResult>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    fun search(query: String) {
        viewModelScope.launch {
            isLoading = true
            error = null
            try {
                results = SerpStackClient.search(query)
            } catch (e: Exception) {
                error = "Ошибка: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // Опционально: очистка ошибки
    fun clearError() { error = null }
}