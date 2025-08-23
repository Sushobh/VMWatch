package com.sushobh.fraglens_ui

import android.app.Application
import androidx.activity.ComponentActivity
import com.sushobh.fraglens.FLConfig
import com.sushobh.fraglens.FLCurrentActivityListener
import com.sushobh.fraglens.FragLens
import com.sushobh.fraglens_ui.screens.InAppOverlay

object FraglensUi {
    val overlay : InAppOverlay = InAppOverlay()

    fun initStart(app: Application, config: FLConfig) {
        FragLens.init(app, config)
        FragLens.setActivityLifecycleListener(object : FLCurrentActivityListener {
            override fun onResumed(activity: ComponentActivity) {
                overlay.showOverlay(activity)
            }

            override fun onPaused(activity: ComponentActivity) {
                overlay.removeOverlay(activity)
            }

        })
    }

}