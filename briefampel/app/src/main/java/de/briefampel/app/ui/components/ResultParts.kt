package de.briefampel.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import de.briefampel.app.domain.Deadline
import de.briefampel.app.domain.Fact
import de.briefampel.app.ui.theme.Brief
import de.briefampel.app.ui.theme.BriefType
import de.briefampel.app.ui.theme.Chalk
import de.briefampel.app.ui.theme.Mono
import de.briefampel.app.ui.theme.Motion
import java.time.LocalDate

/** Ring, der sich zur Frist hin leert (14 Tage = voll). */
@Composable
fun DeadlineRing(deadline: LocalDate?, today: LocalDate, color: Color, modifier: Modifier = Modifier) {
    val c = Brief.colors
    val fraction = deadline?.let { (Deadline.daysLeft(it, today).coerceIn(0, 14) / 14f) } ?: 0f
    val animated by animateFloatAsState(fraction, tween(900, delayMillis = 500, easing = Motion.Settle), label = "ring")
    Canvas(modifier.size(44.dp)) {
        val stroke = 4.dp.toPx()
        val inset = stroke / 2 + 2.dp.toPx()
        val arcSize = Size(size.width - inset * 2, size.height - inset * 2)
        drawArc(c.line, 0f, 360f, false, Offset(inset, inset), arcSize, style = Stroke(stroke))
        drawArc(color, -90f, 360f * animated, false, Offset(inset, inset), arcSize, style = Stroke(stroke, cap = StrokeCap.Round))
    }
}

/** Große Frist-Karte: Resttage in 84er Mono und die Zwei-Wochen-Leiste. */
@Composable
fun DeadlineCard(deadline: LocalDate, today: LocalDate, note: String?, modifier: Modifier = Modifier) {
    val c = Brief.colors
    val days = Deadline.daysLeft(deadline, today)
    val hot = Deadline.isHot(deadline, today)
    val accent = if (hot) c.red else c.text
    Column(
        modifier
            .fillMaxWidth()
            .background(c.surface, RoundedCornerShape(24.dp))
            .border(1.dp, c.surfaceLine, RoundedCornerShape(24.dp))
            .padding(20.dp)
            .semantics(mergeDescendants = true) {
                contentDescription = "Frist: ${Deadline.daysLabel(days)}, bis ${Deadline.formatDay(deadline)}"
            },
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(if (days < 0) "!" else days.toString(), style = BriefType.countdown, color = accent, modifier = Modifier.alignByBaseline())
                Text(
                    if (days == 1L) "Tag" else if (days == 0L) "heute" else "Tage",
                    style = BriefType.title.copy(fontSize = BriefType.title.fontSize * 0.8f),
                    color = c.text,
                    modifier = Modifier.alignByBaseline(),
                )
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                MonoLabel("FRIST")
                Text(Deadline.formatDay(deadline), style = BriefType.data, color = c.text)
            }
        }
        Row(Modifier.fillMaxWidth().clearAndSetSemantics { }, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Deadline.barCells(deadline, today).forEachIndexed { i, cell ->
                val last = i == Deadline.BAR_CELLS - 1
                val color = when (cell) {
                    Deadline.Cell.PAST -> c.line
                    Deadline.Cell.TODAY -> c.text
                    Deadline.Cell.REMAINING -> if (hot) c.red else c.textFaint
                }
                Box(
                    Modifier
                        .weight(1f)
                        .height(18.dp)
                        .then(if (last && hot) Modifier.shadow(8.dp, RoundedCornerShape(3.dp), ambientColor = c.red, spotColor = c.red) else Modifier)
                        .background(color, RoundedCornerShape(3.dp)),
                )
            }
        }
        note?.let { Text(it, style = BriefType.bodySmall, color = c.textMuted) }
    }
}

/** Fakten als ruhige Liste, Label in Mono links. */
@Composable
fun FactList(facts: List<Fact>, modifier: Modifier = Modifier) {
    val c = Brief.colors
    Column(modifier.fillMaxWidth()) {
        facts.forEachIndexed { i, fact ->
            HorizontalDivider(color = c.surfaceLine)
            Row(
                Modifier.fillMaxWidth().padding(vertical = 16.dp).semantics(mergeDescendants = true) {},
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                MonoLabel(fact.label, Modifier.width(96.dp).padding(top = 4.dp), color = if (fact.emphasis) c.text else c.textFaint)
                Text(
                    highlightAmounts(fact.text),
                    style = BriefType.body.copy(fontWeight = if (fact.emphasis) FontWeight.SemiBold else FontWeight.Normal),
                    color = c.text,
                )
            }
            if (i == facts.lastIndex) HorizontalDivider(color = c.surfaceLine)
        }
    }
}

