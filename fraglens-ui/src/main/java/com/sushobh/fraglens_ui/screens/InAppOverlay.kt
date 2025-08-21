package com.sushobh.fraglens_ui.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import androidx.core.content.ContextCompat.startActivity
import com.sushobh.fraglens.ui.R

class InAppOverlay(private val activity: Activity) {

    private var windowManager: WindowManager? = null
    private var overlayView: View? = null

    fun showOverlay() {
        if (overlayView != null) return // already showing

        windowManager = activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        overlayView = LayoutInflater.from(activity).inflate(R.layout.overlay_layout, null)

        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_PANEL, // stays inside your app
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        layoutParams.gravity = Gravity.TOP
        layoutParams.token = activity.window.decorView.windowToken // tie to app window

        windowManager?.addView(overlayView, layoutParams)
    }

    fun removeOverlay() {
        if (overlayView != null) {
            windowManager?.removeView(overlayView)
            overlayView = null
        }
    }
}
