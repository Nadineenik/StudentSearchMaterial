package nadinee.studentmaterialssearch

// AuthState.kt
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AuthState : ViewModel() {
    var isLoggedIn by mutableStateOf(false)
        private set

    fun login() {
        viewModelScope.launch {
            isLoggedIn = true
        }
    }

    fun logout() {
        viewModelScope.launch {
            isLoggedIn = false
        }
    }
}