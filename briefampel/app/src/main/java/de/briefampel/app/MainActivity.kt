package de.briefampel.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.content.IntentCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import de.briefampel.app.domain.LetterInput
import de.briefampel.app.ui.screen.BriefampelApp
import de.briefampel.app.ui.screen.BriefampelViewModel
import de.briefampel.app.ui.theme.BriefampelTheme

class MainActivity : ComponentActivity() {
    private val vm: BriefampelViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.auto(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) handleShare(intent)
        setContent {
            BriefampelTheme {
                BriefampelApp(vm)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleShare(intent)
    }

    /** Briefampel als Teilen-Ziel: PDF aus der Mail-App, Screenshot aus der Galerie, Text. */
    private fun handleShare(intent: Intent?) {
        intent ?: return
        val type = intent.type ?: return
        when (intent.action) {
            Intent.ACTION_SEND -> {
                if (type == "text/plain") {
                    intent.getStringExtra(Intent.EXTRA_TEXT)?.let(vm::setPastedText)
                } else {
                    IntentCompat.getParcelableExtra(intent, Intent.EXTRA_STREAM, Uri::class.java)?.let { vm.addPages(listOf(page(it, type))) }
                }
            }
            Intent.ACTION_SEND_MULTIPLE -> {
                IntentCompat.getParcelableArrayListExtra(intent, Intent.EXTRA_STREAM, Uri::class.java)
                    ?.let { uris -> vm.addPages(uris.map { page(it, type) }) }
            }
        }
    }

    private fun page(uri: Uri, fallbackType: String) = LetterInput.Page(uri.toString(), contentResolver.getType(uri) ?: fallbackType)
}
