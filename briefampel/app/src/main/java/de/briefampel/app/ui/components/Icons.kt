package de.briefampel.app.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

/** Linien-Icons aus dem Design-Entwurf (24er Raster). */
object BriefIcons {
    private fun line(name: String, strokeWidth: Float, vararg paths: String) = ImageVector.Builder(
        name = name, defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f,
    ).apply {
        paths.forEach {
            addPath(
                pathData = addPathNodes(it),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = strokeWidth,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            )
        }
    }.build()

    val Scan = line("scan", 2f, "M4 8V5a1 1 0 0 1 1-1h3M16 4h3a1 1 0 0 1 1 1v3M20 16v3a1 1 0 0 1-1 1h-3M8 20H5a1 1 0 0 1-1-1v-3", "M7 12h10")
    val File = line("file", 1.8f, "M14 3H7a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V8z", "M14 3v5h5")
    val Paste = line("paste", 1.8f, "M9 3h6v3H9z", "M15 4.5h2a2 2 0 0 1 2 2V19a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6.5a2 2 0 0 1 2-2h2", "M9 12h6M9 16h4")
    val Person = line("person", 1.8f, "M8 8a4 4 0 1 0 8 0a4 4 0 1 0-8 0", "M4 21c1.5-4 4.5-6 8-6s6.5 2 8 6")
    val Back = line("back", 2f, "M15 6l-6 6 6 6")
    val Share = line("share", 1.8f, "M12 15V3M7 8l5-5 5 5", "M5 13v6a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2v-6")
    val Arrow = line("arrow", 2.2f, "M5 12h14M13 6l6 6-6 6")
    val Chevron = line("chevron", 2f, "M9 6l6 6-6 6")
    val ChevronDown = line("chevronDown", 2f, "M6 9l6 6 6-6")
    val Pen = line("pen", 2f, "M4 20l4-1L19 8l-3-3L5 16z", "M14 7l3 3")
    val Check = line("check", 2.6f, "M5 12.5l4.5 4.5L19 7.5")
    val Copy = line("copy", 1.8f, "M9 9h10a1 1 0 0 1 1 1v10a1 1 0 0 1-1 1H9a1 1 0 0 1-1-1V10a1 1 0 0 1 1-1z", "M5 15H4a1 1 0 0 1-1-1V4a1 1 0 0 1 1-1h10a1 1 0 0 1 1 1v1")
    val Refresh = line("refresh", 1.8f, "M20 11a8 8 0 1 0-2.3 5.7", "M20 4v7h-7")
    val Close = line("close", 2f, "M6 6l12 12M18 6L6 18")
}
