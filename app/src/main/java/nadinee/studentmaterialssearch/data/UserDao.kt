package nadinee.studentmaterialssearch.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Query("SELECT * FROM users WHERE email = :email AND password = :password")
    suspend fun getUser(email: String, password: String): User?

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    @Query("UPDATE users SET name = :name, interests = :interests WHERE email = :email")
    suspend fun updateProfile(email: String, name: String, interests: String)
}