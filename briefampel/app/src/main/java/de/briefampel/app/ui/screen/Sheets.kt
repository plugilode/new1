package de.briefampel.app.ui.screen

import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import de.briefampel.app.domain.Fact
import de.briefampel.app.domain.LetterInput
import de.briefampel.app.domain.Sender
import de.briefampel.app.ui.components.MonoLabel
import de.briefampel.app.ui.components.OutlineButton
import de.briefampel.app.ui.components.PrimaryButton
import de.briefampel.app.ui.components.TextLink
import de.briefampel.app.ui.components.BriefIcons
import de.briefampel.app.ui.theme.Brief
import de.briefampel.app.ui.theme.BriefType

/** Datenschutzerklärung – vor dem Release durch die echte, öffentliche Seite ersetzen. */
const val PRIVACY_URL = "https://example.com/briefampel/datenschutz"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BriefSheet(onDismiss: () -> Unit, content: @Composable () -> Unit) {
    val c = Brief.colors
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = c.surface,
        scrimColor = Color(0xFF08090B).copy(alpha = 0.55f),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        dragHandle = {
            Box(Modifier.padding(top = 12.dp, bottom = 8.dp).size(40.dp, 5.dp).background(c.line, RoundedCornerShape(3.dp)))
        },
    ) {
        Column(
            Modifier.navigationBarsPadding().padding(start = 22.dp, end = 22.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) { content() }
    }
}

/** Einmal vor dem ersten Upload: wohin der Brief geht und was die App nicht ist. */
@Composable
fun ConsentSheet(onAccept: () -> Unit, onDismiss: () -> Unit) {
    val c = Brief.colors
    val context = LocalContext.current
    BriefSheet(onDismiss) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            MonoLabel("EINMAL, BEVOR ICH LESE")
            Text("Dein Brief bleibt\ndein Brief.", style = BriefType.heading.copy(fontSize = BriefType.heading.fontSize * 0.83f), color = c.text, modifier = Modifier.semantics { heading() })
        }
        SheetFacts(
            listOf(
                Fact("GEHT AN", "Google Gemini, verarbeitet in der EU."),
                Fact("BLEIBT", "Nirgends. Nach der Antwort ist er vom Server weg."),
                Fact("KOSTET", "Nichts. Drei Briefe am Tag sind frei."),
                Fact("IST NICHT", "Rechtsberatung. Ich sortiere und entwerfe, Fristen prüfst du selbst."),
            ),
        )
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            PrimaryButton("Verstanden, los", onAccept)
            TextLink("Datenschutz in Ruhe lesen", {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_URL)))
            }, Modifier.fillMaxWidth(), color = c.textSoft)
        }
    }
}

@Composable
private fun SheetFacts(facts: List<Fact>) {
    val c = Brief.colors
    Column {
        facts.forEachIndexed { i, f ->
            HorizontalDivider(color = c.surfaceLine)
            Row(Modifier.fillMaxWidth().padding(vertical = 14.dp).semantics(mergeDescendants = true) {}, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MonoLabel(f.label, Modifier.width(92.dp).padding(top = 3.dp))
                Text(f.text, style = BriefType.body.copy(fontSize = BriefType.body.fontSize * 0.97f), color = c.text)
            }
            if (i == facts.lastIndex) HorizontalDivider(color = c.surfaceLine)
        }
    }
}

@Composable
fun PasteSheet(onDone: (String) -> Unit, onDismiss: () -> Unit) {
    val c = Brief.colors
    val context = LocalContext.current
    var text by rememberSaveable { mutableStateOf("") }
    BriefSheet(onDismiss) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            MonoLabel("TEXT EINFÜGEN")
            Text("Kopierten Brief\nhier rein.", style = BriefType.heading.copy(fontSize = BriefType.heading.fontSize * 0.83f), color = c.text, modifier = Modifier.semantics { heading() })
        }
        OutlineButton("Aus Zwischenablage einfügen", {
            val clip = context.getSystemService(ClipboardManager::class.java)?.primaryClip
            val pasted = clip?.takeIf { it.itemCount > 0 }?.getItemAt(0)?.coerceToText(context)?.toString()
            if (!pasted.isNullOrBlank()) text = pasted.take(LetterInput.MAX_TEXT_CHARS)
        }, Modifier.fillMaxWidth(), icon = BriefIcons.Paste)
        BasicTextField(
            value = text,
            onValueChange = { text = it.take(LetterInput.MAX_TEXT_CHARS) },
            textStyle = BriefType.body.copy(color = c.text),
            cursorBrush = SolidColor(c.text),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 160.dp, max = 280.dp)
                .verticalScroll(rememberScrollState())
                .semantics { contentDescription = "Brieftext" }
                .background(c.background, RoundedCornerShape(18.dp))
                .border(1.dp, c.line, RoundedCornerShape(18.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp),
        )
        PrimaryButton("Übernehmen", { onDone(text) }, enabled = text.isNotBlank())
    }
}

@Composable
fun SenderSheet(sender: Sender, onSave: (Sender) -> Unit, onDismiss: () -> Unit) {
    val c = Brief.colors
    BriefSheet(onDismiss) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            MonoLabel("ABSENDER · NUR AUF DIESEM HANDY")
            Text("Für deine Antworten.", style = BriefType.heading.copy(fontSize = BriefType.heading.fontSize * 0.83f), color = c.text, modifier = Modifier.semantics { heading() })
        }
        SenderFields(sender, onSave)
        PrimaryButton("Fertig", onDismiss)
    }
}
