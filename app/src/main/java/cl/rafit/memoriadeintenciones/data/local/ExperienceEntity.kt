package cl.rafit.memoriadeintenciones.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import cl.rafit.memoriadeintenciones.domain.ItemExperience

@Entity(tableName = "item_experiences")
data class ExperienceEntity(
    @PrimaryKey val itemId: Long,
    val platform: String?,
    val rating: Int?,
    val comment: String?,
    val createdAt: Long,
    val updatedAt: Long
)

fun ExperienceEntity.toDomain() = ItemExperience(platform, rating, comment, createdAt, updatedAt)

fun ItemExperience.toEntity(itemId: Long) = ExperienceEntity(itemId, platform, rating, comment, createdAt, updatedAt)
