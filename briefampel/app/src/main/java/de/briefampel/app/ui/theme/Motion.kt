package de.briefampel.app.ui.theme

import android.provider.Settings
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

object Motion {
    /** Weiches Abbremsen wie im Entwurf: cubic-bezier(.16,1,.3,1). */
    val Settle = CubicBezierEasing(0.16f, 1f, 0.3f, 1f)
    val Bounce = CubicBezierEasing(0.2f, 1.4f, 0.4f, 1f)
    const val PULSE_MS = 1800
    const val IGNITE_MS = 1200
    const val RISE_MS = 700
}

/** true, wenn „Animationen entfernen“ aktiv ist. */
val LocalReducedMotion = staticCompositionLocalOf { false }

@Composable
fun rememberReducedMotion(): Boolean {
    val context = LocalContext.current
    return remember {
        Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
    }
}
