import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthState : ViewModel() {
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    fun login() {
        viewModelScope.launch {
            _isLoggedIn.emit(true)
        }
    }

    fun logout() {
        viewModelScope.launch {
            _isLoggedIn.emit(false)
        }
    }
}


