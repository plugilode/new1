package de.briefampel.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.briefampel.app.domain.Ampel
import de.briefampel.app.ui.theme.Brief
import de.briefampel.app.ui.theme.Lamp
import de.briefampel.app.ui.theme.Motion
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

sealed interface LightState {
    data object Off : LightState
    data object Checking : LightState
    data class Lit(val ampel: Ampel, val animate: Boolean = true) : LightState
}

/** Glas jeder Lampe, wenn sie aus ist: man ahnt die Farbe noch. */
private val offGlass = mapOf(
    Ampel.ROT to listOf(Color(0xFF3B2421), Color(0xFF251615), Color(0xFF190F0E)),
    Ampel.GELB to listOf(Color(0xFF392E1A), Color(0xFF241D11), Color(0xFF18130C)),
    Ampel.GRUEN to listOf(Color(0xFF1C3327), Color(0xFF12211A), Color(0xFF0C1611)),
)

private val litGlass = mapOf(
    Ampel.ROT to listOf(Color(0xFFFFB3A6), Lamp.Red, Color(0xFFC42A1B)),
    Ampel.GELB to listOf(Color(0xFFFFE2A8), Lamp.Amber, Color(0xFFC07A12)),
    Ampel.GRUEN to listOf(Color(0xFFB4F7D3), Lamp.Green, Color(0xFF188A50)),
)

/**
 * Die Ampel, selbst gezeichnet. Das Gehäuse bleibt auch im hellen Modus dunkel – wie eine echte.
 */
@Composable
fun TrafficLight(
    state: LightState,
    modifier: Modifier = Modifier,
    lampSize: Dp = 76.dp,
    gap: Dp = 22.dp,
    padding: Dp = 20.dp,
) {
    val description = when (state) {
        LightState.Off -> "Ampel, noch aus"
        LightState.Checking -> "Ampel prüft den Brief"
        is LightState.Lit -> state.ampel.spoken
    }
    val width = lampSize + padding * 2
    Column(
        modifier = modifier
            .width(width)
            .semantics { contentDescription = description; role = Role.Image }
            .shadow(40.dp, RoundedCornerShape(width / 2), clip = false, ambientColor = Color.Black, spotColor = Color.Black)
            .background(Brush.verticalGradient(listOf(Color(0xFF1D2024), Color(0xFF0C0D0F))), RoundedCornerShape(width / 2))
            .border(1.dp, Color(0xFF24272C), RoundedCornerShape(width / 2))
            .drawBehind {
                // feine Lichtkante oben
                drawLine(Color.White.copy(alpha = 0.07f), Offset(size.width * 0.3f, 1f), Offset(size.width * 0.7f, 1f), strokeWidth = 1.5f)
            }
            .padding(vertical = padding + 2.dp, horizontal = padding),
        verticalArrangement = Arrangement.spacedBy(gap),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Ampel.entries.forEachIndexed { index, ampel ->
            Lamp(ampel = ampel, index = index, state = state, size = lampSize)
        }
    }
}

@Composable
private fun Lamp(ampel: Ampel, index: Int, state: LightState, size: Dp) {
    val reduced = Brief.reducedMotion
    Box(Modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(size)) { drawGlass(offGlass.getValue(ampel)) }

        when {
            state is LightState.Checking && !reduced -> {
                val pulse by rememberInfiniteTransition(label = "pulse$index").animateFloat(
                    initialValue = 0f,
                    targetValue = 0f,
                    animationSpec = InfiniteRepeatableSpec(
                        animation = keyframes {
                            durationMillis = Motion.PULSE_MS
                            0f at 0
                            0.9f at (Motion.PULSE_MS * 0.30f).toInt()
                            0f at (Motion.PULSE_MS * 0.62f).toInt()
                            0f at Motion.PULSE_MS
                        },
                        repeatMode = RepeatMode.Restart,
                        initialStartOffset = StartOffset(index * 600),
                    ),
                    label = "lamp$index",
                )
                LitLamp(ampel, size, alpha = pulse, scale = 1f)
            }
            state is LightState.Lit && state.ampel == ampel -> {
                val alpha = remember { Animatable(if (state.animate && !reduced) 0f else 1f) }
                val scale = remember { Animatable(if (state.animate && !reduced) 0.9f else 1f) }
                val flare = remember { Animatable(if (state.animate && !reduced) 1f else 0f) }
                LaunchedEffect(Unit) {
                    if (alpha.value < 1f) {
                        coroutineScope {
                            launch { alpha.animateTo(1f, keyframes { durationMillis = Motion.IGNITE_MS; 1f at (Motion.IGNITE_MS * 0.3f).toInt() }) }
                            launch {
                                scale.animateTo(1f, keyframes {
                                    durationMillis = Motion.IGNITE_MS
                                    1.06f at (Motion.IGNITE_MS * 0.3f).toInt() using Motion.Settle
                                })
                            }
                            launch { flare.animateTo(0f, keyframes { durationMillis = Motion.IGNITE_MS; 0.6f at (Motion.IGNITE_MS * 0.3f).toInt() }) }
                        }
                    }
                }
                LitLamp(ampel, size, alpha = alpha.value, scale = scale.value, flare = flare.value)
            }
        }
    }
}

@Composable
private fun LitLamp(ampel: Ampel, size: Dp, alpha: Float, scale: Float, flare: Float = 0f) {
    if (alpha <= 0.001f) return
    val glow = Lamp.glow(ampel)
    Canvas(Modifier.size(size).scale(scale)) {
        val r = this.size.minDimension / 2
        // Leuchtschein weit über die Lampe hinaus
        drawCircle(
            Brush.radialGradient(
                0f to glow.copy(alpha = 0.55f * alpha),
                0.45f to glow.copy(alpha = 0.22f * alpha),
                1f to Color.Transparent,
                center = center,
                radius = r * 3.2f,
            ),
            radius = r * 3.2f,
        )
        drawGlass(litGlass.getValue(ampel), alpha)
        if (flare > 0f) drawCircle(Color.White.copy(alpha = 0.35f * flare * alpha), radius = r)
    }
}

private fun DrawScope.drawGlass(stops: List<Color>, alpha: Float = 1f) {
    val r = size.minDimension / 2
    val highlight = Offset(size.width * 0.38f, size.height * 0.31f)
    drawCircle(
        Brush.radialGradient(
            0f to stops[0].copy(alpha = alpha),
            0.4f to stops[1].copy(alpha = alpha),
            1f to stops[2].copy(alpha = alpha),
            center = highlight,
            radius = r * 1.35f,
        ),
        radius = r,
    )
    // innere Schattenkante: Glas sitzt in einer Fassung
    drawCircle(
        Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.45f * alpha), Color.Transparent), startY = 0f, endY = size.height * 0.35f),
        radius = r,
    )
    drawCircle(Color.White.copy(alpha = 0.04f), radius = r - 0.5f, style = Stroke(1f))
}

/** Das kleine Markenzeichen neben dem Schriftzug. */
@Composable
fun AmpelMark(modifier: Modifier = Modifier) {
    Column(
        modifier
            .background(Color(0xFF0B0C0E), RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFF262A2F), RoundedCornerShape(8.dp))
            .padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        listOf(Lamp.Red, Lamp.Amber, Lamp.Green).forEach {
            Box(Modifier.size(6.dp).background(it, CircleShape))
        }
    }
}
