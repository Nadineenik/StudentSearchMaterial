package nadinee.studentmaterialssearch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.lifecycle.ViewModelProvider
import nadinee.studentmaterialssearch.navigation.SetupNavGraph
import nadinee.studentmaterialssearch.ui.theme.StudentMaterialsSearchTheme

class MainActivity : ComponentActivity() {
    private lateinit var authState: AuthState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authState = ViewModelProvider(this)[AuthState::class.java]

        setContent {
            StudentMaterialsSearchTheme {
                val isLoggedIn by authState.isLoggedIn.collectAsState()
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