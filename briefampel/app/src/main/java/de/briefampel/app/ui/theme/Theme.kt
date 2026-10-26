package de.briefampel.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun BriefampelTheme(
    dark: Boolean = isSystemInDarkTheme(),
    reducedMotion: Boolean = rememberReducedMotion(),
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
    ) {
        MaterialTheme(colorScheme = scheme, content = content)
    }
}

object Brief {
    val colors: BriefColors
        @Composable get() = LocalBriefColors.current
    val reducedMotion: Boolean
        @Composable get() = LocalReducedMotion.current
}
