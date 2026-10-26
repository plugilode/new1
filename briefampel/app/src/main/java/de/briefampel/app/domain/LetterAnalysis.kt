package de.briefampel.app.domain

import java.time.LocalDate

enum class Ampel(val label: String, val tag: String, val headline: String, val promise: String) {
    ROT(
        "Rot", "UNANGENEHM – ABER LÖSBAR", "Den musst du\nnicht lesen.",
        "Atme durch. Ich hab ihn gelesen – und die Antwort schreiben wir zusammen.",
    ),
    GELB(
        "Gelb", "KEIN ÄRGER, NUR ERLEDIGEN", "Da ist was\nzu tun.",
        "Nichts Schlimmes. Eine Kleinigkeit, die wir schnell vom Tisch haben.",
    ),
    GRUEN(
        "Grün", "ALLES GUT", "Den kannst du\nruhig lesen.",
        "Nichts Unangenehmes drin. Versprochen.",
    );

    /** Was TalkBack vorliest. */
    val spoken: String
        get() = when (this) {
            ROT -> "Ampel: Rot – unangenehm, du musst ihn nicht lesen"
            GELB -> "Ampel: Gelb – etwas zu tun, kein Ärger"
            GRUEN -> "Ampel: Grün – alles gut"
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
