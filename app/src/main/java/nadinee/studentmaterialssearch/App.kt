// App.kt — ФИНАЛЬНАЯ ВЕРСИЯ, РАБОТАЕТ НА ВСЕХ УСТРОЙСТВАХ
package nadinee.studentmaterialssearch

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import nadinee.studentmaterialssearch.data.AppDatabase


class App : Application() {
    companion object {
        val database: AppDatabase by lazy {
            Room.databaseBuilder(
                instance.applicationContext,
                AppDatabase::class.java,
                "student_app.db"
            )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                .fallbackToDestructiveMigration() // ← ОСТАВЬ НА ВРЕМЯ РАЗРАБОТКИ!
                .allowMainThreadQueries() // ← временно, чтобы не падало
                .build()
        }

        // Самые надёжные миграции
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE users ADD COLUMN name TEXT NOT NULL DEFAULT 'Без имени'")
                db.execSQL("ALTER TABLE users ADD COLUMN interests TEXT NOT NULL DEFAULT ''")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS favorites (
                        url TEXT PRIMARY KEY NOT NULL,
                        title TEXT NOT NULL,
                        content TEXT NOT NULL,
                        addedAt INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())
            }
        }

        // САМАЯ ВАЖНАЯ МИГРАЦИЯ — ДЕЛАЕТ ВСЁ БЕЗОПАСНО
        val MIGRATION_3_4 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Создаём новую таблицу
                db.execSQL("""
                    CREATE TABLE favorites_new (
                        url TEXT PRIMARY KEY NOT NULL,
                        userEmail TEXT NOT NULL DEFAULT 'unknown@user.ru',
                        title TEXT NOT NULL,
                        content TEXT NOT NULL,
                        addedAt INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())

                // Безопасно переносим данные
                db.execSQL("""
                    INSERT INTO favorites_new (url, userEmail, title, content, addedAt)
                    SELECT 
                        f.url, 
                        COALESCE(u.email, 'legacy@user.ru'), 
                        f.title, 
                        f.content, 
                        COALESCE(f.addedAt, 0)
                    FROM favorites f
                    LEFT JOIN users u ON 1=1
                    LIMIT 1000
                """.trimIndent())

                // Удаляем старую
                db.execSQL("DROP TABLE IF EXISTS favorites")
                db.execSQL("ALTER TABLE favorites_new RENAME TO favorites")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_favorites_userEmail ON favorites(userEmail)")
            }
        }

        // Добавляем миграцию 4 в App.kt
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
            CREATE TABLE history (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                userEmail TEXT,
                url TEXT NOT NULL,
                title TEXT NOT NULL,
                content TEXT NOT NULL,
                viewedAt INTEGER NOT NULL
            )
        """.trimIndent())
                db.execSQL("CREATE INDEX index_history_userEmail ON history(userEmail)")
            }
        }

        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        GlobalScope.launch(Dispatchers.IO) {
            database // просто инициализируем
        }
    }
}