package com.mrk.assistant

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(36, 60, 36, 36)
            setBackgroundColor(android.graphics.Color.rgb(8, 13, 24))
        }
        val title = TextView(this).apply {
            text = "MR. K"
            textSize = 32f
            setTextColor(android.graphics.Color.WHITE)
        }
        status = TextView(this).apply {
            text = "Your voice assistant is ready to set up."
            textSize = 16f
            setTextColor(android.graphics.Color.LTGRAY)
            setPadding(0, 24, 0, 24)
        }
        val start = Button(this).apply {
            text = "START MR. K"
            setOnClickListener { requestAndStart() }
        }
        val accessibility = Button(this).apply {
            text = "ENABLE PHONE CONTROL"
            setOnClickListener { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
        }
        val stop = Button(this).apply {
            text = "STOP ASSISTANT"
            setOnClickListener { stopService(Intent(this@MainActivity, AssistantService::class.java)); status.text = "Assistant stopped." }
        }
        layout.addView(title); layout.addView(status); layout.addView(start); layout.addView(accessibility); layout.addView(stop)
        setContentView(layout)
    }

    private fun requestAndStart() {
        val permissions = mutableListOf(Manifest.permission.RECORD_AUDIO)
        if (Build.VERSION.SDK_INT >= 33) permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        val missing = permissions.filter { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }
        if (missing.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, missing.toTypedArray(), 42)
            return
        }
        ContextCompat.startForegroundService(this, Intent(this, AssistantService::class.java))
        status.text = "MR. K is running. Use the notification to start voice commands."
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 42 && grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) requestAndStart()
    }
}
