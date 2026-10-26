package de.briefampel.app.ui.screen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.briefampel.app.data.LocalStore
import de.briefampel.app.data.SampleProvider
import de.briefampel.app.domain.AnalysisError
import de.briefampel.app.domain.AnalysisException
import de.briefampel.app.domain.Keywords
import de.briefampel.app.domain.LetterAnalysis
import de.briefampel.app.domain.LetterInput
import de.briefampel.app.domain.LlmProvider
import de.briefampel.app.domain.ReplyRequest
import de.briefampel.app.domain.Sender
import de.briefampel.app.domain.Tone
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

enum class Screen { HOME, RESULT, REPLY }

enum class Phase { IDLE, PAGES, CHECKING, RESULT }

data class ReplyState(
    val keywords: String = "",
    val tone: Tone = Tone.SACHLICH,
    val text: String = "",
    val writing: Boolean = false,
)

data class UiState(
    val screen: Screen = Screen.HOME,
    val phase: Phase = Phase.IDLE,
    val input: LetterInput = LetterInput(emptyList(), null),
    val receivedAt: LocalDateTime? = null,
    val analysis: LetterAnalysis? = null,
    val checkedAt: LocalDateTime? = null,
    val error: AnalysisError? = null,
    val showConsent: Boolean = false,
    val showPaste: Boolean = false,
    val envelopeOpen: Boolean = false,
    val sender: Sender = Sender(),
    val reply: ReplyState = ReplyState(),
)

class BriefampelViewModel(
    app: Application,
    private val provider: LlmProvider = SampleProvider(),
    private val store: LocalStore = LocalStore(app),
) : AndroidViewModel(app) {

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    private var checkJob: Job? = null
    private var replyJob: Job? = null

    init {
        viewModelScope.launch { store.sender.collect { s -> _state.update { it.copy(sender = s) } } }
    }

    // ---- Eingang ----

    fun addPages(pages: List<LetterInput.Page>) {
        if (pages.isEmpty()) return
        _state.update {
            val merged = (it.input.pages + pages).take(LetterInput.MAX_IMAGES)
            it.copy(
                input = it.input.copy(pages = merged),
                phase = Phase.PAGES,
                receivedAt = LocalDateTime.now(),
                error = if (it.input.pages.size + pages.size > LetterInput.MAX_IMAGES) AnalysisError.TOO_LARGE else null,
            )
        }
    }

    fun setPastedText(text: String) {
        val trimmed = text.trim().take(LetterInput.MAX_TEXT_CHARS)
        if (trimmed.isEmpty()) return
        _state.update {
            it.copy(input = it.input.copy(pastedText = trimmed), phase = Phase.PAGES, receivedAt = LocalDateTime.now(), showPaste = false, error = null)
        }
    }

    fun showPaste(show: Boolean) = _state.update { it.copy(showPaste = show) }

    fun discard() {
        checkJob?.cancel()
        replyJob?.cancel()
        _state.update { UiState(sender = it.sender) }
    }

    // ---- Prüfen ----

    fun check() {
        viewModelScope.launch {
            if (!store.consent.first()) {
                _state.update { it.copy(showConsent = true) }
                return@launch
            }
            runCheck()
        }
    }

    fun acceptConsent() {
        viewModelScope.launch {
            store.giveConsent()
            _state.update { it.copy(showConsent = false) }
            runCheck()
        }
    }

    fun dismissConsent() = _state.update { it.copy(showConsent = false) }

    private fun runCheck() {
        val input = _state.value.input
        if (input.isEmpty) return
        checkJob?.cancel()
        _state.update { it.copy(phase = Phase.CHECKING, error = null) }
        checkJob = viewModelScope.launch {
            try {
                val analysis = provider.analyze(input)
                _state.update {
                    it.copy(
                        phase = Phase.RESULT,
                        analysis = analysis,
                        checkedAt = LocalDateTime.now(),
                        envelopeOpen = false,
                        reply = ReplyState(keywords = analysis.suggestions.take(2).joinToString("\n")),
                    )
                }
            } catch (e: AnalysisException) {
                _state.update { it.copy(phase = Phase.PAGES, error = e.error) }
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update { it.copy(phase = Phase.PAGES, error = AnalysisError.UNKNOWN) }
            }
        }
    }

    fun dismissError() = _state.update { it.copy(error = null) }

    // ---- Navigation ----

    fun open(screen: Screen) = _state.update { it.copy(screen = screen) }

    fun back(): Boolean {
        val s = _state.value
        return when (s.screen) {
            Screen.REPLY -> { stopWriting(); open(Screen.RESULT); true }
            Screen.RESULT -> { open(Screen.HOME); true }
            Screen.HOME -> if (s.phase != Phase.IDLE) { discard(); true } else false
        }
    }

    fun toggleEnvelope() = _state.update { it.copy(envelopeOpen = !it.envelopeOpen) }

    // ---- Antwort ----

    fun toggleSuggestion(chip: String) = updateReply { it.copy(keywords = Keywords.toggle(it.keywords, chip)) }

    fun setKeywords(text: String) = updateReply { it.copy(keywords = text) }

    fun setTone(tone: Tone) = updateReply { it.copy(tone = tone) }

    fun saveSender(sender: Sender) {
        _state.update { it.copy(sender = sender) }
        viewModelScope.launch { store.saveSender(sender) }
    }

    fun write() {
        val s = _state.value
        val analysis = s.analysis ?: return
        replyJob?.cancel()
        updateReply { it.copy(text = "", writing = true) }
        replyJob = viewModelScope.launch {
            try {
                provider.draftReply(ReplyRequest(analysis, s.reply.keywords, s.reply.tone, s.sender)).collect { chunk ->
                    updateReply { it.copy(text = it.text + chunk) }
                }
            } finally {
                updateReply { it.copy(writing = false) }
            }
        }
    }

    /** Stopp: Der bisher geschriebene Text bleibt stehen. */
    fun stopWriting() {
        replyJob?.cancel()
        updateReply { it.copy(writing = false) }
    }

    private fun updateReply(f: (ReplyState) -> ReplyState) = _state.update { it.copy(reply = f(it.reply)) }

    val today: LocalDate get() = de.briefampel.app.domain.Deadline.today()
}
