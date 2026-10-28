package de.briefampel.app.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.briefampel.app.domain.Ampel
import de.briefampel.app.domain.Deadline
import de.briefampel.app.ui.Haptics
import de.briefampel.app.ui.Sounds
import de.briefampel.app.ui.components.AmpelMark
import de.briefampel.app.ui.components.BriefIcons
import de.briefampel.app.ui.components.CircleIconButton
import de.briefampel.app.ui.components.DeadlineRing
import de.briefampel.app.ui.components.Disclaimer
import de.briefampel.app.ui.components.LightState
import de.briefampel.app.ui.components.Eyebrow
import de.briefampel.app.ui.components.OutlineButton
import de.briefampel.app.ui.components.PageStack
import de.briefampel.app.ui.components.PrimaryButton
import de.briefampel.app.ui.components.TextLink
import de.briefampel.app.ui.components.TrafficLight
import de.briefampel.app.ui.components.Voice
import de.briefampel.app.ui.components.pagesLabel
import de.briefampel.app.ui.theme.Brief
import de.briefampel.app.ui.theme.BriefType
import de.briefampel.app.ui.theme.Lamp
import de.briefampel.app.ui.theme.Motion
import de.briefampel.app.ui.theme.Serif
import kotlinx.coroutines.delay
import java.time.format.DateTimeFormatter

private val dayMonth = DateTimeFormatter.ofPattern("dd.MM.")

@Composable
fun HomeScreen(
    state: UiState,
    onScan: () -> Unit,
    onPickFile: () -> Unit,
    onPaste: () -> Unit,
    onCheck: () -> Unit,
    onDiscard: () -> Unit,
    onOpenResult: () -> Unit,
    onOpenSender: () -> Unit,
    onRevealed: () -> Unit = {},
) {
    val c = Brief.colors
    val analysis = state.analysis
    val result = state.phase == Phase.RESULT && analysis != null
    val lightState = when {
        state.phase == Phase.CHECKING -> LightState.Checking
        result -> LightState.Lit(analysis!!.ampel, animate = state.revealPending)
        else -> LightState.Off
    }

    val context = LocalContext.current
    LaunchedEffect(result, state.revealPending) {
        if (result && state.revealPending) {
            Haptics.reveal(context, analysis!!.ampel)
            Sounds.reveal(context)
            onRevealed()
        }
    }

    var lightTop by remember { mutableStateOf(Offset.Zero) }
    Box(Modifier.fillMaxSize().background(c.background)) {
        AmbientLight(lightTop)
        if (result) ColorWash(analysis!!.ampel, lightTop, animate = state.revealPending)
        Pole(lightTop)

        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 16.dp),
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AmpelMark()
                    Column {
                        Text("Briefampel", style = BriefType.brand, color = c.text, modifier = Modifier.semantics { heading() })
                        Text(greeting(state.sender.name, Brief.now.hour), style = BriefType.fine.copy(fontStyle = FontStyle.Italic, fontFamily = Serif, fontSize = BriefType.fine.fontSize * 1.1f), color = c.textMuted)
                    }
                }
                CircleIconButton(BriefIcons.Person, "Absender und Einstellungen", onOpenSender)
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(22.dp)) {
                val density = LocalDensity.current
                TrafficLight(
                    state = lightState,
                    modifier = Modifier.onGloballyPositioned {
                        val b = it.boundsInRoot()
                        lightTop = Offset(b.center.x, b.top + with(density) { 22.dp.toPx() })
                    },
                )
                Headline(state, Modifier.padding(top = 14.dp))
            }
            Spacer(Modifier.weight(1f))

            AnimatedContent(
                targetState = state.phase,
                transitionSpec = { fadeIn(tween(500)) togetherWith fadeOut(tween(200)) },
                label = "phase",
            ) { phase ->
                when (phase) {
                    Phase.IDLE -> IdleActions(onScan, onPickFile, onPaste)
                    Phase.PAGES -> PagesActions(state, onCheck, onDiscard)
                    Phase.CHECKING -> CheckingSteps(state.input.pageCount)
                    Phase.RESULT -> Spacer(Modifier.height(1.dp))
                }
            }
            Spacer(Modifier.height(16.dp))
            Disclaimer()
        }

        AnimatedVisibility(
            visible = result,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically(tween(Motion.RISE_MS, delayMillis = 450, easing = Motion.Settle)) { it / 3 } + fadeIn(tween(Motion.RISE_MS, delayMillis = 450)),
            exit = fadeOut(tween(200)),
        ) {
            if (analysis != null) ResultPeek(state, onOpenResult, onDiscard)
        }
    }
}

private fun greeting(name: String, hour: Int): String {
    val hello = when (hour) {
        in 5..10 -> "Guten Morgen"
        in 11..17 -> "Schön, dass du da bist"
        else -> "Guten Abend"
    }
    val first = name.trim().substringBefore(' ')
    return if (first.isNotEmpty()) "$hello, $first." else "$hello."
}

