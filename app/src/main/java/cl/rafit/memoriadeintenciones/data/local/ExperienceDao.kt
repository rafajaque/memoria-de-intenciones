package cl.rafit.memoriadeintenciones.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExperienceDao {
    @Query("SELECT * FROM item_experiences")
    fun observeAll(): Flow<List<ExperienceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(experience: ExperienceEntity)

    @Query("DELETE FROM item_experiences WHERE itemId = :itemId")
    suspend fun deleteByItemId(itemId: Long)
}
