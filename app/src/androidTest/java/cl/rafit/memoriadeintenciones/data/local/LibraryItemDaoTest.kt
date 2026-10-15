package cl.rafit.memoriadeintenciones.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import cl.rafit.memoriadeintenciones.domain.LibraryCategory
import cl.rafit.memoriadeintenciones.domain.LibraryStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LibraryItemDaoTest {
    private lateinit var database: MemoriaDatabase
    private lateinit var dao: LibraryItemDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, MemoriaDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.libraryItemDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndFilterByCategory() = runBlocking {
        dao.insert(item(category = LibraryCategory.MOVIE))
        dao.insert(item(category = LibraryCategory.BOOK))

        val movies = dao.observeByCategory(LibraryCategory.MOVIE.name).first()

        assertEquals(1, movies.size)
        assertEquals("Elemento de prueba", movies.single().title)
    }

    @Test
    fun updateAndDeleteById() = runBlocking {
        val id = dao.insert(item(category = LibraryCategory.GAME))
        val inserted = dao.observeAll().first().single()

        dao.update(inserted.copy(status = LibraryStatus.COMPLETED.name, updatedAt = inserted.updatedAt + 1))
        assertEquals(LibraryStatus.COMPLETED.name, dao.observeAll().first().single().status)

        dao.deleteById(id)
        assertEquals(0, dao.observeAll().first().size)
    }

    private fun item(category: LibraryCategory) = LibraryItemEntity(
        title = "Elemento de prueba",
        category = category.name,
        status = LibraryStatus.TO_CONSUME.name,
        notes = null,
        createdAt = 1L,
        updatedAt = 1L
    )
}
