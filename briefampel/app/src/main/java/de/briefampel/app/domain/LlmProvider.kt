package de.briefampel.app.domain

import kotlinx.coroutines.flow.Flow

/** Was eingereicht wurde: Seiten (Bilder/PDF) und/oder eingefügter Text. */
data class LetterInput(
    val pages: List<Page>,
    val pastedText: String?,
) {
    data class Page(val uri: String, val mimeType: String)

    val isEmpty: Boolean get() = pages.isEmpty() && pastedText.isNullOrBlank()
    val pageCount: Int get() = if (pages.isNotEmpty()) pages.size else if (pastedText.isNullOrBlank()) 0 else 1

    companion object {
        const val MAX_TEXT_CHARS = 60_000
        const val MAX_IMAGES = 10
        const val MAX_PDF_PAGES = 12
    }
}

enum class Tone(val label: String) { FREUNDLICH("Freundlich"), SACHLICH("Sachlich"), BESTIMMT("Bestimmt"), SCHARF("Scharf") }

data class Sender(val name: String = "", val street: String = "", val city: String = "", val email: String = "") {
    val isEmpty: Boolean get() = name.isBlank() && street.isBlank() && city.isBlank()
    fun oneLine(): String = listOf(name, street, city).filter { it.isNotBlank() }.joinToString(" · ")
}

data class ReplyRequest(
    val analysis: LetterAnalysis,
    val keywords: String,
    val tone: Tone,
    val sender: Sender,
)

/**
 * Austauschbarer KI-Anbieter. v1: Gemini über Firebase AI Logic (M2); bis dahin [SampleProvider].
 */
interface LlmProvider {
    suspend fun analyze(input: LetterInput): LetterAnalysis

    /** Liefert den Antwortbrief stückweise; Abbrechen der Coroutine = Stopp. */
    fun draftReply(request: ReplyRequest): Flow<String>
}
