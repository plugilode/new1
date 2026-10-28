package de.briefampel.app.domain

/** Fehlerfälle mit Texten, die ein Mensch versteht (Spec §6). */
enum class AnalysisError(val message: String) {
    OFFLINE("Keine Verbindung. Prüf dein Internet und versuch es noch mal."),
    TOO_LARGE("Der Brief ist zu groß. Höchstens 12 PDF-Seiten oder 10 Fotos."),
    UNREADABLE_INPUT("Ich kann darin keinen Text erkennen. Ein schärferes Foto hilft."),
    UNREADABLE_ANSWER("Die Prüfung ist schiefgegangen. Versuch es bitte noch einmal."),
    DAILY_LIMIT("Für heute sind die drei freien Prüfungen aufgebraucht. Morgen geht es weiter."),
    UNKNOWN("Etwas hat nicht geklappt. Versuch es bitte noch einmal."),
}

class AnalysisException(val error: AnalysisError) : Exception(error.message)
