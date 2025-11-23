package nadinee.studentmaterialssearch.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

// data/FavoriteDao.kt
@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(favorite: Favorite)

    @Query("SELECT * FROM favorites WHERE userEmail = :userEmail ORDER BY addedAt DESC")
    suspend fun getAllForUser(userEmail: String): List<Favorite>

    @Query("DELETE FROM favorites WHERE url = :url AND userEmail = :userEmail")
    suspend fun remove(url: String, userEmail: String)

    @Query("SELECT * FROM favorites WHERE url = :url AND userEmail = :userEmail LIMIT 1")
    suspend fun getByUrl(url: String, userEmail: String): Favorite?

    // Опционально: очистка при выходе
    @Query("DELETE FROM favorites WHERE userEmail = :userEmail")
    suspend fun clearForUser(userEmail: String)
}