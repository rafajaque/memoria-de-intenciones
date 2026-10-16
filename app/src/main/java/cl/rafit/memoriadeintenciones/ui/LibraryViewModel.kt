package cl.rafit.memoriadeintenciones.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.rafit.memoriadeintenciones.RecommendationPreferencesStore
import cl.rafit.memoriadeintenciones.domain.Recommendation
import cl.rafit.memoriadeintenciones.domain.RecommendationEngine
import cl.rafit.memoriadeintenciones.domain.RecommendationPriority
import cl.rafit.memoriadeintenciones.data.LibraryRepository
import cl.rafit.memoriadeintenciones.data.catalog.BookCatalogRepository
import cl.rafit.memoriadeintenciones.data.local.BookCoverStorage
import cl.rafit.memoriadeintenciones.domain.BookCatalogProposal
import cl.rafit.memoriadeintenciones.domain.LibraryCategory
import cl.rafit.memoriadeintenciones.domain.LibraryItem
import cl.rafit.memoriadeintenciones.domain.LibraryStatus
import cl.rafit.memoriadeintenciones.domain.BookMetadata
import cl.rafit.memoriadeintenciones.domain.ItemExperience
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class LibraryUiState(val items: List<LibraryItem> = emptyList()) {
    fun itemsFor(category: LibraryCategory): List<LibraryItem> =
        items.filter { it.category == category }

    fun countFor(category: LibraryCategory): Int = items.count { it.category == category }
}

data class RecommendationUiState(
    val inputMinutes: String = "",
    val recommendations: List<Recommendation> = emptyList(),
    val error: String? = null
)

class LibraryViewModel(
    private val repository: LibraryRepository,
    private val bookCatalogRepository: BookCatalogRepository,
    private val bookCoverStorage: BookCoverStorage,
    private val recommendationPreferencesStore: RecommendationPreferencesStore
) : ViewModel() {

    suspend fun findBookByIsbn(isbn: String): Result<List<BookCatalogProposal>> = runCatching {
        bookCatalogRepository.findByIsbn(isbn)
    }
    val uiState = repository.observeAll()
        .map(::LibraryUiState)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = LibraryUiState()
        )

    private val recommendationInputMinutes = MutableStateFlow("")
    val recommendationUiState = combine(
        uiState,
        recommendationInputMinutes,
        recommendationPreferencesStore.preferences
    ) { library, input, storedPreferences ->
        val minutes = input.toIntOrNull()
        val effectiveMinutes = minutes ?: storedPreferences.lastAvailableMinutes
        if (effectiveMinutes == null) {
            RecommendationUiState(inputMinutes = input)
        } else if (effectiveMinutes <= 0 || (input.isNotBlank() && minutes == null)) {
            RecommendationUiState(inputMinutes = input, error = "Escribe un número de minutos mayor que cero.")
        } else {
            RecommendationUiState(
                inputMinutes = input.ifBlank { effectiveMinutes.toString() },
                recommendations = RecommendationEngine.rank(
                    items = library.items,
                    availableMinutes = effectiveMinutes,
                    asOfMillis = System.currentTimeMillis(),
                    preferences = storedPreferences.engine
                )
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RecommendationUiState()
    )

    fun setRecommendationMinutes(value: String) {
        recommendationInputMinutes.value = value.filter(Char::isDigit)
        val minutes = recommendationInputMinutes.value.toIntOrNull()
        if (minutes != null && minutes > 0) {
            viewModelScope.launch { recommendationPreferencesStore.saveLastAvailableMinutes(minutes) }
        }
    }

    suspend fun acceptRecommendation(itemId: Long): Result<Unit> = runCatching {
        repository.recordRecommendationAccepted(itemId)
    }

    suspend fun postponeRecommendation(itemId: Long): Result<Unit> = runCatching {
        repository.recordRecommendationPostponed(itemId)
    }

    suspend fun dismissRecommendation(itemId: Long): Result<Unit> = runCatching {
        repository.recordRecommendationDismissed(itemId)
    }

    suspend fun setRecommendationPriority(itemId: Long, priority: RecommendationPriority): Result<Unit> = runCatching {
        repository.setRecommendationPriority(itemId, priority)
    }

    suspend fun add(
        title: String,
        category: LibraryCategory,
        status: LibraryStatus,
        durationMinutes: Int?,
        notes: String?
    ): Result<Long> = add(title, category, status, durationMinutes, notes, null)

    suspend fun add(
        title: String,
        category: LibraryCategory,
        status: LibraryStatus,
        durationMinutes: Int?,
        notes: String?,
        bookMetadata: BookMetadata?
    ): Result<Long> = runCatching {
        val storedMetadata = bookMetadata?.let { storeCover(it) }
        repository.add(
            title = title,
            category = category,
            status = status,
            durationMinutes = durationMinutes,
            notes = notes,
            bookMetadata = storedMetadata,
            now = System.currentTimeMillis()
        )
    }

    suspend fun update(item: LibraryItem) = repository.update(item)

    suspend fun edit(
        item: LibraryItem,
        title: String,
        status: LibraryStatus,
        durationMinutes: Int?,
        notes: String?
    ): Result<Unit> = edit(item, title, status, durationMinutes, notes, item.bookMetadata)

    suspend fun edit(
        item: LibraryItem,
        title: String,
        status: LibraryStatus,
        durationMinutes: Int?,
        notes: String?,
        bookMetadata: BookMetadata?
    ): Result<Unit> = runCatching {
        val previousLocalCover = item.bookMetadata?.coverLocalPath
        val storedMetadata = bookMetadata?.let { metadata ->
            val sameRemoteCover = metadata.coverUrl == item.bookMetadata?.coverUrl
            if (metadata.coverLocalPath != null || (sameRemoteCover && previousLocalCover != null) || metadata.coverUrl == null) metadata
            else storeCover(metadata)
        }
        repository.edit(
            item = item,
            title = title,
            status = status,
            durationMinutes = durationMinutes,
            notes = notes,
            bookMetadata = storedMetadata,
            now = System.currentTimeMillis()
        )
        if (previousLocalCover != storedMetadata?.coverLocalPath) bookCoverStorage.delete(previousLocalCover)
    }

    suspend fun delete(id: Long): Result<Unit> = runCatching {
        val localCover = uiState.value.items.firstOrNull { it.id == id }?.bookMetadata?.coverLocalPath
        repository.delete(id)
        bookCoverStorage.delete(localCover)
    }

    suspend fun saveExperience(itemId: Long, experience: ItemExperience): Result<Unit> =
        runCatching { repository.saveExperience(itemId, experience) }

    suspend fun deleteExperience(itemId: Long): Result<Unit> =
        runCatching { repository.deleteExperience(itemId) }

    private suspend fun storeCover(metadata: BookMetadata): BookMetadata =
        metadata.copy(coverLocalPath = bookCoverStorage.download(metadata.coverUrl, metadata.isbn))
}