/** Warmes Licht, das immer im Raum liegt – als stünde eine Lampe neben dir. */
@Composable
private fun AmbientLight(lightTop: Offset) {
    val c = Brief.colors
    Box(
        Modifier.fillMaxSize().drawBehind {
            val center = if (lightTop == Offset.Zero) Offset(size.width * 0.2f, size.height * 0.25f) else Offset(lightTop.x, lightTop.y + 140.dp.toPx())
            drawRect(
                Brush.radialGradient(
                    0f to c.ambient.copy(alpha = if (c.isDark) 0.16f else 0.22f),
                    0.55f to c.ambient.copy(alpha = if (c.isDark) 0.05f else 0.07f),
                    1f to Color.Transparent,
                    center = center,
                    radius = size.maxDimension * 0.75f,
                ),
            )
        },
    )
}

/** Der Mast unter dem Gehäuse, der nach unten ins Dunkel ausläuft. */
@Composable
private fun Pole(lightTop: Offset) {
    if (lightTop == Offset.Zero) return
    val density = LocalDensity.current
    val pole = Brief.colors.housingBottom
    Box(
        Modifier.fillMaxSize().drawBehind {
            val w = with(density) { 14.dp.toPx() }
            val top = lightTop.y + with(density) { 300.dp.toPx() }
            drawRect(
                Brush.verticalGradient(0f to pole, 0.7f to pole.copy(alpha = 0f), startY = top, endY = size.height),
                topLeft = Offset(lightTop.x - w / 2, top),
                size = androidx.compose.ui.geometry.Size(w, size.height - top),
            )
        },
    )
}

/** Der Schein der gezündeten Lampe färbt den Bildschirm ganz leicht ein. */
@Composable
private fun ColorWash(ampel: Ampel, lightTop: Offset, animate: Boolean) {
    val still = Brief.reducedMotion || !animate
    val alpha = remember(ampel) { Animatable(if (still) 0.75f else 0f) }
    LaunchedEffect(ampel) {
        if (alpha.value == 0f) {
            alpha.animateTo(1f, tween(560, delayMillis = 100, easing = Motion.Settle))
            alpha.animateTo(0.75f, tween(1040, easing = Motion.Settle))
        }
    }
    val glow = Lamp.glow(ampel)
    val density = LocalDensity.current
    val lampStep = with(density) { 98.dp.toPx() }
    val half = with(density) { 38.dp.toPx() }
    val center = Offset(lightTop.x, lightTop.y + half + lampStep * ampel.ordinal)
    Box(
        Modifier
            .fillMaxSize()
            .alpha(alpha.value)
            .drawBehind {
                val r = size.maxDimension * 0.62f
                drawRect(
                    Brush.radialGradient(
                        0f to glow.copy(alpha = 0.34f),
                        0.5f to glow.copy(alpha = 0.10f),
                        1f to Color.Transparent,
                        center = center,
                        radius = r,
                    ),
                )
            },
    )
}

@Composable
private fun Headline(state: UiState, modifier: Modifier = Modifier) {
    val c = Brief.colors
    val analysis = state.analysis
    val result = state.phase == Phase.RESULT && analysis != null
    val (head, sub) = when {
        result -> analysis!!.ampel.headline to analysis.ampel.tag
        state.phase == Phase.CHECKING -> "Einen\nMoment …" to "Ich lese ganz in Ruhe. Du musst nichts tun."
        state.phase == Phase.PAGES -> "Danke.\nIch hab ihn." to "${pagesLabel(state.input.pageCount)} im Umschlag. Wenn du so weit bist, schau ich rein."
        else -> "Gib mir\nden Brief." to "Ich lese ihn zuerst und sag dir in Ruhe, was drinsteht."
    }
    val color = if (result) c.lamp(analysis!!.ampel) else c.text
    AnimatedContent(
        targetState = Triple(head, sub, color),
        transitionSpec = { fadeIn(tween(450)) togetherWith fadeOut(tween(180)) },
        modifier = modifier.semantics { liveRegion = LiveRegionMode.Polite },
        label = "headline",
    ) { (h, s, col) ->
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(h, style = BriefType.hero, color = col, modifier = Modifier.semantics { heading() })
            if (result) {
                Eyebrow(s, color = col)
            } else {
                Voice(s, color = c.textSoft)
            }
        }
    }
}

@Composable
private fun IdleActions(onScan: () -> Unit, onPickFile: () -> Unit, onPaste: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            "Du kannst mir einen Brief auch aus der Mail-App teilen.",
            style = BriefType.bodySmall.copy(fontSize = BriefType.bodySmall.fontSize * 0.93f),
            color = Brief.colors.textFaint,
            modifier = Modifier.padding(start = 104.dp, bottom = 6.dp),
        )
        PrimaryButton("Brief scannen", onScan, icon = BriefIcons.Scan)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlineButton("Datei", onPickFile, Modifier.weight(1f), icon = BriefIcons.File)
            OutlineButton("Text einfügen", onPaste, Modifier.weight(1f), icon = BriefIcons.Paste)
        }
    }
}

