package cl.rafit.memoriadeintenciones

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import cl.rafit.memoriadeintenciones.domain.LibraryCategory
import cl.rafit.memoriadeintenciones.domain.RecommendationPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class StoredRecommendationPreferences(
    val lastAvailableMinutes: Int? = null,
    val engine: RecommendationPreferences = RecommendationPreferences()
)

class RecommendationPreferencesStore(private val dataStore: DataStore<Preferences>) {
    private val lastMinutesKey = intPreferencesKey("recommendation_last_minutes")
    private val affinityKeys = LibraryCategory.entries.associateWith { category ->
        stringPreferencesKey("recommendation_affinity_${category.name.lowercase()}")
    }

    val preferences: Flow<StoredRecommendationPreferences> = dataStore.data.map { values ->
        StoredRecommendationPreferences(
            lastAvailableMinutes = values[lastMinutesKey],
            engine = RecommendationPreferences(
                categoryAffinity = affinityKeys.mapNotNull { (category, key) ->
                    values[key]?.toIntOrNull()?.let { category to it }
                }.toMap()
            )
        )
    }

    suspend fun saveLastAvailableMinutes(minutes: Int) {
        require(minutes > 0) { "El tiempo disponible debe ser mayor que cero" }
        dataStore.edit { values -> values[lastMinutesKey] = minutes }
    }
}
