package cl.rafit.memoriadeintenciones.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecommendationFeedbackDao {
    @Query("SELECT * FROM recommendation_feedback")
    fun observeAll(): Flow<List<RecommendationFeedbackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(feedback: RecommendationFeedbackEntity)

    @Query("DELETE FROM recommendation_feedback WHERE itemId = :itemId")
    suspend fun deleteByItemId(itemId: Long)
}
