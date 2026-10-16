package cl.rafit.memoriadeintenciones.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [LibraryItemEntity::class, ExperienceEntity::class],
    version = 5,
    exportSchema = false
)
abstract class MemoriaDatabase : RoomDatabase() {
    abstract fun libraryItemDao(): LibraryItemDao
    abstract fun experienceDao(): ExperienceDao

    companion object {
        @Volatile
        private var instance: MemoriaDatabase? = null

        fun getInstance(context: Context): MemoriaDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    MemoriaDatabase::class.java,
                    "memoria_de_intenciones.db"
                ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5).build().also { instance = it }
            }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE library_items ADD COLUMN durationMinutes INTEGER")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE library_items ADD COLUMN bookIsbn TEXT")
                db.execSQL("ALTER TABLE library_items ADD COLUMN bookAuthors TEXT")
                db.execSQL("ALTER TABLE library_items ADD COLUMN bookPublisher TEXT")
                db.execSQL("ALTER TABLE library_items ADD COLUMN bookPublishedDate TEXT")
                db.execSQL("ALTER TABLE library_items ADD COLUMN bookLanguage TEXT")
                db.execSQL("ALTER TABLE library_items ADD COLUMN bookPageCount INTEGER")
                db.execSQL("ALTER TABLE library_items ADD COLUMN bookCoverUrl TEXT")
                db.execSQL("ALTER TABLE library_items ADD COLUMN bookEdition TEXT")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE library_items ADD COLUMN bookCoverLocalPath TEXT")
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS item_experiences (itemId INTEGER NOT NULL, platform TEXT, rating INTEGER, comment TEXT, createdAt INTEGER NOT NULL, updatedAt INTEGER NOT NULL, PRIMARY KEY(itemId))")
            }
        }
    }
}
