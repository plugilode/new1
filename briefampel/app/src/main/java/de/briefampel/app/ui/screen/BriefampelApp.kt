package de.briefampel.app.ui.screen

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import de.briefampel.app.domain.LetterInput
import de.briefampel.app.ui.theme.Motion

@Composable
fun BriefampelApp(vm: BriefampelViewModel) {
    val state by vm.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var senderOpen by rememberSaveable { mutableStateOf(false) }

    val scanLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode != Activity.RESULT_OK) return@rememberLauncherForActivityResult
        val scan = GmsDocumentScanningResult.fromActivityResultIntent(result.data) ?: return@rememberLauncherForActivityResult
        vm.addPages(scan.pages.orEmpty().map { LetterInput.Page(it.imageUri.toString(), "image/jpeg") })
    }
    val fileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        vm.addPages(uris.map { LetterInput.Page(it.toString(), context.contentResolver.getType(it) ?: "application/octet-stream") })
    }

    val startScan = {
        val options = GmsDocumentScannerOptions.Builder()
            .setGalleryImportAllowed(true)
            .setPageLimit(LetterInput.MAX_IMAGES)
            .setResultFormats(GmsDocumentScannerOptions.RESULT_FORMAT_JPEG)
            .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL)
            .build()
        GmsDocumentScanning.getClient(options).getStartScanIntent(context as Activity)
            .addOnSuccessListener { scanLauncher.launch(IntentSenderRequest.Builder(it).build()) }
            // Ohne Google-Play-Dienste: auf Dateiauswahl ausweichen.
            .addOnFailureListener { fileLauncher.launch(arrayOf("image/*", "application/pdf")) }
        Unit
    }

    BackHandler(enabled = state.screen != Screen.HOME || state.phase != Phase.IDLE) { vm.back() }

    AnimatedContent(
        targetState = state.screen,
        transitionSpec = {
            val forward = targetState.ordinal > initialState.ordinal
            (slideInHorizontally(tween(420, easing = Motion.Settle)) { if (forward) it / 4 else -it / 4 } + fadeIn(tween(300))) togetherWith
                (slideOutHorizontally(tween(420, easing = Motion.Settle)) { if (forward) -it / 6 else it / 6 } + fadeOut(tween(200)))
        },
        label = "screen",
    ) { screen ->
        when (screen) {
            Screen.HOME -> HomeScreen(
                state = state,
                onScan = startScan,
                onPickFile = { fileLauncher.launch(arrayOf("application/pdf", "image/*")) },
                onPaste = { vm.showPaste(true) },
                onCheck = vm::check,
                onDiscard = vm::discard,
                onOpenResult = { vm.open(Screen.RESULT) },
                onOpenSender = { senderOpen = true },
            )
            Screen.RESULT -> ResultScreen(
                state = state,
                onBack = { vm.back() },
                onReply = { vm.open(Screen.REPLY) },
                onToggleEnvelope = vm::toggleEnvelope,
            )
            Screen.REPLY -> ReplyScreen(
                state = state,
                onBack = { vm.back() },
                onToggleSuggestion = vm::toggleSuggestion,
                onKeywords = vm::setKeywords,
                onTone = vm::setTone,
                onSaveSender = vm::saveSender,
                onWrite = vm::write,
                onStop = vm::stopWriting,
            )
        }
    }

    if (state.showConsent) ConsentSheet(onAccept = vm::acceptConsent, onDismiss = vm::dismissConsent)
    if (state.showPaste) PasteSheet(onDone = vm::setPastedText, onDismiss = { vm.showPaste(false) })
    if (senderOpen) SenderSheet(state.sender, vm::saveSender) { senderOpen = false }
}
