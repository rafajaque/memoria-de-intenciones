package cl.rafit.memoriadeintenciones

import cl.rafit.memoriadeintenciones.domain.Isbn
import java.util.concurrent.atomic.AtomicBoolean

internal class IsbnScanGate {
    private val emitted = AtomicBoolean(false)

    fun emitIfValid(rawValue: String?, onFound: (String) -> Unit) {
        val isbn = rawValue?.takeIf(Isbn::isValid) ?: return
        if (emitted.compareAndSet(false, true)) onFound(isbn)
    }
}
