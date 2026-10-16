package cl.rafit.memoriadeintenciones.domain

// MVP: validación local previa a consultar cualquier catálogo.
object Isbn {
    fun normalize(value: String): String = value.filter { it.isDigit() || it == 'X' || it == 'x' }.uppercase()

    fun isValid(value: String): Boolean {
        val isbn = normalize(value)
        return when (isbn.length) {
            10 -> isbn.take(9).all(Char::isDigit) && (isbn.last().isDigit() || isbn.last() == 'X') &&
                isbn.mapIndexed { index, char -> (if (char == 'X') 10 else char.digitToInt()) * (10 - index) }.sum() % 11 == 0
            13 -> isbn.all(Char::isDigit) && isbn.mapIndexed { index, char -> char.digitToInt() * if (index % 2 == 0) 1 else 3 }.sum() % 10 == 0
            else -> false
        }
    }
}
