package cl.rafit.memoriadeintenciones.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [LibraryItemEntity::class],
    version = 3,
    exportSchema = false
)
abstract class MemoriaDatabase : RoomDatabase() {
    abstract fun libraryItemDao(): LibraryItemDao

    companion object {
        @Volatile
        private var instance: MemoriaDatabase? = null

        fun getInstance(context: Context): MemoriaDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    MemoriaDatabase::class.java,
                    "memoria_de_intenciones.db"
                ).addMigrations(MIGRATION_1_2, MIGRATION_2_3).build().also { instance = it }
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
    }
}
