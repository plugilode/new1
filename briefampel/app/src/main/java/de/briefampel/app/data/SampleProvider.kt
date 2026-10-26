package de.briefampel.app.data

import de.briefampel.app.domain.Ampel
import de.briefampel.app.domain.Deadline
import de.briefampel.app.domain.Fact
import de.briefampel.app.domain.LetterAnalysis
import de.briefampel.app.domain.LetterInput
import de.briefampel.app.domain.LlmProvider
import de.briefampel.app.domain.ReplyRequest
import de.briefampel.app.domain.Tone
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate

/**
 * Beispiel-Anbieter ohne KI (Meilenstein M1). Schaltet bei jeder Prüfung Rot → Gelb → Grün weiter,
 * damit man alle drei Ergebnisse sieht. Fristen sind relativ zu heute, damit die Beispiele stimmen.
 */
class SampleProvider(
    private val today: () -> LocalDate = { Deadline.today() },
    private val thinkMillis: Long = 2_900,
    private val wordMillis: Long = 45,
) : LlmProvider {
    private var next = 0

    override suspend fun analyze(input: LetterInput): LetterAnalysis {
        delay(thinkMillis)
        val sample = pick(input)
        return sample(today())
    }

    private fun pick(input: LetterInput): (LocalDate) -> LetterAnalysis {
        val text = input.pastedText.orEmpty().lowercase()
        return when {
            "inkasso" in text || "mahnung" in text -> ::red
            "finanzamt" in text -> ::yellow
            "termin" in text -> ::green
            else -> listOf(::red, ::yellow, ::green)[next++ % 3]
        }
    }

    override fun draftReply(request: ReplyRequest): Flow<String> = flow {
        delay(400)
        val text = replyText(request)
        // Wortweise mit Leerzeichen ausgeben, damit die Vorschau „schreibt“.
        Regex("""\S+\s*""").findAll(text).forEach {
            emit(it.value)
            delay(wordMillis)
        }
    }

    private fun replyText(request: ReplyRequest): String {
        val a = request.analysis
        val s = request.sender
        val head = listOf(
            s.name.ifBlank { "[Name]" },
            s.street.ifBlank { "[Straße]" },
            s.city.ifBlank { "[PLZ Ort]" },
            s.email.ifBlank { "[E-Mail]" },
        ).joinToString(" · ")
        val city = s.city.substringAfter(' ', "").ifBlank { "[Ort]" }
        val subject = buildString {
            a.reference?.let { append("Ihr Zeichen $it – ") }
            append("Ihr Schreiben")
            a.letterDate?.let { append(" vom $it") }
        }
        val opening = when (request.tone) {
            Tone.FREUNDLICH -> "vielen Dank für Ihr Schreiben."
            Tone.SACHLICH -> "ich beziehe mich auf Ihr Schreiben."
            Tone.BESTIMMT -> "zu Ihrem Schreiben nehme ich wie folgt Stellung."
            Tone.SCHARF -> "Ihr Schreiben weise ich in dieser Form zurück."
        }
        val points = request.keywords.lines().map { it.trim() }.filter { it.isNotEmpty() }
            .joinToString("\n\n") { "– ${it.replaceFirstChar(Char::uppercase)}." }
        return """
            |$head
            |
            |${a.sender}
            |
            |$city, ${Deadline.formatShort(today())}
            |
            |$subject
            |
            |Sehr geehrte Damen und Herren,
            |
            |$opening
            |
            |${points.ifBlank { "[Ihr Anliegen]" }}
            |
            |Bitte bestätigen Sie mir den Eingang dieses Schreibens.
            |
            |Mit freundlichen Grüßen
            |
            |${s.name.ifBlank { "[Name]" }}
        """.trimMargin()
    }

    companion object {
        fun red(today: LocalDate) = LetterAnalysis(
            ampel = Ampel.ROT,
            sender = "Inkasso Nordwest GmbH",
            letterDate = Deadline.formatShort(today.minusDays(11)),
            reference = "INK-55821/26",
            title = "Mahnung mit Klageandrohung",
            summary = "Ein Inkassobüro will 312,40 € für eine angeblich offene Handyrechnung. Wenn du nicht zahlst, drohen sie mit dem gerichtlichen Mahnverfahren.",
            facts = listOf(
                Fact("WOLLEN", "312,40 € – davon 189,90 € Hauptforderung, der Rest Inkassokosten und Zinsen."),
                Fact("TON", "Drohend. „Letzte Aufforderung“, Hinweis auf die SCHUFA."),
                Fact("WENN NICHTS", "Wahrscheinlich kommt ein Mahnbescheid. Die Inkassokosten wirken überhöht."),
                Fact("MEIN RAT", "Forderung bestreiten und Vertrag plus Rechnung als Nachweis verlangen.", emphasis = true),
            ),
            deadline = today.plusDays(3),
            deadlineNote = "Zahlungsfrist aus dem Schreiben, keine gesetzliche Frist. Trotzdem nicht verstreichen lassen.",
            replyNeeded = true,
            suggestions = listOf("Forderung bestreiten", "Vertrag und Rechnung anfordern", "Inkassokosten zurückweisen", "SCHUFA-Eintrag widersprechen"),
            fullText = "Inkasso Nordwest GmbH · Hafenstraße 3 · 28217 Bremen\n\nLetzte Aufforderung vor Einleitung des gerichtlichen Mahnverfahrens\n\nSehr geehrte Damen und Herren,\n\ntrotz mehrfacher Erinnerung unseres Mandanten, der Mobilfunk Süd GmbH, ist die nachstehende Forderung bislang nicht beglichen …\n\n(Beispieltext)",
        )

        fun yellow(today: LocalDate) = LetterAnalysis(
            ampel = Ampel.GELB,
            sender = "Finanzamt Köln-Altstadt",
            letterDate = Deadline.formatShort(today.minusDays(4)),
            reference = "St.-Nr. 214/5873/0912",
            title = "Rückfrage zur Steuererklärung 2025",
            summary = "Das Finanzamt möchte zwei Belege zu deinen Werbungskosten sehen. Kein Vorwurf, nur eine Nachfrage.",
            facts = listOf(
                Fact("WOLLEN", "Nachweis der Fahrtkosten und die Rechnung für den Laptop (1.249,00 €)."),
                Fact("TON", "Sachlich und freundlich."),
                Fact("WENN NICHTS", "Die Kosten werden womöglich nicht anerkannt."),
                Fact("MEIN RAT", "Belege fotografieren, kurz anschreiben, fertig.", emphasis = true),
            ),
            deadline = today.plusDays(18),
            deadlineNote = "Frist laut Brief.",
            replyNeeded = true,
            suggestions = listOf("Belege anbei", "Fristverlängerung erbitten", "Rückfrage zum Laptop"),
            fullText = "Finanzamt Köln-Altstadt\n\nEinkommensteuererklärung 2025 – Rückfrage\n\nSehr geehrte Frau Becker,\n\nzur Bearbeitung Ihrer Erklärung bitten wir um folgende Nachweise …\n\n(Beispieltext)",
        )

        fun green(today: LocalDate) = LetterAnalysis(
            ampel = Ampel.GRUEN,
            sender = "Stadt Köln · Bürgeramt Innenstadt",
            letterDate = Deadline.formatShort(today.minusDays(3)),
            reference = null,
            title = "Terminbestätigung Personalausweis",
            summary = "Dein Termin ist bestätigt. Bring das alte Ausweisdokument und ein biometrisches Foto mit.",
            facts = listOf(
                Fact("TERMIN", "${Deadline.formatDay(today.plusDays(9))} · 09:40 Uhr · Laurenzplatz 4"),
                Fact("ANTWORT", "Einfach hingehen.", emphasis = true),
            ),
            deadline = null,
            deadlineNote = null,
            replyNeeded = false,
            suggestions = listOf("Termin verschieben", "Termin absagen"),
            fullText = "Stadt Köln – Bürgeramt Innenstadt\n\nTerminbestätigung\n\nGuten Tag,\n\nhiermit bestätigen wir Ihren Termin zur Beantragung eines Personalausweises …\n\n(Beispieltext)",
        )
    }
}
