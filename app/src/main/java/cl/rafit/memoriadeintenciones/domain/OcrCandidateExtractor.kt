package cl.rafit.memoriadeintenciones.domain

// MVP: propuesta local y explicable; la persona siempre la revisa antes de guardar.
object OcrCandidateExtractor {
    fun titleFrom(text: String, isbnDetected: Boolean = false): String {
        if (isbnDetected) return ""
        val lines = text.lineSequence()
            .map { it.trim().replace(Regex("\\s+"), " ") }
            .filter { line -> line.count(Char::isLetter) >= 3 }
            .toList()
        val shortTail = lines.takeLast(3).filter { it.length <= 24 }
        return (if (shortTail.size >= 2) shortTail.takeLast(2) else lines.take(1))
            .joinToString(" ")
            .take(80)
    }
}
