package nadinee.studentmaterialssearch.data

import androidx.room.Database
import androidx.room.RoomDatabase

// data/AppDatabase.kt
@Database(entities = [User::class, Favorite::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun favoriteDao(): FavoriteDao
}