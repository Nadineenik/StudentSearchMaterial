// Полный MainActivity.kt
package nadinee.studentmaterialssearch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import nadinee.studentmaterialssearch.navigation.SetupNavGraph
import nadinee.studentmaterialssearch.ui.theme.StudentMaterialsSearchTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authState = ViewModelProvider(this)[AuthState::class.java]

        setContent {
            StudentMaterialsSearchTheme {
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