private val amount = Regex("""\d{1,3}(?:\.\d{3})*,\d{2} €""")

/** Beträge in Mono, damit sie sofort ins Auge fallen. */
private fun highlightAmounts(text: String) = buildAnnotatedString {
    var last = 0
    amount.findAll(text).forEach {
        append(text.substring(last, it.range.first))
        withStyle(SpanStyle(fontFamily = Mono, fontWeight = FontWeight.SemiBold)) { append(it.value) }
        last = it.range.last + 1
    }
    append(text.substring(last))
}

/** „Brief doch selbst lesen“ – der versiegelte Umschlag. */
@Composable
fun SealedEnvelope(pages: Int, open: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val c = Brief.colors
    val dash = PathEffect.dashPathEffect(floatArrayOf(10f, 8f))
    Row(
        modifier
            .fillMaxWidth()
            .pressable(onClick = onClick)
            .drawBehind {
                drawRoundRect(Color(0xFF3A3F46), cornerRadius = CornerRadius(20.dp.toPx()), style = Stroke(1.dp.toPx(), pathEffect = dash))
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(Modifier.size(64.dp, 46.dp)) {
            Canvas(Modifier.size(64.dp, 46.dp)) {
                drawRoundRect(Color(0xFFE9E5DC), cornerRadius = CornerRadius(5.dp.toPx()))
                drawRoundRect(Color(0xFFCFCBC1), cornerRadius = CornerRadius(5.dp.toPx()), style = Stroke(1f))
                val flap = Path().apply {
                    moveTo(2.dp.toPx(), 3.dp.toPx())
                    lineTo(size.width / 2, size.height * 0.54f)
                    lineTo(size.width - 2.dp.toPx(), 3.dp.toPx())
                }
                drawPath(flap, Color(0xFFBDB8AD), style = Stroke(1.5.dp.toPx()))
            }
            if (!open) {
                Box(
                    Modifier
                        .offset(22.dp, 15.dp)
                        .size(20.dp)
                        .shadow(2.dp, CircleShape)
                        .background(Brush.radialGradient(listOf(Color(0xFFFF7A69), Color(0xFFD63A2A), Color(0xFFA72417))), CircleShape),
                )
            }
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(if (open) "Brief wieder zuklappen" else "Brief doch selbst lesen", style = BriefType.body.copy(fontWeight = FontWeight.Bold), color = c.text)
            Text(
                if (open) "Unten steht der ganze Text" else "${pagesLabel(pages)} · bleibt zu, bis du willst",
                style = BriefType.bodySmall.copy(fontSize = BriefType.bodySmall.fontSize * 0.93f),
                color = c.textFaint,
            )
        }
        Icon(if (open) BriefIcons.ChevronDown else BriefIcons.Chevron, null, tint = c.textFaint, modifier = Modifier.size(18.dp))
    }
}

fun pagesLabel(pages: Int) = if (pages == 1) "1 Seite" else "$pages Seiten"

/** Eingereichte Seiten als kleiner, schräger Papierstapel. */
@Composable
fun PageStack(pages: Int, modifier: Modifier = Modifier) {
    val shown = pages.coerceIn(1, 3)
    val rotations = listOf(-7f, 2f, 8f).takeLast(shown)
    val lineSets = listOf(listOf(0.6f, 1f, 0.85f, 0.92f), listOf(1f, 0.78f, 0.94f), listOf(0.88f, 0.5f))
    Row(modifier.clearAndSetSemantics { }, horizontalArrangement = Arrangement.spacedBy((-22).dp), verticalAlignment = Alignment.Bottom) {
        rotations.forEachIndexed { i, r ->
            Column(
                Modifier
                    .rotate(r)
                    .size(58.dp, 80.dp)
                    .shadow(10.dp, RoundedCornerShape(4.dp))
                    .background(Chalk, RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                lineSets[i % lineSets.size].forEachIndexed { j, w ->
                    Box(Modifier.fillMaxWidth(w).height(3.dp).background(if (i == 0 && j == 0) Color(0xFFB9B5AC) else Color(0xFFD4D0C7), RoundedCornerShape(2.dp)))
                }
            }
        }
    }
}

@Composable
fun SectionHeading(text: String, modifier: Modifier = Modifier) {
    Text(text, style = BriefType.title, color = Brief.colors.text, modifier = modifier.semantics { heading() })
}

@Composable
fun VSpace(h: Int) = Spacer(Modifier.height(h.dp))
