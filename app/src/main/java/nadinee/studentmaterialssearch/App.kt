// App.kt
package nadinee.studentmaterialssearch

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import nadinee.studentmaterialssearch.data.AppDatabase

// App.kt
class App : Application() {
    companion object {
        val database: AppDatabase by lazy {
            Room.databaseBuilder(
                instance.applicationContext,
                AppDatabase::class.java,
                "student_app.db"
            )
                .addMigrations(MIGRATION_1_2 as Migration)
                .build()
        }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE users ADD COLUMN name TEXT NOT NULL DEFAULT 'Без имени'")
                db.execSQL("ALTER TABLE users ADD COLUMN interests TEXT NOT NULL DEFAULT ''")
            }
        }

        lateinit var instance: App
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        GlobalScope.launch(Dispatchers.IO) { database }
    }
}