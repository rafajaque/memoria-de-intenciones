package cl.rafit.memoriadeintenciones.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [LibraryItemEntity::class],
    version = 2,
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
                ).addMigrations(MIGRATION_1_2).build().also { instance = it }
            }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE library_items ADD COLUMN durationMinutes INTEGER")
            }
        }
    }
}
