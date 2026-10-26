package de.briefampel.app.domain

import java.time.LocalDate

enum class Ampel(val label: String, val tag: String, val headline: String, val promise: String) {
    ROT("Rot", "ROT · UNANGENEHM", "Lieber\nnicht\nlesen.", "Ich hab ihn gelesen. Hier steht alles, was du wissen musst."),
    GELB("Gelb", "GELB · HANDELN, KEIN ÄRGER", "Kümmern.\nAber ganz\nsachlich.", "Du musst etwas tun – Ärger gibt es keinen."),
    GRUEN("Grün", "GRÜN · HARMLOS", "Ganz\nentspannt\nlesen.", "Nichts Unangenehmes drin. Versprochen.");

    /** Was TalkBack vorliest. */
    val spoken: String
        get() = when (this) {
            ROT -> "Ampel: Rot – lieber nicht lesen"
            GELB -> "Ampel: Gelb – kümmern, aber sachlich"
            GRUEN -> "Ampel: Grün – harmlos"
        }

    companion object {
        /** Unbekannte oder fehlende Werte landen bewusst bei Gelb: lieber hinschauen als falsch beruhigen. */
        fun parse(raw: String?): Ampel = when (raw?.trim()?.lowercase()) {
            "rot", "red" -> ROT
            "grün", "gruen", "green" -> GRUEN
            else -> GELB
        }
    }
}

data class Fact(val label: String, val text: String, val emphasis: Boolean = false)

data class LetterAnalysis(
    val ampel: Ampel,
    val sender: String,
    val letterDate: String?,
    val reference: String?,
    val title: String,
    val summary: String,
    val facts: List<Fact>,
    val deadline: LocalDate?,
    val deadlineNote: String?,
    val replyNeeded: Boolean,
    val suggestions: List<String>,
    val fullText: String,
)
