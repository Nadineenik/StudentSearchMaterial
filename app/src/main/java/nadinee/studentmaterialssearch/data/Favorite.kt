package nadinee.studentmaterialssearch.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// data/Favorite.kt
@Entity(
    tableName = "favorites",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["email"],
        childColumns = ["userEmail"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["userEmail"])]
)
data class Favorite(
    @PrimaryKey val url: String,
    val userEmail: String,           // ← НОВОЕ ПОЛЕ!
    val title: String,
    val content: String,
    val addedAt: Long = System.currentTimeMillis()
)