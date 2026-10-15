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
}
