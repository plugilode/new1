package de.briefampel.app.ui

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import de.briefampel.app.domain.Ampel

/** Jede Farbe fühlt sich anders an: grün weich, gelb doppelt, rot fester. */
object Haptics {
    fun reveal(context: Context, ampel: Ampel) {
        val vibrator = vibrator(context) ?: return
        if (!vibrator.hasVibrator()) return
        val effect = when (ampel) {
            Ampel.GRUEN -> VibrationEffect.createOneShot(30, 70)
            Ampel.GELB -> VibrationEffect.createWaveform(longArrayOf(0, 35, 90, 35), intArrayOf(0, 140, 0, 140), -1)
            Ampel.ROT -> VibrationEffect.createWaveform(longArrayOf(0, 70, 60, 120), intArrayOf(0, 255, 0, 200), -1)
        }
        vibrator.vibrate(effect)
    }

    private fun vibrator(context: Context): Vibrator? = if (Build.VERSION.SDK_INT >= 31) {
        context.getSystemService(VibratorManager::class.java)?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Vibrator::class.java)
    }
}