@Composable
private fun PagesActions(state: UiState, onCheck: () -> Unit, onDiscard: () -> Unit) {
    val c = Brief.colors
    val source = if (state.input.pages.isEmpty()) "Text" else if (state.input.pages.all { it.mimeType == "application/pdf" }) "PDF" else "Scan"
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(Modifier.padding(start = 116.dp, bottom = 20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Eyebrow("IM UMSCHLAG")
            PageStack(state.input.pageCount)
            Text(
                "${pagesLabel(state.input.pageCount)} · $source ${state.receivedAt?.let { dayMonth.format(it) }.orEmpty()}",
                style = BriefType.data.copy(fontSize = BriefType.data.fontSize * 0.87f),
                color = c.textSoft,
            )
        }
        state.error?.let {
            Text(it.message, style = BriefType.bodySmall, color = c.text, modifier = Modifier.semantics { liveRegion = LiveRegionMode.Assertive })
        }
        PrimaryButton(if (state.error != null) "Noch mal versuchen" else "Lies du ihn für mich", onCheck)
        TextLink("Doch nicht", onDiscard, Modifier.fillMaxWidth())
    }
}

@Composable
private fun CheckingSteps(pages: Int) {
    val c = Brief.colors
    var step by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (step < 2) {
            delay(950)
            step++
        }
    }
    val steps = listOf(if (pages == 1) "Ich lese den Brief" else "Ich lese alle $pages Seiten", "Ich schaue, wer schreibt und wie", "Ich suche nach Fristen")
    Column(
        Modifier.padding(start = 116.dp, bottom = 120.dp).semantics { liveRegion = LiveRegionMode.Polite },
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        steps.forEachIndexed { i, label ->
            val done = i < step
            val active = i == step
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                val dotAlpha = if (active) blink() else 1f
                Box(
                    Modifier
                        .size(8.dp)
                        .alpha(dotAlpha)
                        .then(
                            if (done || active) Modifier.background(c.text, CircleShape)
                            else Modifier.background(Color.Transparent, CircleShape).drawBehind {
                                drawCircle(c.textFaint, style = Stroke(1.5.dp.toPx()))
                            },
                        ),
                )
                Text(
                    label,
                    style = BriefType.bodySmall,
                    color = when {
                        active -> c.text
                        done -> c.textSoft
                        else -> c.textFaint
                    },
                )
            }
        }
    }
}

@Composable
private fun blink(): Float {
    if (Brief.reducedMotion) return 1f
    val a by rememberInfiniteTransition(label = "blink").animateFloat(0.25f, 1f, infiniteRepeatable(tween(500), RepeatMode.Reverse), label = "dot")
    return a
}

/** Ergebnis-Karte, die unter der Ampel hereingleitet. */
@Composable
private fun ResultPeek(state: UiState, onOpenResult: () -> Unit, onDiscard: () -> Unit) {
    val c = Brief.colors
    val a = state.analysis ?: return
    val today = Brief.today
    val color = c.lamp(a.ampel)
    Column(
        Modifier
            .fillMaxWidth()
            .shadow(30.dp, RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp), clip = false, ambientColor = Color(0xFF5A3A22), spotColor = Color(0xFF5A3A22))
            .background(c.surface, RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
            .navigationBarsPadding()
            .padding(start = 20.dp, end = 20.dp, top = 26.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Voice("Ich hab ihn gelesen.", color = c.textMuted)
        Text(a.title, style = BriefType.titleSmall, color = c.text)
        Eyebrow(listOfNotNull(a.sender, a.letterDate).joinToString(" · "))
        Text(a.summary, style = BriefType.body, color = c.textSoft)
        Spacer(Modifier.height(4.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DeadlineRing(a.deadline, today, color)
                Column {
                    val d = a.deadline
                    Text(
                        if (d != null) "noch ${Deadline.daysLabel(Deadline.daysLeft(d, today))}" else "keine Frist",
                        style = BriefType.data.copy(fontWeight = FontWeight.SemiBold, fontSize = BriefType.data.fontSize * 1.07f),
                        color = color,
                    )
                    Text(
                        if (d != null) "bis ${Deadline.formatShort(d)}" else "nichts zu tun",
                        style = BriefType.data.copy(fontSize = BriefType.data.fontSize * 0.83f, fontWeight = FontWeight.Normal),
                        color = c.textFaint,
                    )
                }
            }
            OutlineButton("Neuer Brief", onDiscard, height = 44.dp)
        }
        PrimaryButton(
            when (a.ampel) {
                Ampel.ROT -> "Was heißt das für mich?"
                Ampel.GELB -> "Was ist zu tun?"
                Ampel.GRUEN -> "Zeig mir, was drinsteht"
            },
            onOpenResult,
            trailingIcon = BriefIcons.Arrow,
            height = 60.dp,
        )
        Disclaimer()
    }
}
