package de.briefampel.app.ui.theme

import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import de.briefampel.app.R

@OptIn(ExperimentalTextApi::class)
private fun variable(res: Int, weight: Int, opsz: Float? = null) = Font(
    res,
    weight = FontWeight(weight),
    variationSettings = FontVariation.Settings(
        *listOfNotNull(FontVariation.weight(weight), opsz?.let { FontVariation.Setting("opsz", it) }).toTypedArray(),
    ),
)

/** Bricolage Grotesque – Überschriften mit Charakter. */
val Display = FontFamily(
    variable(R.font.bricolage_grotesque, 600, 48f),
    variable(R.font.bricolage_grotesque, 700, 48f),
    variable(R.font.bricolage_grotesque, 800, 96f),
)

/** Source Sans 3 – ruhiger Fließtext. */
val Body = FontFamily(
    variable(R.font.source_sans_3, 400),
    variable(R.font.source_sans_3, 600),
    variable(R.font.source_sans_3, 700),
)

/** IBM Plex Mono – Daten, Fristen, Aktenzeichen. */
val Mono = FontFamily(
    Font(R.font.plex_mono_regular, FontWeight.Normal),
    Font(R.font.plex_mono_medium, FontWeight.Medium),
    Font(R.font.plex_mono_semibold, FontWeight.SemiBold),
)

object BriefType {
    val hero = TextStyle(fontFamily = Display, fontWeight = FontWeight.ExtraBold, fontSize = 40.sp, lineHeight = 40.sp, letterSpacing = (-0.035).em)
    val heroLarge = hero.copy(fontSize = 44.sp, lineHeight = 43.sp, letterSpacing = (-0.04).em)
    val title = TextStyle(fontFamily = Display, fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 31.sp, letterSpacing = (-0.02).em)
    val titleSmall = title.copy(fontSize = 25.sp, lineHeight = 28.sp)
    val heading = TextStyle(fontFamily = Display, fontWeight = FontWeight.ExtraBold, fontSize = 36.sp, lineHeight = 36.sp, letterSpacing = (-0.035).em)
    val brand = TextStyle(fontFamily = Display, fontWeight = FontWeight.ExtraBold, fontSize = 21.sp, letterSpacing = (-0.02).em)
    val lead = TextStyle(fontFamily = Body, fontSize = 18.sp, lineHeight = 26.sp)
    val body = TextStyle(fontFamily = Body, fontSize = 16.5.sp, lineHeight = 24.sp)
    val bodySmall = TextStyle(fontFamily = Body, fontSize = 15.sp, lineHeight = 21.sp)
    val button = TextStyle(fontFamily = Body, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    val buttonSmall = TextStyle(fontFamily = Body, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
    val fine = TextStyle(fontFamily = Body, fontSize = 12.sp, lineHeight = 16.sp)
    val label = TextStyle(fontFamily = Mono, fontSize = 11.5.sp, letterSpacing = 0.1.em)
    val data = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Medium, fontSize = 15.sp)
    val countdown = TextStyle(fontFamily = Mono, fontWeight = FontWeight.SemiBold, fontSize = 84.sp, lineHeight = 70.sp, letterSpacing = (-0.04).em)
    val paper = TextStyle(fontFamily = Body, fontSize = 14.5.sp, lineHeight = 22.sp)
}
