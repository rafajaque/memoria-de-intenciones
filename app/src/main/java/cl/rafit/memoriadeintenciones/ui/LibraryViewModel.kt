package cl.rafit.memoriadeintenciones.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.rafit.memoriadeintenciones.data.LibraryRepository
import cl.rafit.memoriadeintenciones.domain.LibraryCategory
import cl.rafit.memoriadeintenciones.domain.LibraryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val repository: LibraryRepository
) : ViewModel() {
    val items = repository.observeAll().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun itemsFor(category: LibraryCategory): Flow<List<LibraryItem>> =
        repository.observeByCategory(category)

    fun add(item: LibraryItem) {
        viewModelScope.launch { repository.insert(item) }
    }

    fun update(item: LibraryItem) {
        viewModelScope.launch { repository.update(item) }
    }

    fun delete(id: Long) {
        viewModelScope.launch { repository.delete(id) }
    }
}
