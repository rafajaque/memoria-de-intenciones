package cl.rafit.memoriadeintenciones.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import cl.rafit.memoriadeintenciones.data.local.MemoriaDatabase
import cl.rafit.memoriadeintenciones.domain.LibraryCategory
import cl.rafit.memoriadeintenciones.domain.LibraryStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RoomLibraryRepositoryTest {
    private lateinit var database: MemoriaDatabase
    private lateinit var repository: LibraryRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, MemoriaDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = RoomLibraryRepository(database.libraryItemDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun addDraftNormalizesAndPersistsInItsCategory() = runBlocking {
        repository.add(
            title = "  El viaje de Chihiro  ",
            category = LibraryCategory.MOVIE,
            status = LibraryStatus.TO_CONSUME,
            durationMinutes = 125,
            notes = "   ",
            now = 42L
        )

        val movie = repository.observeByCategory(LibraryCategory.MOVIE).first().single()

        assertEquals("El viaje de Chihiro", movie.title)
        assertEquals(LibraryCategory.MOVIE, movie.category)
        assertEquals(LibraryStatus.TO_CONSUME, movie.status)
        assertEquals(125, movie.durationMinutes)
        assertNull(movie.notes)
        assertEquals(42L, movie.createdAt)
        assertEquals(42L, movie.updatedAt)
        assertEquals(emptyList<Any>(), repository.observeByCategory(LibraryCategory.BOOK).first())
    }

    @Test
    fun editPreservesIdentityThenDeleteRemovesTheTitle() = runBlocking {
        repository.add(
            title = "La llegada",
            category = LibraryCategory.MOVIE,
            status = LibraryStatus.TO_CONSUME,
            durationMinutes = 116,
            notes = null,
            now = 10L
        )
        val original = repository.observeAll().first().single()

        repository.edit(
            item = original,
            title = "  La llegada (vista)  ",
            status = LibraryStatus.COMPLETED,
            durationMinutes = null,
            notes = "  Volver a verla  ",
            now = 20L
        )

        val edited = repository.observeAll().first().single()
        assertEquals(original.id, edited.id)
        assertEquals("La llegada (vista)", edited.title)
        assertEquals(LibraryCategory.MOVIE, edited.category)
        assertEquals(LibraryStatus.COMPLETED, edited.status)
        assertNull(edited.durationMinutes)
        assertEquals("Volver a verla", edited.notes)
        assertEquals(10L, edited.createdAt)
        assertEquals(20L, edited.updatedAt)

        repository.delete(edited.id)
        assertEquals(emptyList<Any>(), repository.observeAll().first())
    }
}
