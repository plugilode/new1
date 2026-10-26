package de.briefampel.app.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.time.LocalDate

class AnalysisParserTest {
    private val full = """
        {"ampel":"rot","absender":"Inkasso Nordwest GmbH","datum":"2026-10-14","aktenzeichen":"INK-55821/26",
         "titel":"Mahnung mit Klageandrohung","kurzfassung":"Ein Inkassobüro will Geld.",
         "forderung":"312,40 €","ton":"Drohend","wenn_nichts":"Mahnbescheid","rat":"Bestreiten",
         "frist":"2026-10-28","frist_hinweis":"laut Brief","antwort_noetig":true,
         "vorschlaege":["Forderung bestreiten"," ","Vertrag anfordern"]}
    """.trimIndent()

    @Test fun parsesCompleteAnswer() {
        val a = AnalysisParser.parse(full)
        assertEquals(Ampel.ROT, a.ampel)
        assertEquals("Inkasso Nordwest GmbH", a.sender)
        assertEquals(LocalDate.of(2026, 10, 28), a.deadline)
        assertEquals(listOf("WOLLEN", "TON", "WENN NICHTS", "MEIN RAT"), a.facts.map { it.label })
        assertTrue(a.facts.last().emphasis)
        assertEquals(listOf("Forderung bestreiten", "Vertrag anfordern"), a.suggestions)
    }

    @Test fun toleratesMarkdownFenceAndChatter() {
        val a = AnalysisParser.parse("Klar, hier:\n```json\n$full\n```\nViel Erfolg!")
        assertEquals("Mahnung mit Klageandrohung", a.title)
    }

    @Test fun unknownAmpelFallsBackToYellow() {
        assertEquals(Ampel.GELB, AnalysisParser.parse("""{"ampel":"lila","titel":"T","kurzfassung":"K"}""").ampel)
        assertEquals(Ampel.GELB, AnalysisParser.parse("""{"titel":"T","kurzfassung":"K"}""").ampel)
        assertEquals(Ampel.GRUEN, AnalysisParser.parse("""{"ampel":" Grün ","titel":"T","kurzfassung":"K"}""").ampel)
    }

    @Test fun missingOptionalFieldsAreNull() {
        val a = AnalysisParser.parse("""{"titel":"T","kurzfassung":"K","frist":null,"aktenzeichen":""}""")
        assertNull(a.deadline)
        assertNull(a.reference)
        assertTrue(a.facts.isEmpty())
        assertTrue(a.replyNeeded)
    }

    @Test fun parsesGermanDate() {
        assertEquals(LocalDate.of(2026, 11, 12), AnalysisParser.parseDate("12.11.2026"))
        assertNull(AnalysisParser.parseDate("bald"))
    }

    @Test fun rejectsUnusableAnswers() {
        listOf("", "kein json", """{"ampel":"rot"}""", "{kaputt").forEach {
            try {
                AnalysisParser.parse(it)
                fail("expected failure for '$it'")
            } catch (e: AnalysisException) {
                assertEquals(AnalysisError.UNREADABLE_ANSWER, e.error)
            }
        }
    }
}
