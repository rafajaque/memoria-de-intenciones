package cl.rafit.memoriadeintenciones.domain

data class RecommendationFeedback(
    val acceptedCount: Int = 0,
    val postponedCount: Int = 0,
    val dismissed: Boolean = false,
    val updatedAt: Long = 0L
)
