package com.mrk.assistant

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast

class CommandExecutor(private val context: Context) {
    fun execute(raw: String) {
        val command = raw.lowercase(LocaleHolder.locale).trim()
        when {
            command.startsWith("open ") || command.startsWith("launch ") || command.startsWith("खोलो ") -> {
                val name = command.removePrefix("open ").removePrefix("launch ").removePrefix("खोलो ").trim()
                launchApp(name)
            }
            command.contains("go home") || command == "home" -> {
                val i = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(i)
            }
            command.contains("settings") -> {
                context.startActivity(Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
            command.contains("back") -> {
                MrKAccessibilityService.instance?.performGlobalAction(android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK)
                    ?: say("Enable MR. K phone control in Accessibility settings first.")
            }
            command.contains("scroll down") -> MrKAccessibilityService.instance?.scrollDown()
                ?: say("Enable MR. K phone control in Accessibility settings first.")
            command.contains("scroll up") -> MrKAccessibilityService.instance?.scrollUp()
                ?: say("Enable MR. K phone control in Accessibility settings first.")
            else -> say("I heard: $raw. This command is not supported yet.")
        }
    }

    private fun launchApp(query: String) {
        val pm = context.packageManager
        val apps = pm.getInstalledApplications(0)
        val match = apps.firstOrNull {
            pm.getApplicationLabel(it).toString().lowercase().contains(query) ||
            it.packageName.substringAfterLast('.').replace("_", " ").contains(query)
        }
        if (match == null) { say("I couldn't find that app."); return }
        val launch = pm.getLaunchIntentForPackage(match.packageName)
        if (launch == null) { say("That app cannot be launched."); return }
        launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(launch)
    }

    private fun say(message: String) = Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}
private object LocaleHolder { val locale = java.util.Locale.ROOT }
