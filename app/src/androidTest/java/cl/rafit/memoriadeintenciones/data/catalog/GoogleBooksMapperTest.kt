package cl.rafit.memoriadeintenciones.data.catalog

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GoogleBooksMapperTest {
    @Test fun mapsPublicBookFieldsAndUpgradesThumbnailToHttps() {
        val item = GoogleBooksMapper.toCatalogItem(GoogleBookVolume(id = "id", title = "  Fuego y sangre ", authors = listOf("George R. R. Martin"), publisher = "Plaza", publishedDate = "2018-11-20", identifiers = listOf(BookIdentifier("ISBN_13", "9788401021020")), thumbnailUrl = "http://cover"))!!
        assertEquals("Fuego y sangre", item.title); assertEquals(2018, item.publishedYear); assertEquals("https://cover", item.thumbnailUrl)
    }
    @Test fun rejectsVolumeWithoutTitle() { assertNull(GoogleBooksMapper.toCatalogItem(GoogleBookVolume("id", null))) }
}
