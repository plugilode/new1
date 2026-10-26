package de.briefampel.app.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import de.briefampel.app.domain.Ampel

/**
 * Warmes Briefpapier und Tinte statt Asphalt: Die App soll sich anfühlen wie jemand Vertrautes,
 * der dir den Brief abnimmt. Die Ampelfarben sind weich (Koralle, Honig, Salbei) – Bedeutung, kein Alarm.
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
    val primary: Color,
    val onPrimary: Color,
    val red: Color,
    val amber: Color,
    val green: Color,
    /** Warmes Licht, das immer leicht im Raum liegt – wie eine Lampe am Abend. */
    val ambient: Color,
    val housingTop: Color,
    val housingBottom: Color,
    val housingEdge: Color,
    val isDark: Boolean,
) {
    fun lamp(ampel: Ampel): Color = when (ampel) {
        Ampel.ROT -> red
        Ampel.GELB -> amber
        Ampel.GRUEN -> green
    }
}

object Lamp {
    val Red = Color(0xFFEC6A4C)
    val Amber = Color(0xFFF0AE45)
    val Green = Color(0xFF67B386)

    fun glow(ampel: Ampel): Color = when (ampel) {
        Ampel.ROT -> Red
        Ampel.GELB -> Amber
        Ampel.GRUEN -> Green
    }
}

val Paper = Color(0xFFFFFBF4)
val Ink = Color(0xFF2B221D)

val LightColors = BriefColors(
    background = Color(0xFFF5EEE3),
    surface = Color(0xFFFFFBF4),
    surfaceLine = Color(0xFFE8DDCD),
    line = Color(0xFFE0D3C1),
    text = Ink,
    textSoft = Color(0xFF4A3E36),
    textMuted = Color(0xFF6B5D52),
    textFaint = Color(0xFF8A7B6E),
    primary = Color(0xFF2E241F),
    onPrimary = Color(0xFFFBF4E9),
    red = Color(0xFFB8472D),
    amber = Color(0xFF94600F),
    green = Color(0xFF2E7650),
    ambient = Color(0xFFF2B866),
    housingTop = Color(0xFFFFFCF7),
    housingBottom = Color(0xFFEDE2D2),
    housingEdge = Color(0xFFE2D5C3),
    isDark = false,
)

val DarkColors = BriefColors(
    background = Color(0xFF1C1714),
    surface = Color(0xFF26201C),
    surfaceLine = Color(0xFF372E28),
    line = Color(0xFF41372F),
    text = Color(0xFFF5ECDF),
    textSoft = Color(0xFFE4D8C8),
    textMuted = Color(0xFFBBAC9C),
    textFaint = Color(0xFF948576),
    primary = Color(0xFFF5ECDF),
    onPrimary = Color(0xFF1C1714),
    red = Lamp.Red,
    amber = Lamp.Amber,
    green = Lamp.Green,
    ambient = Color(0xFFE59A4A),
    housingTop = Color(0xFF342B26),
    housingBottom = Color(0xFF221C18),
    housingEdge = Color(0xFF41362F),
    isDark = true,
)

val LocalBriefColors = staticCompositionLocalOf { LightColors }
