package de.briefampel.app.domain

/** Verknüpft Vorschlags-Chips mit dem Stichwortfeld: eine Zeile pro Stichwort. */
object Keywords {
    fun lines(text: String): List<String> = text.lines().map { it.trim() }.filter { it.isNotEmpty() }

    fun contains(text: String, chip: String): Boolean = lines(text).any { it.equals(chip, ignoreCase = true) }

    fun toggle(text: String, chip: String): String {
        val current = lines(text)
        val next = if (current.any { it.equals(chip, ignoreCase = true) }) {
            current.filterNot { it.equals(chip, ignoreCase = true) }
        } else {
            current + chip
        }
        return next.joinToString("\n")
    }
}
