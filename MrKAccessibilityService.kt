package com.mrk.assistant

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.graphics.Path
import android.os.Build

class MrKAccessibilityService : AccessibilityService() {
    override fun onServiceConnected() { instance = this }
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}
    override fun onDestroy() { if (instance === this) instance = null; super.onDestroy() }

    fun scrollDown() = scroll(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
    fun scrollUp() = scroll(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD)

    private fun scroll(action: Int) {
        val root = rootInActiveWindow ?: return
        fun visit(node: AccessibilityNodeInfo): Boolean {
            if (node.isScrollable && node.performAction(action)) return true
            for (i in 0 until node.childCount) {
                val child = node.getChild(i) ?: continue
                if (visit(child)) return true
            }
            return false
        }
        visit(root)
    }

    companion object { @Volatile var instance: MrKAccessibilityService? = null }
}
