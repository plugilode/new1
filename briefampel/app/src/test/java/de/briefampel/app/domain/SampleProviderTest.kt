package de.briefampel.app.domain

import de.briefampel.app.data.SampleProvider
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class SampleProviderTest {
    private val today = LocalDate.of(2026, 10, 25)
    private val provider = SampleProvider(today = { today }, thinkMillis = 0, wordMillis = 0)
    private val pages = LetterInput(listOf(LetterInput.Page("content://x", "image/jpeg")), null)

    @Test fun cyclesThroughAllThreeColors() = runTest {
        val colors = List(3) { provider.analyze(pages).ampel }
        assertEquals(listOf(Ampel.ROT, Ampel.GELB, Ampel.GRUEN), colors)
    }

    @Test fun pastedTextPicksMatchingSample() = runTest {
        assertEquals(Ampel.GELB, provider.analyze(LetterInput(emptyList(), "Ihr Finanzamt")).ampel)
    }

    @Test fun replyUsesKeywordsAndMarksGaps() = runTest {
        val analysis = SampleProvider.red(today)
        val text = provider.draftReply(ReplyRequest(analysis, "Forderung bestreiten", Tone.SACHLICH, Sender(name = "Jana Becker"))).toList().joinToString("")
        assertTrue("Forderung bestreiten." in text)
        assertTrue("[E-Mail]" in text)
        assertTrue("INK-55821/26" in text)
        assertFalse("null" in text)
    }
}
