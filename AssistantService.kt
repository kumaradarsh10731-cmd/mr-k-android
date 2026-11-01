package com.mrk.assistant

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.core.app.NotificationCompat
import java.util.Locale

class AssistantService : Service() {
    private var recognizer: SpeechRecognizer? = null
    private val channelId = "mrk_assistant"

    override fun onCreate() {
        super.onCreate()
        createChannel()
        startForeground(7, notification("Ready. Tap Listen to give a command."))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_LISTEN) listenOnce()
        if (intent?.action == ACTION_STOP) stopSelf()
        return START_NOT_STICKY
    }

    private fun listenOnce() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            updateNotification("Speech recognition is unavailable on this phone.")
            return
        }
        recognizer?.destroy()
        recognizer = SpeechRecognizer.createSpeechRecognizer(this)
        recognizer?.setRecognitionListener(object : android.speech.RecognitionListener {
            override fun onReadyForSpeech(params: android.os.Bundle?) { updateNotification("Listening…") }
            override fun onResults(results: android.os.Bundle?) {
                val words = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull().orEmpty()
                updateNotification(if (words.isBlank()) "No command heard." else "Heard: $words")
                if (words.isNotBlank()) CommandExecutor(this@AssistantService).execute(words)
                recognizer?.destroy(); recognizer = null
            }
            override fun onError(error: Int) { updateNotification("Listening stopped. Tap Listen to try again.") }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onPartialResults(partialResults: android.os.Bundle?) {}
            override fun onEvent(eventType: Int, params: android.os.Bundle?) {}
        })
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en-IN")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
        }
        recognizer?.startListening(intent)
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(channelId, "MR. K Assistant", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    private fun notification(message: String): Notification {
        val listen = PendingIntent.getService(this, 1, Intent(this, AssistantService::class.java).setAction(ACTION_LISTEN), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val stop = PendingIntent.getService(this, 2, Intent(this, AssistantService::class.java).setAction(ACTION_STOP), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setContentTitle("MR. K")
            .setContentText(message)
            .setOngoing(true)
            .addAction(0, "LISTEN", listen)
            .addAction(0, "STOP", stop)
            .build()
    }

    private fun updateNotification(message: String) {
        getSystemService(NotificationManager::class.java).notify(7, notification(message))
    }

    override fun onDestroy() {
        recognizer?.destroy()
        super.onDestroy()
    }
    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val ACTION_LISTEN = "com.mrk.assistant.LISTEN"
        const val ACTION_STOP = "com.mrk.assistant.STOP"
    }
}
