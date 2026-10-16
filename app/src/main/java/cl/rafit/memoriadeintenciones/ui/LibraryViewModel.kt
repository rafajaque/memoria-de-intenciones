package cl.rafit.memoriadeintenciones.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.rafit.memoriadeintenciones.data.LibraryRepository
import cl.rafit.memoriadeintenciones.data.catalog.BookCatalogRepository
import cl.rafit.memoriadeintenciones.data.local.BookCoverStorage
import cl.rafit.memoriadeintenciones.domain.BookCatalogProposal
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
    private val repository: LibraryRepository,
    private val bookCatalogRepository: BookCatalogRepository,
    private val bookCoverStorage: BookCoverStorage
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

    private suspend fun storeCover(metadata: BookMetadata): BookMetadata =
        metadata.copy(coverLocalPath = bookCoverStorage.download(metadata.coverUrl, metadata.isbn))
}
