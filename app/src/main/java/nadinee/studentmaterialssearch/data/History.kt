// Добавляем новую сущность History в data/History.kt
package nadinee.studentmaterialssearch.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "history",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["email"],
            childColumns = ["userEmail"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userEmail")]
)
data class History(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userEmail: String?, // null для гостей
    val url: String,
    val title: String,
    val content: String,
    val viewedAt: Long = System.currentTimeMillis()
)