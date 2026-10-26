package de.briefampel.app.domain

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * Liest die JSON-Antwort des Modells tolerant: Markdown-Zäune, Text drumherum,
 * fehlende Felder und deutsche Datumsangaben sind erlaubt.
 */
object AnalysisParser {
    private val json = Json { isLenient = true; ignoreUnknownKeys = true }
    private val germanDate = DateTimeFormatter.ofPattern("d.M.yyyy")

    fun parse(raw: String, fullText: String = ""): LetterAnalysis {
        val obj = extractObject(raw) ?: throw AnalysisException(AnalysisError.UNREADABLE_ANSWER)
        val title = obj.string("titel")
        val summary = obj.string("kurzfassung")
        if (title.isNullOrBlank() || summary.isNullOrBlank()) throw AnalysisException(AnalysisError.UNREADABLE_ANSWER)

        val facts = buildList {
            obj.string("forderung")?.let { add(Fact("WOLLEN", it)) }
            obj.string("ton")?.let { add(Fact("TON", it)) }
            obj.string("wenn_nichts")?.let { add(Fact("WENN NICHTS", it)) }
            obj.string("rat")?.let { add(Fact("MEIN RAT", it, emphasis = true)) }
        }
        return LetterAnalysis(
            ampel = Ampel.parse(obj.string("ampel")),
            sender = obj.string("absender") ?: "Unbekannter Absender",
            letterDate = obj.string("datum"),
            reference = obj.string("aktenzeichen"),
            title = title,
            summary = summary,
            facts = facts,
            deadline = obj.string("frist")?.let(::parseDate),
            deadlineNote = obj.string("frist_hinweis"),
            replyNeeded = (obj["antwort_noetig"] as? JsonPrimitive)?.contentOrNull?.toBooleanStrictOrNull() ?: true,
            suggestions = (obj["vorschlaege"] as? JsonArray).orEmpty()
                .mapNotNull { (it as? JsonPrimitive)?.contentOrNull?.trim() }
                .filter { it.isNotEmpty() }
                .take(6),
            fullText = fullText,
        )
    }

    internal fun parseDate(raw: String): LocalDate? {
        val s = raw.trim()
        return try {
            LocalDate.parse(s)
        } catch (_: DateTimeParseException) {
            try {
                LocalDate.parse(s, germanDate)
            } catch (_: DateTimeParseException) {
                null
            }
        }
    }

    private fun extractObject(raw: String): JsonObject? {
        val start = raw.indexOf('{')
        val end = raw.lastIndexOf('}')
        if (start < 0 || end <= start) return null
        return try {
            json.parseToJsonElement(raw.substring(start, end + 1)) as? JsonObject
        } catch (_: Exception) {
            null
        }
    }

    private fun JsonObject.string(key: String): String? =
        (this[key] as? JsonPrimitive)?.contentOrNull?.trim()?.takeIf { it.isNotEmpty() && it != "null" }

}
