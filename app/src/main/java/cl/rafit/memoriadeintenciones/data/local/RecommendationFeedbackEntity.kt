package cl.rafit.memoriadeintenciones.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import cl.rafit.memoriadeintenciones.domain.RecommendationFeedback

@Entity(tableName = "recommendation_feedback")
data class RecommendationFeedbackEntity(
    @PrimaryKey val itemId: Long,
    val acceptedCount: Int,
    val postponedCount: Int,
    val dismissed: Boolean,
    val updatedAt: Long
)

fun RecommendationFeedbackEntity.toDomain() = RecommendationFeedback(
    acceptedCount = acceptedCount,
    postponedCount = postponedCount,
    dismissed = dismissed,
    updatedAt = updatedAt
)

fun RecommendationFeedback.toEntity(itemId: Long) = RecommendationFeedbackEntity(
    itemId = itemId,
    acceptedCount = acceptedCount,
    postponedCount = postponedCount,
    dismissed = dismissed,
    updatedAt = updatedAt
)
