package cl.rafit.memoriadeintenciones.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class OcrCandidateExtractorTest {
    @Test
    fun proposesShortTrailingTitleLinesForReview() {
        assertEquals(
            "FUEGO SNGRE",
            OcrCandidateExtractor.titleFrom("GEORGE R.R.\nMARTIN\nFUEGO\nSNGRE")
        )
    }
}
