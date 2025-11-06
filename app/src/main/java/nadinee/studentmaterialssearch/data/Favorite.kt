package nadinee.studentmaterialssearch.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class Favorite(
    @PrimaryKey val url: String,
    val title: String,
    val content: String,
    val addedAt: Long = System.currentTimeMillis()
)