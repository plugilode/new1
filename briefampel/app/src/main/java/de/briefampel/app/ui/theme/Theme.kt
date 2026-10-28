package de.briefampel.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import de.briefampel.app.domain.Deadline
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun BriefampelTheme(
    dark: Boolean = isSystemInDarkTheme(),
    reducedMotion: Boolean = rememberReducedMotion(),
    clock: Clock = Clock.system(Deadline.zone),
    content: @Composable () -> Unit,
) {
    val colors = if (dark) DarkColors else LightColors
    // Material nur für Bausteine wie das Bottom Sheet; Farben kommen aus BriefColors.
    val scheme = if (dark) {
        darkColorScheme(background = colors.background, surface = colors.surface, onSurface = colors.text, primary = colors.primary, onPrimary = colors.onPrimary)
    } else {
        lightColorScheme(background = colors.background, surface = colors.surface, onSurface = colors.text, primary = colors.primary, onPrimary = colors.onPrimary)
    }
    CompositionLocalProvider(
        LocalBriefColors provides colors,
        LocalReducedMotion provides reducedMotion,
        LocalClock provides clock,
    ) {
        MaterialTheme(colorScheme = scheme, content = content)
    }
}

val LocalClock = staticCompositionLocalOf<Clock> { Clock.system(Deadline.zone) }

object Brief {
    val colors: BriefColors
        @Composable get() = LocalBriefColors.current
    val reducedMotion: Boolean
        @Composable get() = LocalReducedMotion.current

    /** Heute in Berlin – über [LocalClock], damit Screenshots ein festes Datum haben. */
    val today: LocalDate
        @Composable get() = Deadline.today(LocalClock.current)

    val now: LocalDateTime
        @Composable get() = LocalDateTime.now(LocalClock.current)
}
