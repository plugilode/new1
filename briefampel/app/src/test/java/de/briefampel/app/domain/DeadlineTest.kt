package de.briefampel.app.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

class DeadlineTest {
    private val today = LocalDate.of(2026, 10, 25)

    @Test fun hotBelowSevenDays() {
        assertTrue(Deadline.isHot(today.plusDays(3), today))
        assertTrue(Deadline.isHot(today.plusDays(6), today))
        assertFalse(Deadline.isHot(today.plusDays(7), today))
        assertTrue(Deadline.isHot(today.minusDays(1), today))
    }

    @Test fun todayUsesBerlinTime() {
        // 23:30 UTC am 25.10. ist in Berlin schon der 26.10.
        val clock = Clock.fixed(Instant.parse("2026-10-25T23:30:00Z"), ZoneOffset.UTC)
        assertEquals(LocalDate.of(2026, 10, 26), Deadline.today(clock))
    }

    @Test fun formatsGermanWeekday() {
        assertEquals("Mi 28.10.2026", Deadline.formatDay(LocalDate.of(2026, 10, 28)))
    }

    @Test fun barEndsOnDeadline() {
        val cells = Deadline.barCells(today.plusDays(3), today)
        assertEquals(Deadline.BAR_CELLS, cells.size)
        assertEquals(10, cells.count { it == Deadline.Cell.PAST })
        assertEquals(Deadline.Cell.TODAY, cells[10])
        assertEquals(3, cells.count { it == Deadline.Cell.REMAINING })
    }

    @Test fun barForFarAndPastDeadlines() {
        assertTrue(Deadline.barCells(today.plusDays(30), today).all { it == Deadline.Cell.REMAINING })
        val past = Deadline.barCells(today.minusDays(2), today)
        assertEquals(Deadline.Cell.TODAY, past.last())
    }

    @Test fun daysLabel() {
        assertEquals("heute", Deadline.daysLabel(0))
        assertEquals("1 Tag", Deadline.daysLabel(1))
        assertEquals("18 Tage", Deadline.daysLabel(18))
        assertEquals("abgelaufen", Deadline.daysLabel(-1))
    }
}
