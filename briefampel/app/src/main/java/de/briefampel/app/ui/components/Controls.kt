package de.briefampel.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.briefampel.app.ui.theme.Brief
import de.briefampel.app.ui.theme.BriefType
import de.briefampel.app.ui.theme.Motion

/** Klick mit leichtem Eindrücken (.97) wie im Entwurf. */
@Composable
fun Modifier.pressable(enabled: Boolean = true, role: Role = Role.Button, onClick: () -> Unit): Modifier {
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.97f else 1f, tween(150, easing = Motion.Bounce), label = "press")
    return this
        .scale(scale)
        .clickable(interactionSource = source, indication = null, enabled = enabled, role = role, onClick = onClick)
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    height: Dp = 64.dp,
    enabled: Boolean = true,
) {
    val c = Brief.colors
    Row(
        modifier
            .fillMaxWidth()
            .height(height)
            .pressable(enabled, onClick = onClick)
            .background(if (enabled) c.primary else c.primary.copy(alpha = 0.4f), RoundedCornerShape(height / 2)),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        icon?.let { Icon(it, null, tint = c.onPrimary, modifier = Modifier.padding(end = 10.dp).size(22.dp)) }
        Text(text, style = BriefType.button.copy(fontSize = if (height < 64.dp) BriefType.button.fontSize * 0.95f else BriefType.button.fontSize), color = c.onPrimary)
        trailingIcon?.let { Icon(it, null, tint = c.onPrimary, modifier = Modifier.padding(start = 8.dp).size(18.dp)) }
    }
}

@Composable
fun OutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    height: Dp = 56.dp,
    style: TextStyle = BriefType.buttonSmall,
) {
    val c = Brief.colors
    Row(
        modifier
            .height(height)
            .pressable(onClick = onClick)
            .background(c.text.copy(alpha = 0.02f), RoundedCornerShape(height / 2))
            .border(1.dp, c.line, RoundedCornerShape(height / 2))
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        icon?.let { Icon(it, null, tint = c.text, modifier = Modifier.padding(end = 8.dp).size(19.dp)) }
        Text(text, style = style, color = c.text)
    }
}

@Composable
fun CircleIconButton(icon: ImageVector, description: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val c = Brief.colors
    Box(
        modifier
            .size(44.dp)
            .clip(CircleShape)
            .clickable(role = Role.Button, onClickLabel = description, onClick = onClick)
            .background(c.background.copy(alpha = 0.6f), CircleShape)
            .border(1.dp, c.surfaceLine, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, description, tint = c.text, modifier = Modifier.size(20.dp))
    }
}

@Composable
fun TextLink(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, color: Color = Brief.colors.textMuted) {
    Box(
        modifier
            .height(44.dp)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, style = BriefType.buttonSmall, color = color)
    }
}

@Composable
fun Chip(text: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val c = Brief.colors
    val haptics = LocalHapticFeedback.current
    Row(
        modifier
            .height(44.dp)
            .pressable(role = Role.Checkbox) {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            }
            .then(
                if (selected) Modifier.background(c.primary, RoundedCornerShape(22.dp))
                else Modifier.border(1.dp, c.line, RoundedCornerShape(22.dp)),
            )
            .padding(start = if (selected) 12.dp else 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (selected) Icon(BriefIcons.Check, null, tint = c.onPrimary, modifier = Modifier.padding(end = 6.dp).size(16.dp))
        Text(text, style = BriefType.buttonSmall, color = if (selected) c.onPrimary else c.textSoft)
    }
}

@Composable
fun Disclaimer(modifier: Modifier = Modifier) {
    Text(
        "Sortiert und entwirft. Ersetzt keine Rechtsberatung – Fristen selbst gegenprüfen.",
        style = BriefType.fine,
        color = Brief.colors.textFaint,
        modifier = modifier,
    )
}

@Composable
fun MonoLabel(text: String, modifier: Modifier = Modifier, color: Color = Brief.colors.textFaint) {
    Text(text, style = BriefType.label, color = color, modifier = modifier)
}
