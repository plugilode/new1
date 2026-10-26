package de.briefampel.app.domain

import java.time.Clock
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

object Deadline {
    val zone: ZoneId = ZoneId.of("Europe/Berlin")
    const val HOT_DAYS = 7
    /** Die Frist-Leiste zeigt immer zwei Wochen. */
    const val BAR_CELLS = 14

    private val weekday = DateTimeFormatter.ofPattern("EE", Locale.GERMAN)
    private val date = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    fun today(clock: Clock = Clock.system(zone)): LocalDate = LocalDate.now(clock.withZone(zone))

    fun daysLeft(deadline: LocalDate, today: LocalDate): Long = ChronoUnit.DAYS.between(today, deadline)

    fun isHot(deadline: LocalDate, today: LocalDate): Boolean = daysLeft(deadline, today) < HOT_DAYS

    /** „Mi 28.10.2026“ */
    fun formatDay(day: LocalDate): String = "${weekday.format(day).removeSuffix(".")} ${date.format(day)}"

    fun formatShort(day: LocalDate): String = date.format(day)

    fun daysLabel(days: Long): String = when {
        days < 0 -> "abgelaufen"
        days == 0L -> "heute"
        days == 1L -> "1 Tag"
        else -> "$days Tage"
    }

    /**
     * Zustand jeder Zelle der Frist-Leiste: vergangene Tage, heute, verbleibende Tage.
     * Die Leiste endet am Fristtag; liegt die Frist weiter als 14 Tage weg, sind alle Zellen „verbleibend“.
     */
    fun barCells(deadline: LocalDate, today: LocalDate): List<Cell> {
        val left = daysLeft(deadline, today).coerceAtLeast(0).toInt()
        if (left >= BAR_CELLS) return List(BAR_CELLS) { Cell.REMAINING }
        val past = BAR_CELLS - left - 1
        return List(past) { Cell.PAST } + Cell.TODAY + List(left) { Cell.REMAINING }
    }

    enum class Cell { PAST, TODAY, REMAINING }
}
