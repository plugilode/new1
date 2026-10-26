package de.briefampel.app.ui.theme

import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import de.briefampel.app.R

/**
 * Fraunces mit weicher Achse (SOFT) – klingt wie ein Mensch, nicht wie ein Formular.
 * opsz folgt der Größe, damit große Zeilen elegant und kleine robust bleiben.
 */
@OptIn(ExperimentalTextApi::class)
private fun fraunces(weight: Int, italic: Boolean = false, opsz: Float = 72f) = Font(
    if (italic) R.font.fraunces_italic else R.font.fraunces,
    weight = FontWeight(weight),
    style = if (italic) FontStyle.Italic else FontStyle.Normal,
    variationSettings = FontVariation.Settings(
        FontVariation.weight(weight),
        FontVariation.Setting("opsz", opsz),
        FontVariation.Setting("SOFT", 100f),
        FontVariation.Setting("WONK", 0f),
    ),
)

@OptIn(ExperimentalTextApi::class)
private fun nunito(weight: Int) = Font(
    R.font.nunito_sans,
    weight = FontWeight(weight),
    variationSettings = FontVariation.Settings(FontVariation.weight(weight), FontVariation.Setting("opsz", 12f)),
)

/** Überschriften, Zahlen, die persönliche Stimme. */
val Serif = FontFamily(
    fraunces(400), fraunces(500), fraunces(600),
    fraunces(400, italic = true), fraunces(500, italic = true),
)

/** Ruhiger, runder Fließtext. */
val Sans = FontFamily(nunito(400), nunito(600), nunito(700), nunito(800))

private val tight = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.Both)

object BriefType {
    val hero = TextStyle(fontFamily = Serif, fontWeight = FontWeight.Medium, fontSize = 38.sp, lineHeight = 41.sp, letterSpacing = (-0.02).em, lineHeightStyle = tight)
    val heroLarge = hero.copy(fontSize = 40.sp, lineHeight = 43.sp)
    val title = TextStyle(fontFamily = Serif, fontWeight = FontWeight.Medium, fontSize = 27.sp, lineHeight = 32.sp, letterSpacing = (-0.01).em)
    val titleSmall = title.copy(fontSize = 24.sp, lineHeight = 29.sp)
    val heading = TextStyle(fontFamily = Serif, fontWeight = FontWeight.Medium, fontSize = 34.sp, lineHeight = 38.sp, letterSpacing = (-0.02).em)
    val brand = TextStyle(fontFamily = Serif, fontWeight = FontWeight.SemiBold, fontSize = 21.sp, letterSpacing = (-0.01).em)
    /** Die persönliche Stimme: kursiv, wie eine Notiz von jemandem. */
    val voice = TextStyle(fontFamily = Serif, fontStyle = FontStyle.Italic, fontWeight = FontWeight.Normal, fontSize = 19.sp, lineHeight = 27.sp)
    val lead = TextStyle(fontFamily = Sans, fontSize = 18.sp, lineHeight = 27.sp)
    val body = TextStyle(fontFamily = Sans, fontSize = 16.5.sp, lineHeight = 25.sp)
    val bodySmall = TextStyle(fontFamily = Sans, fontSize = 15.sp, lineHeight = 22.sp)
    val button = TextStyle(fontFamily = Sans, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp)
    val buttonSmall = TextStyle(fontFamily = Sans, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    val fine = TextStyle(fontFamily = Sans, fontSize = 12.5.sp, lineHeight = 17.sp)
    val label = TextStyle(fontFamily = Sans, fontWeight = FontWeight.Bold, fontSize = 11.5.sp, letterSpacing = 0.12.em)
    val data = TextStyle(fontFamily = Sans, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
    val countdown = TextStyle(fontFamily = Serif, fontWeight = FontWeight.Medium, fontSize = 88.sp, lineHeight = 80.sp, letterSpacing = (-0.03).em)
    val paper = TextStyle(fontFamily = Sans, fontSize = 14.5.sp, lineHeight = 22.sp)
}
