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

    @Test
    fun doesNotUseOcrHeuristicWhenIsbnWasDetectedWithoutCatalogProposal() {
        assertEquals(
            "",
            OcrCandidateExtractor.titleFrom(
                "ISBN: 978-84-663-7497-2\nRandom House Grupo Editorial",
                isbnDetected = true
            )
        )
    }
}
