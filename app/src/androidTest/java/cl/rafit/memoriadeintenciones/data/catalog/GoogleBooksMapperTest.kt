package cl.rafit.memoriadeintenciones.data.catalog

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GoogleBooksMapperTest {
    @Test fun mapsPublicBookFieldsAndUpgradesThumbnailToHttps() {
        val item = GoogleBooksMapper.toCatalogItem(GoogleBookVolume("id", "  Fuego y sangre ", listOf("George R. R. Martin"), "Plaza", "2018-11-20", listOf(BookIdentifier("ISBN_13", "9788401021020"),), "http://cover"))!!
        assertEquals("Fuego y sangre", item.title); assertEquals(2018, item.publishedYear); assertEquals("https://cover", item.thumbnailUrl)
    }
    @Test fun rejectsVolumeWithoutTitle() { assertNull(GoogleBooksMapper.toCatalogItem(GoogleBookVolume("id", null))) }
}
