package de.briefampel.app.ui.screen

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import de.briefampel.app.domain.Ampel
import de.briefampel.app.domain.Deadline
import de.briefampel.app.ui.components.BriefIcons
import de.briefampel.app.ui.components.CircleIconButton
import de.briefampel.app.ui.components.DeadlineCard
import de.briefampel.app.ui.components.Disclaimer
import de.briefampel.app.ui.components.FactList
import de.briefampel.app.ui.components.LightState
import de.briefampel.app.ui.components.MonoLabel
import de.briefampel.app.ui.components.PrimaryButton
import de.briefampel.app.ui.components.SealedEnvelope
import de.briefampel.app.ui.components.TextLink
import de.briefampel.app.ui.components.TrafficLight
import de.briefampel.app.ui.theme.Brief
import de.briefampel.app.ui.theme.BriefType
import de.briefampel.app.ui.theme.Ink
import de.briefampel.app.ui.theme.Lamp
import de.briefampel.app.ui.theme.Paper
import kotlinx.coroutines.delay
import java.time.format.DateTimeFormatter

private val checkedFormat = DateTimeFormatter.ofPattern("dd.MM. · HH:mm")

@Composable
fun ResultScreen(state: UiState, onBack: () -> Unit, onReply: () -> Unit, onToggleEnvelope: () -> Unit) {
    val c = Brief.colors
    val a = state.analysis ?: return
    val today = Deadline.today()
    val context = LocalContext.current
    val scroll = rememberScrollState()
    val glow = Lamp.glow(a.ampel)
    LaunchedEffect(state.envelopeOpen) {
        if (state.envelopeOpen) {
            delay(250)
            scroll.animateScrollTo(scroll.maxValue)
        }
    }

    Box(Modifier.fillMaxSize().background(c.background)) {
        Box(
            Modifier.fillMaxSize().drawBehind {
                drawRect(
                    Brush.radialGradient(
                        0f to glow.copy(alpha = 0.30f), 0.5f to glow.copy(alpha = 0.09f), 1f to Color.Transparent,
                        center = Offset(62.dp.toPx(), 150.dp.toPx() + 98.dp.toPx() * a.ampel.ordinal * 0.66f - scroll.value),
                        radius = size.width * 1.1f,
                    ),
                )
            },
        )
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 170.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp),
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                CircleIconButton(BriefIcons.Back, "Zurück", onBack)
                MonoLabel("GEPRÜFT ${state.checkedAt?.let(checkedFormat::format).orEmpty()}")
                CircleIconButton(BriefIcons.Share, "Ergebnis teilen", {
                    val text = buildString {
                        appendLine("Briefampel: ${a.ampel.label} – ${a.title}")
                        appendLine(a.summary)
                        a.deadline?.let { appendLine("Frist: ${Deadline.formatDay(it)}") }
                    }
                    context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, text), null))
                })
            }

            Row(horizontalArrangement = Arrangement.spacedBy(22.dp), verticalAlignment = Alignment.Top) {
                TrafficLight(LightState.Lit(a.ampel, animate = false), lampSize = 52.dp, gap = 14.dp, padding = 16.dp)
                Column(Modifier.padding(top = 6.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    MonoLabel(a.ampel.tag, color = c.lamp(a.ampel))
                    Text(a.ampel.headline, style = BriefType.heroLarge, color = c.lamp(a.ampel), modifier = Modifier.semantics { heading() })
                    Text(a.ampel.promise, style = BriefType.bodySmall, color = c.textMuted)
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(a.title, style = BriefType.title, color = c.text, modifier = Modifier.semantics { heading() })
                Text(
                    listOfNotNull(a.sender, a.letterDate).joinToString(" · ") + (a.reference?.let { "\n$it" } ?: ""),
                    style = BriefType.data.copy(fontSize = BriefType.data.fontSize * 0.83f, lineHeight = BriefType.data.fontSize * 1.3f),
                    color = c.textFaint,
                )
                Text(a.summary, style = BriefType.lead, color = c.text, modifier = Modifier.padding(top = 6.dp))
            }

            a.deadline?.let { DeadlineCard(it, today, a.deadlineNote) }

            FactList(a.facts)

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                SealedEnvelope(state.input.pageCount.coerceAtLeast(1), state.envelopeOpen, onToggleEnvelope)
                AnimatedVisibility(state.envelopeOpen, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()) {
                    Text(
                        a.fullText.ifBlank { state.input.pastedText.orEmpty() },
                        style = BriefType.paper,
                        color = Ink,
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(24.dp, RoundedCornerShape(6.dp))
                            .background(Paper, RoundedCornerShape(6.dp))
                            .padding(horizontal = 22.dp, vertical = 24.dp),
                    )
                }
            }
        }

        Column(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Brush.verticalGradient(0f to c.background.copy(alpha = 0f), 0.38f to c.background))
                .navigationBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 40.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (a.replyNeeded || a.ampel != Ampel.GRUEN) {
                PrimaryButton("Antwort schreiben", onReply, icon = BriefIcons.Pen)
            } else {
                PrimaryButton(if (state.envelopeOpen) "Brief zuklappen" else "Brief jetzt lesen", onToggleEnvelope)
                TextLink("Antwort nicht nötig – trotzdem eine?", onReply, Modifier.fillMaxWidth().height(36.dp))
            }
            Disclaimer()
        }
    }
}
