package de.briefampel.app.ui

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import androidx.compose.runtime.Composable
import com.android.resources.NightMode
import de.briefampel.app.data.SampleProvider
import de.briefampel.app.domain.Deadline
import de.briefampel.app.domain.LetterInput
import de.briefampel.app.domain.Sender
import de.briefampel.app.ui.screen.HomeScreen
import de.briefampel.app.ui.screen.Phase
import de.briefampel.app.ui.screen.ReplyState
import de.briefampel.app.ui.screen.ReplyScreen
import de.briefampel.app.ui.screen.ResultScreen
import de.briefampel.app.ui.screen.Screen
import de.briefampel.app.ui.screen.UiState
import de.briefampel.app.ui.theme.BriefampelTheme
import org.junit.Rule
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime

/** Rendert die Bildschirme als Screenshots (./gradlew recordPaparazziDebug), zur Design-Abnahme. */
class ScreensSnapshotTest {
    @get:Rule
    val paparazzi = Paparazzi(deviceConfig = DeviceConfig.PIXEL_5, maxPercentDifference = 0.1)

    // Fester Zeitpunkt: Mi 28.10.2026, 09:12 in Berlin – sonst ändern sich die Bilder jeden Tag.
    private val clock = Clock.fixed(Instant.parse("2026-10-28T08:12:00Z"), Deadline.zone)
    private val today = Deadline.today(clock)
    private val now = LocalDateTime.now(clock)
    private val threePages = LetterInput(List(3) { LetterInput.Page("content://p$it", "image/jpeg") }, null)
    private val base = UiState(input = threePages, receivedAt = now, checkedAt = now)

    private fun shot(dark: Boolean = false, content: @Composable () -> Unit) {
        paparazzi.unsafeUpdateConfig(deviceConfig = DeviceConfig.PIXEL_5.copy(nightMode = if (dark) NightMode.NIGHT else NightMode.NOTNIGHT))
        paparazzi.snapshot { BriefampelTheme(dark = dark, reducedMotion = true, clock = clock, content = content) }
    }

    @Composable
    private fun home(state: UiState) = HomeScreen(state, {}, {}, {}, {}, {}, {}, {})

    @Test fun home_idle() = shot { home(UiState()) }

    @Test fun home_pages() = shot { home(base.copy(phase = Phase.PAGES)) }

    @Test fun home_checking() = shot { home(base.copy(phase = Phase.CHECKING)) }

    @Test fun home_result_red() = shot { home(base.copy(phase = Phase.RESULT, analysis = SampleProvider.red(today))) }

    @Test fun home_result_green() = shot { home(base.copy(phase = Phase.RESULT, analysis = SampleProvider.green(today))) }

    @Test fun result_red() = shot { ResultScreen(base.copy(screen = Screen.RESULT, phase = Phase.RESULT, analysis = SampleProvider.red(today)), {}, {}, {}) }

    @Test fun home_result_red_dark() = shot(dark = true) { home(base.copy(phase = Phase.RESULT, analysis = SampleProvider.red(today))) }

    @Test fun result_red_dark() = shot(dark = true) { ResultScreen(base.copy(screen = Screen.RESULT, phase = Phase.RESULT, analysis = SampleProvider.red(today)), {}, {}, {}) }

    @Test fun result_yellow() = shot {
        ResultScreen(base.copy(screen = Screen.RESULT, phase = Phase.RESULT, analysis = SampleProvider.yellow(today)), {}, {}, {})
    }

    @Test fun result_green() = shot { ResultScreen(base.copy(screen = Screen.RESULT, phase = Phase.RESULT, analysis = SampleProvider.green(today)), {}, {}, {}) }

    @Test fun reply_writing() = shot {
        val red = SampleProvider.red(today)
        ReplyScreen(
            base.copy(
                screen = Screen.REPLY,
                phase = Phase.RESULT,
                analysis = red,
                sender = Sender("Jana Becker", "Lindenstraße 14", "50674 Köln"),
                reply = ReplyState(
                    keywords = "Forderung bestreiten\nVertrag und Rechnung anfordern\nnie Kundin bei Mobilfunk Süd gewesen",
                    text = "Jana Becker · Lindenstraße 14 · 50674 Köln · [E-Mail]\n\nInkasso Nordwest GmbH\n\nKöln, 25.10.2026\n\nIhr Zeichen INK-55821/26\n\nSehr geehrte Damen und Herren,\n\ndie von Ihnen geltend gemachte Forderung über 312,40 € bestreite ich hiermit ausdrücklich. Bis dahin werde ich keine Zahlung leis",
                    writing = true,
                ),
            ),
            {}, {}, {}, {}, {}, {}, {},
        )
    }
}
