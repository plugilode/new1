package de.briefampel.app.ui

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import de.briefampel.app.R

/**
 * Der Klang, wenn die Ampel ihr Ergebnis zeigt. Bleibt still, wenn das Handy lautlos oder auf Vibration steht.
 */
object Sounds {
    private var pool: SoundPool? = null
    private var chime = 0
    private var loaded = false

    /** Früh laden, damit der Klang beim Ergebnis ohne Verzögerung kommt. */
    fun preload(context: Context) {
        if (pool != null) return
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        pool = SoundPool.Builder().setMaxStreams(1).setAudioAttributes(attributes).build().also { p ->
            p.setOnLoadCompleteListener { _, _, status -> loaded = status == 0 }
            chime = p.load(context.applicationContext, R.raw.reveal_chime, 1)
        }
    }

    fun reveal(context: Context) {
        val p = pool ?: return
        if (!loaded) return
        val audio = context.getSystemService(AudioManager::class.java) ?: return
        if (audio.ringerMode != AudioManager.RINGER_MODE_NORMAL) return
        p.play(chime, 0.8f, 0.8f, 1, 0, 1f)
    }

    fun release() {
        pool?.release()
        pool = null
        loaded = false
    }
}
