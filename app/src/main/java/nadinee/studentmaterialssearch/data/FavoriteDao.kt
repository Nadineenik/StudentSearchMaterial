package nadinee.studentmaterialssearch.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(favorite: Favorite)

    @Query("SELECT * FROM favorites ORDER BY addedAt DESC")
    suspend fun getAll(): List<Favorite>

    @Query("DELETE FROM favorites WHERE url = :url")
    suspend fun remove(url: String)

    @Query("SELECT * FROM favorites WHERE url = :url LIMIT 1")
    suspend fun getByUrl(url: String): Favorite?
}