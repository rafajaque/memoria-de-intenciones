package cl.rafit.memoriadeintenciones

import org.junit.Assert.assertEquals
import org.junit.Test

class IsbnScanGateTest {
    @Test fun emitsOnlyTheFirstValidIsbn() {
        val values = mutableListOf<String>()
        val gate = IsbnScanGate()

        gate.emitIfValid("9780306406157", values::add)
        gate.emitIfValid("9788466374972", values::add)

        assertEquals(listOf("9780306406157"), values)
    }
}
