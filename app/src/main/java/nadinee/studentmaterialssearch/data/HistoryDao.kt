// Добавляем DAO для History в data/HistoryDao.kt
package nadinee.studentmaterialssearch.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun add(history: History)

    @Query("SELECT * FROM history WHERE userEmail = :userEmail OR (userEmail IS NULL AND userEmail = :userEmail) ORDER BY viewedAt DESC")
    suspend fun getAllForUser(userEmail: String?): List<History>

    @Query("DELETE FROM history WHERE userEmail IS NULL")
    suspend fun clearGuestHistory()
}