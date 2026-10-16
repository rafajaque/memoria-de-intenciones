package cl.rafit.memoriadeintenciones.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class IsbnTest {
    @Test fun normalizesAndValidatesIsbn10And13() {
        assertEquals("9780306406157", Isbn.normalize("978-0-306-40615-7"))
        assertTrue(Isbn.isValid("0306406152"))
        assertTrue(Isbn.isValid("9780306406157"))
    }
    @Test fun rejectsInvalidChecksum() { assertFalse(Isbn.isValid("9780306406158")) }
}
