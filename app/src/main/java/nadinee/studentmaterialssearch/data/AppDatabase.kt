package nadinee.studentmaterialssearch.data

import androidx.room.Database
import androidx.room.RoomDatabase

// data/AppDatabase.kt
// Обновляем AppDatabase.kt
@Database(entities = [User::class, Favorite::class, History::class], version = 5) // + History, version +1
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun historyDao(): HistoryDao
}