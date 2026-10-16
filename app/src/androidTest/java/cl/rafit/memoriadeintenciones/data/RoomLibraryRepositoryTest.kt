package cl.rafit.memoriadeintenciones.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import cl.rafit.memoriadeintenciones.data.local.MemoriaDatabase
import cl.rafit.memoriadeintenciones.domain.LibraryCategory
import cl.rafit.memoriadeintenciones.domain.BookMetadata
import cl.rafit.memoriadeintenciones.domain.LibraryStatus
import cl.rafit.memoriadeintenciones.domain.ItemExperience
import cl.rafit.memoriadeintenciones.domain.RecommendationPriority
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
        repository = RoomLibraryRepository(
            database.libraryItemDao(),
            database.experienceDao(),
            database.recommendationFeedbackDao()
        )
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
            bookMetadata = null,
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
            bookMetadata = null,
            now = 10L
        )
        val original = repository.observeAll().first().single()

        repository.edit(
            item = original,
            title = "  La llegada (vista)  ",
            status = LibraryStatus.COMPLETED,
            durationMinutes = null,
            notes = "  Volver a verla  ",
            bookMetadata = null,
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

    @Test
    fun bookMetadataPersistsOnlyForBooks() = runBlocking {
        val metadata = BookMetadata(
            isbn = "9780306406157",
            authors = listOf("Autor Uno", "Autora Dos"),
            publisher = "Editorial de prueba",
            publishedDate = "2026",
            language = "es",
            pageCount = 320,
            coverUrl = "https://example.com/cover.jpg",
            edition = "2.ª edición"
        )

        repository.add(
            title = "Libro de prueba",
            category = LibraryCategory.BOOK,
            status = LibraryStatus.TO_CONSUME,
            durationMinutes = null,
            notes = null,
            bookMetadata = metadata,
            now = 50L
        )

        assertEquals(metadata, repository.observeByCategory(LibraryCategory.BOOK).first().single().bookMetadata)
    }

    @Test
    fun experienceCanBeCreatedEditedAndDeletedSeparately() = runBlocking {
        repository.add(
            title = "Libro vivido",
            category = LibraryCategory.BOOK,
            status = LibraryStatus.COMPLETED,
            durationMinutes = null,
            notes = null,
            bookMetadata = null,
            now = 60L
        )
        val item = repository.observeAll().first().single()

        repository.saveExperience(item.id, ItemExperience(platform = "Kindle", rating = 4, comment = "  Me gustó  ", startedAt = 100L, finishedAt = 200L))
        val saved = repository.observeAll().first().single().experience
        assertEquals("Kindle", saved?.platform)
        assertEquals(4, saved?.rating)
        assertEquals("Me gustó", saved?.comment)
        assertEquals(100L, saved?.startedAt)
        assertEquals(200L, saved?.finishedAt)

        repository.saveExperience(item.id, ItemExperience(platform = "Papel", rating = 5, comment = "Excelente", createdAt = saved!!.createdAt))
        assertEquals("Papel", repository.observeAll().first().single().experience?.platform)

        repository.deleteExperience(item.id)
        assertNull(repository.observeAll().first().single().experience)
    }

    @Test
    fun recommendationPriorityAndFeedbackPersistLocally() = runBlocking {
        repository.add(
            title = "Pendiente para recomendar",
            category = LibraryCategory.GAME,
            status = LibraryStatus.TO_CONSUME,
            durationMinutes = 90,
            notes = null,
            bookMetadata = null,
            now = 70L
        )
        val item = repository.observeAll().first().single()

        assertEquals(RecommendationPriority.NORMAL, item.recommendationPriority)
        repository.setRecommendationPriority(item.id, RecommendationPriority.HIGH)
        repository.recordRecommendationAccepted(item.id)
        repository.recordRecommendationPostponed(item.id)
        repository.recordRecommendationDismissed(item.id)

        val stored = repository.observeAll().first().single()
        assertEquals(RecommendationPriority.HIGH, stored.recommendationPriority)
        assertEquals(1, stored.recommendationFeedback.acceptedCount)
        assertEquals(1, stored.recommendationFeedback.postponedCount)
        assertEquals(true, stored.recommendationFeedback.dismissed)

        repository.delete(item.id)
        assertEquals(emptyList<Any>(), repository.observeAll().first())
        assertEquals(emptyList<Any>(), database.recommendationFeedbackDao().observeAll().first())
    }
}
