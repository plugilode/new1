package de.briefampel.app.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class KeywordsTest {
    @Test fun chipAddsAndRemovesLine() {
        val added = Keywords.toggle("nie Kundin gewesen", "Forderung bestreiten")
        assertEquals("nie Kundin gewesen\nForderung bestreiten", added)
        assertTrue(Keywords.contains(added, "forderung bestreiten"))
        val removed = Keywords.toggle(added, "Forderung bestreiten")
        assertEquals("nie Kundin gewesen", removed)
        assertFalse(Keywords.contains(removed, "Forderung bestreiten"))
    }

    @Test fun typedLineCountsAsSelectedChip() {
        assertTrue(Keywords.contains("  Vertrag anfordern  \n", "Vertrag anfordern"))
    }

    @Test fun emptyLinesAreIgnored() {
        assertEquals(listOf("a", "b"), Keywords.lines("\n a \n\n b\n"))
    }
}
