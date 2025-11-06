// data/User.kt
package nadinee.studentmaterialssearch.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val email: String,
    val password: String,
    val name: String = "Без имени",  // ← Новое
    val interests: String = ""       // ← Через запятую: "kotlin,android,ai"
)