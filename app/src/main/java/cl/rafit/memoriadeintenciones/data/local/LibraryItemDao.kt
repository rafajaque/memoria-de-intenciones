package cl.rafit.memoriadeintenciones.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryItemDao {
    @Query("SELECT * FROM library_items ORDER BY updatedAt DESC")
    fun observeAll(): Flow<List<LibraryItemEntity>>

    @Query("SELECT * FROM library_items WHERE category = :category ORDER BY updatedAt DESC")
    fun observeByCategory(category: String): Flow<List<LibraryItemEntity>>

    @Insert
    suspend fun insert(item: LibraryItemEntity): Long

    @Update
    suspend fun update(item: LibraryItemEntity)

    @Query("DELETE FROM library_items WHERE id = :id")
    suspend fun deleteById(id: Long)
}
