package cl.rafit.memoriadeintenciones.domain

data class ItemExperience(
    val platform: String? = null,
    val rating: Int? = null,
    val comment: String? = null,
    val startedAt: Long? = null,
    val finishedAt: Long? = null,
    val createdAt: Long = 0,
    val updatedAt: Long = 0
)
