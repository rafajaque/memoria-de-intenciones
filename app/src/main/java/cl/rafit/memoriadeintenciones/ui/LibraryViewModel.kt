package cl.rafit.memoriadeintenciones.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.rafit.memoriadeintenciones.data.LibraryRepository
import cl.rafit.memoriadeintenciones.domain.LibraryCategory
import cl.rafit.memoriadeintenciones.domain.LibraryItem
import cl.rafit.memoriadeintenciones.domain.LibraryStatus
import cl.rafit.memoriadeintenciones.domain.BookMetadata
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class LibraryUiState(val items: List<LibraryItem> = emptyList()) {
    fun itemsFor(category: LibraryCategory): List<LibraryItem> =
        items.filter { it.category == category }

    fun countFor(category: LibraryCategory): Int = items.count { it.category == category }
}

class LibraryViewModel(
    private val repository: LibraryRepository
) : ViewModel() {
    val uiState = repository.observeAll()
        .map(::LibraryUiState)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = LibraryUiState()
        )

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
        repository.add(
            title = title,
            category = category,
            status = status,
            durationMinutes = durationMinutes,
            notes = notes,
            bookMetadata = bookMetadata,
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
        repository.edit(
            item = item,
            title = title,
            status = status,
            durationMinutes = durationMinutes,
            notes = notes,
            bookMetadata = bookMetadata,
            now = System.currentTimeMillis()
        )
    }

    suspend fun delete(id: Long): Result<Unit> = runCatching { repository.delete(id) }
}
