import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import nadinee.studentmaterialssearch.data.User

class AuthState : ViewModel() {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    val isLoggedIn: StateFlow<Boolean> = _currentUser
        .asStateFlow()
        .map { it != null }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun login(user: User) {
        viewModelScope.launch {
            _currentUser.emit(user)
        }
    }

    fun logout() {
        viewModelScope.launch {
            _currentUser.emit(null)
        }
    }
}