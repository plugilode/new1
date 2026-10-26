package de.briefampel.app.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import de.briefampel.app.domain.Ampel

/**
 * „Nur die Ampel hat Farbe.“ Alles andere ist Asphalt und Kreide.
 * Lampenfarben sind Bedeutung, nie Deko.
 */
@Immutable
data class BriefColors(
    val background: Color,
    val surface: Color,
    val surfaceLine: Color,
    val line: Color,
    val text: Color,
    val textSoft: Color,
    val textMuted: Color,
    val textFaint: Color,
    /** Hauptknöpfe: kreideweiß auf dunkel, asphalt auf hell. */
    val primary: Color,
    val onPrimary: Color,
    val red: Color,
    val amber: Color,
    val green: Color,
    val isDark: Boolean,
) {
    fun lamp(ampel: Ampel): Color = when (ampel) {
        Ampel.ROT -> red
        Ampel.GELB -> amber
        Ampel.GRUEN -> green
    }
}

object Lamp {
    val Red = Color(0xFFFF5A47)
    val Amber = Color(0xFFF5B544)
    val Green = Color(0xFF3DD68C)

    fun glow(ampel: Ampel): Color = when (ampel) {
        Ampel.ROT -> Red
        Ampel.GELB -> Amber
        Ampel.GRUEN -> Green
    }
}

val Chalk = Color(0xFFF3F0E8)
val Asphalt = Color(0xFF121417)
val Paper = Color(0xFFF3F0E8)
val Ink = Color(0xFF1E2126)

val DarkColors = BriefColors(
    background = Asphalt,
    surface = Color(0xFF1A1D21),
    surfaceLine = Color(0xFF2A2E34),
    line = Color(0xFF2E3238),
    text = Chalk,
    textSoft = Color(0xFFD6D8DB),
    textMuted = Color(0xFFA3A8AF),
    textFaint = Color(0xFF8E949B),
    primary = Chalk,
    onPrimary = Asphalt,
    red = Lamp.Red,
    amber = Lamp.Amber,
    green = Lamp.Green,
    isDark = true,
)

val LightColors = BriefColors(
    background = Color(0xFFECEEF1),
    surface = Color(0xFFFFFFFF),
    surfaceLine = Color(0xFFDADDE2),
    line = Color(0xFFD3D6DB),
    text = Color(0xFF15181C),
    textSoft = Color(0xFF2A2E34),
    textMuted = Color(0xFF4E545C),
    textFaint = Color(0xFF5E646C),
    primary = Color(0xFF15181C),
    onPrimary = Chalk,
    // Dunklere Textvarianten für Kontrast auf hellem Grund; die Lampen selbst bleiben leuchtend.
    red = Color(0xFFB42318),
    amber = Color(0xFF8A5200),
    green = Color(0xFF12784A),
    isDark = false,
)

val LocalBriefColors = staticCompositionLocalOf { DarkColors }
