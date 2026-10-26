package de.briefampel.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.briefampel.app.domain.Ampel
import de.briefampel.app.ui.theme.Brief
import de.briefampel.app.ui.theme.BriefColors
import de.briefampel.app.ui.theme.Lamp
import de.briefampel.app.ui.theme.Motion
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

sealed interface LightState {
    data object Off : LightState
    data object Checking : LightState
    data class Lit(val ampel: Ampel, val animate: Boolean = true) : LightState
}

/**
 * Die Ampel als warmes Licht in einem Gehäuse aus Keramik – eher Nachttischlampe als Straßenkreuzung.
 * Ausgeschaltete Lampen sind nur ein Hauch ihrer Farbe; die leuchtende glüht weich nach außen.
 */
@Composable
fun TrafficLight(
    state: LightState,
    modifier: Modifier = Modifier,
    lampSize: Dp = 72.dp,
    gap: Dp = 18.dp,
    padding: Dp = 18.dp,
) {
    val c = Brief.colors
    val description = when (state) {
        LightState.Off -> "Ampel, noch aus"
        LightState.Checking -> "Ampel liest den Brief"
        is LightState.Lit -> state.ampel.spoken
    }
    val width = lampSize + padding * 2
    val shape = RoundedCornerShape(width / 2)
    Column(
        modifier = modifier
            .width(width)
            .semantics { contentDescription = description; role = Role.Image }
            .shadow(if (c.isDark) 24.dp else 18.dp, shape, clip = false, ambientColor = Color(0xFF5A3A22), spotColor = Color(0xFF5A3A22))
            .background(Brush.verticalGradient(listOf(c.housingTop, c.housingBottom)), shape)
            .border(1.dp, c.housingEdge, shape)
            .padding(vertical = padding + 2.dp, horizontal = padding),
        verticalArrangement = Arrangement.spacedBy(gap),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Ampel.entries.forEachIndexed { index, ampel ->
            Lamp(ampel = ampel, index = index, state = state, size = lampSize, colors = c)
        }
    }
}

@Composable
private fun Lamp(ampel: Ampel, index: Int, state: LightState, size: Dp, colors: BriefColors) {
    val reduced = Brief.reducedMotion
    val glow = Lamp.glow(ampel)
    Box(Modifier.size(size), contentAlignment = Alignment.Center) {
        // Ausgeschaltet: eine kleine, eingelassene Schale mit einem Hauch Farbe.
        Canvas(Modifier.size(size)) { drawOffLamp(glow, colors) }

        when {
            state is LightState.Checking && !reduced -> {
                val pulse by rememberInfiniteTransition(label = "pulse$index").animateFloat(
                    initialValue = 0f,
                    targetValue = 0f,
                    animationSpec = InfiniteRepeatableSpec(
                        animation = keyframes {
                            durationMillis = Motion.PULSE_MS
                            0f at 0
                            0.75f at (Motion.PULSE_MS * 0.32f).toInt()
                            0f at (Motion.PULSE_MS * 0.68f).toInt()
                            0f at Motion.PULSE_MS
                        },
                        repeatMode = RepeatMode.Restart,
                        initialStartOffset = StartOffset(index * 600),
                    ),
                    label = "lamp$index",
                )
                LitLamp(glow, size, alpha = pulse, scale = 0.96f + 0.04f * pulse)
            }
            state is LightState.Lit && state.ampel == ampel -> {
                val animate = state.animate && !reduced
                val alpha = remember { Animatable(if (animate) 0f else 1f) }
                val scale = remember { Animatable(if (animate) 0.92f else 1f) }
                LaunchedEffect(Unit) {
                    if (animate) {
                        coroutineScope {
                            launch { alpha.animateTo(1f, tween(Motion.IGNITE_MS, easing = Motion.Settle)) }
                            launch {
                                scale.animateTo(1f, keyframes {
                                    durationMillis = Motion.IGNITE_MS
                                    1.04f at (Motion.IGNITE_MS * 0.35f).toInt() using Motion.Settle
                                })
                            }
                        }
                    }
                }
                LitLamp(glow, size, alpha = alpha.value, scale = scale.value)
            }
        }
    }
}

@Composable
private fun LitLamp(glow: Color, size: Dp, alpha: Float, scale: Float) {
    if (alpha <= 0.001f) return
    Canvas(Modifier.size(size).scale(scale)) {
        val r = this.size.minDimension / 2
        // weicher Hof, weit über die Lampe hinaus
        drawCircle(
            Brush.radialGradient(
                0f to glow.copy(alpha = 0.50f * alpha),
                0.35f to glow.copy(alpha = 0.22f * alpha),
                0.7f to glow.copy(alpha = 0.06f * alpha),
                1f to Color.Transparent,
                center = center,
                radius = r * 3.4f,
            ),
            radius = r * 3.4f,
        )
        // der Kern: hell in der Mitte, satt am Rand, wie Glas mit Licht dahinter
        drawCircle(
            Brush.radialGradient(
                0f to lerp(glow, Color.White, 0.55f).copy(alpha = alpha),
                0.45f to glow.copy(alpha = alpha),
                1f to lerp(glow, Color(0xFF5A2A10), 0.25f).copy(alpha = alpha),
                center = Offset(center.x - r * 0.18f, center.y - r * 0.22f),
                radius = r * 1.25f,
            ),
            radius = r,
        )
        drawCircle(Color.White.copy(alpha = 0.18f * alpha), radius = r * 0.32f, center = Offset(center.x - r * 0.28f, center.y - r * 0.34f))
    }
}

private fun DrawScope.drawOffLamp(glow: Color, c: BriefColors) {
    val r = size.minDimension / 2
    val base = lerp(c.housingBottom, glow, if (c.isDark) 0.16f else 0.10f)
    drawCircle(
        Brush.radialGradient(
            0f to lerp(base, Color.Black, if (c.isDark) 0.25f else 0.06f),
            1f to base,
            center = Offset(center.x, center.y - r * 0.4f),
            radius = r * 1.4f,
        ),
        radius = r,
    )
    // eingelassen: oben Schatten, unten Lichtkante
    drawCircle(
        Brush.verticalGradient(
            0f to Color.Black.copy(alpha = if (c.isDark) 0.35f else 0.10f),
            0.45f to Color.Transparent,
            0.85f to Color.Transparent,
            1f to Color.White.copy(alpha = if (c.isDark) 0.06f else 0.6f),
        ),
        radius = r,
    )
}

/** Das kleine Markenzeichen neben dem Schriftzug. */
@Composable
fun AmpelMark(modifier: Modifier = Modifier) {
    val c = Brief.colors
    Column(
        modifier
            .background(Brush.verticalGradient(listOf(c.housingTop, c.housingBottom)), RoundedCornerShape(8.dp))
            .border(1.dp, c.housingEdge, RoundedCornerShape(8.dp))
            .padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        listOf(Lamp.Red, Lamp.Amber, Lamp.Green).forEach {
            Box(Modifier.size(6.dp).background(it, CircleShape))
        }
    }
}
