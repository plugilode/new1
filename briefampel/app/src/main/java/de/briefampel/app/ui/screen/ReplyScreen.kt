package de.briefampel.app.ui.screen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import de.briefampel.app.domain.Keywords
import de.briefampel.app.domain.Sender
import de.briefampel.app.domain.Tone
import de.briefampel.app.ui.components.BriefIcons
import de.briefampel.app.ui.components.Chip
import de.briefampel.app.ui.components.CircleIconButton
import de.briefampel.app.ui.components.Disclaimer
import de.briefampel.app.ui.components.MonoLabel
import de.briefampel.app.ui.components.OutlineButton
import de.briefampel.app.ui.components.PrimaryButton
import de.briefampel.app.ui.theme.Brief
import de.briefampel.app.ui.theme.BriefType
import de.briefampel.app.ui.theme.Ink
import de.briefampel.app.ui.theme.Lamp
import de.briefampel.app.ui.theme.Paper

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReplyScreen(
    state: UiState,
    onBack: () -> Unit,
    onToggleSuggestion: (String) -> Unit,
    onKeywords: (String) -> Unit,
    onTone: (Tone) -> Unit,
    onSaveSender: (Sender) -> Unit,
    onWrite: () -> Unit,
    onStop: () -> Unit,
) {
    val c = Brief.colors
    val a = state.analysis ?: return
    val reply = state.reply
    val context = LocalContext.current
    val scroll = rememberScrollState()

    // Mitscrollen, solange der Nutzer nicht selbst scrollt.
    var follow by remember { mutableStateOf(true) }
    LaunchedEffect(scroll) {
        scroll.interactionSource.interactions.collect { if (it is DragInteraction.Start) follow = false }
    }
    LaunchedEffect(reply.writing) { if (reply.writing) follow = true }
    LaunchedEffect(reply.text) { if (reply.writing && follow) scroll.animateScrollTo(scroll.maxValue) }

    Column(
        Modifier
            .fillMaxSize()
            .background(c.background)
            .verticalScroll(scroll)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            CircleIconButton(BriefIcons.Back, "Zurück zum Ergebnis", onBack)
            Box(Modifier.size(10.dp).shadow(6.dp, CircleShape, ambientColor = Lamp.glow(a.ampel), spotColor = Lamp.glow(a.ampel)).background(Lamp.glow(a.ampel), CircleShape))
            MonoLabel(("AN " + a.sender.uppercase() + (a.reference?.let { " · $it" } ?: "")), Modifier.weight(1f))
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Was soll rein?", style = BriefType.heading, color = c.text, modifier = Modifier.semantics { heading() })
            Text("Stichworte reichen. Den Rest schreibe ich – und erfinde nichts dazu.", style = BriefType.body, color = c.textMuted)
        }

        if (a.suggestions.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                MonoLabel("VORSCHLÄGE · ANTIPPEN")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    a.suggestions.forEach { s ->
                        Chip(s, Keywords.contains(reply.keywords, s), { onToggleSuggestion(s) })
                    }
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            MonoLabel("DEINE STICHWORTE")
            BasicTextField(
                value = reply.keywords,
                onValueChange = onKeywords,
                textStyle = BriefType.body.copy(color = c.text),
                cursorBrush = SolidColor(c.text),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 112.dp)
                    .semantics { contentDescription = "Deine Stichworte" }
                    .background(c.surface, RoundedCornerShape(18.dp))
                    .border(1.dp, c.line, RoundedCornerShape(18.dp))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            MonoLabel("TON")
            ToneSegmented(reply.tone, onTone)
        }

        SenderRow(state.sender, onSaveSender)

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            val words = Keywords.lines(reply.text.replace(' ', '\n')).size
            val status = when {
                reply.writing -> "SCHREIBT · $words WÖRTER"
                reply.text.isNotEmpty() -> "FERTIG · $words WÖRTER"
                else -> "NOCH NICHTS GESCHRIEBEN"
            }
            Row(
                Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                if (reply.writing) Box(Modifier.size(8.dp).alpha(pulse()).background(c.text, CircleShape))
                MonoLabel(status, color = if (reply.writing) c.text else c.textFaint)
            }
            if (reply.writing) {
                Row(
                    Modifier
                        .height(44.dp)
                        .clickable(role = Role.Button, onClick = onStop)
                        .border(1.dp, c.line, RoundedCornerShape(22.dp))
                        .padding(start = 14.dp, end = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(Modifier.size(10.dp).background(c.text, RoundedCornerShape(2.dp)))
                    Text("Stopp", style = BriefType.buttonSmall.copy(fontWeight = FontWeight.Bold), color = c.text)
                }
            }
        }

        if (reply.text.isEmpty() && !reply.writing) {
            PrimaryButton("Antwort schreiben", onWrite, icon = BriefIcons.Pen, enabled = Keywords.lines(reply.keywords).isNotEmpty())
        } else {
            PaperLetter(reply.text, reply.writing)
        }

        AnimatedVisibility(reply.text.isNotEmpty() && !reply.writing) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                PrimaryButton("Kopieren", {
                    val cm = context.getSystemService(ClipboardManager::class.java)
                    cm?.setPrimaryClip(ClipData.newPlainText("Antwort", reply.text))
                    Toast.makeText(context, "Kopiert", Toast.LENGTH_SHORT).show()
                }, icon = BriefIcons.Copy)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlineButton("Teilen", {
                        context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, reply.text), null))
                    }, Modifier.weight(1f), icon = BriefIcons.Share)
                    OutlineButton("Neu formulieren", onWrite, Modifier.weight(1f), icon = BriefIcons.Refresh)
                }
            }
        }

        Disclaimer()
    }
}

