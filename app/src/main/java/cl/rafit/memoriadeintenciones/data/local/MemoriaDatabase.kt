package cl.rafit.memoriadeintenciones.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [LibraryItemEntity::class],
    version = 1,
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
                ).build().also { instance = it }
            }
    }
}