@Composable
private fun pulse(): Float {
    if (Brief.reducedMotion) return 1f
    val a by rememberInfiniteTransition(label = "pulse").animateFloat(0.25f, 1f, infiniteRepeatable(tween(500), RepeatMode.Reverse), label = "p")
    return a
}

@Composable
private fun ToneSegmented(tone: Tone, onTone: (Tone) -> Unit) {
    val c = Brief.colors
    Row(
        Modifier
            .fillMaxWidth()
            .background(c.surface, RoundedCornerShape(26.dp))
            .border(1.dp, c.surfaceLine, RoundedCornerShape(26.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Tone.entries.forEach { t ->
            val on = t == tone
            Box(
                Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clickable(role = Role.RadioButton) { onTone(t) }
                    .semantics { selected = on; role = Role.RadioButton }
                    .then(if (on) Modifier.background(c.primary, RoundedCornerShape(22.dp)) else Modifier),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    t.label,
                    style = BriefType.buttonSmall.copy(fontSize = BriefType.buttonSmall.fontSize * 0.97f, fontWeight = if (on) FontWeight.Bold else FontWeight.SemiBold),
                    color = if (on) c.onPrimary else c.textMuted,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun SenderRow(sender: Sender, onSave: (Sender) -> Unit) {
    val c = Brief.colors
    var open by rememberSaveable { mutableStateOf(false) }
    Column(
        Modifier
            .fillMaxWidth()
            .border(1.dp, c.surfaceLine, RoundedCornerShape(18.dp)),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable(role = Role.Button, onClickLabel = if (open) "Absender zuklappen" else "Absender bearbeiten") { open = !open }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                MonoLabel("ABSENDER · NUR AUF DIESEM HANDY")
                Text(sender.oneLine().ifBlank { "Noch leer – antippen zum Ausfüllen" }, style = BriefType.bodySmall, color = if (sender.isEmpty) c.textFaint else c.text)
            }
            Icon(if (open) BriefIcons.Close else BriefIcons.ChevronDown, null, tint = c.textFaint, modifier = Modifier.size(18.dp))
        }
        AnimatedVisibility(open) {
            SenderFields(sender, onSave, Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp))
        }
    }
}

@Composable
fun SenderFields(sender: Sender, onSave: (Sender) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Field("NAME", sender.name) { onSave(sender.copy(name = it)) }
        Field("STRASSE UND NR.", sender.street) { onSave(sender.copy(street = it)) }
        Field("PLZ UND ORT", sender.city) { onSave(sender.copy(city = it)) }
        Field("E-MAIL (OPTIONAL)", sender.email) { onSave(sender.copy(email = it)) }
    }
}

@Composable
private fun Field(label: String, value: String, onChange: (String) -> Unit) {
    val c = Brief.colors
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        MonoLabel(label)
        BasicTextField(
            value = value,
            onValueChange = onChange,
            singleLine = true,
            textStyle = BriefType.body.copy(color = c.text),
            cursorBrush = SolidColor(c.text),
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = label.lowercase() }
                .background(c.surface, RoundedCornerShape(14.dp))
                .border(1.dp, c.line, RoundedCornerShape(14.dp))
                .padding(horizontal = 14.dp, vertical = 12.dp),
        )
    }
}

private val placeholder = Regex("""\[[^\]]+]""")

/** Der Antwortbrief auf echtem Papier. Lücken wie [E-Mail] sind markiert. */
@Composable
private fun PaperLetter(text: String, writing: Boolean) {
    val cursorAlpha = if (writing && !Brief.reducedMotion) {
        val a by rememberInfiniteTransition(label = "cursor").animateFloat(1f, 0f, infiniteRepeatable(tween(500), RepeatMode.Reverse), label = "c")
        if (a > 0.5f) 1f else 0f
    } else 0f
    val annotated = buildAnnotatedString {
        var last = 0
        placeholder.findAll(text).forEach {
            append(text.substring(last, it.range.first))
            withStyle(SpanStyle(background = Color(0xFFF2DC9B), color = Color(0xFF3A2C05))) { append(it.value) }
            last = it.range.last + 1
        }
        append(text.substring(last))
        if (writing) withStyle(SpanStyle(color = Ink.copy(alpha = cursorAlpha))) { append("▍") }
    }
    Text(
        annotated,
        style = BriefType.paper,
        color = Ink,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(24.dp, RoundedCornerShape(6.dp))
            .background(Paper, RoundedCornerShape(6.dp))
            .padding(start = 22.dp, end = 22.dp, top = 24.dp, bottom = 28.dp),
    )
}
