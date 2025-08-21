package com.sushobh.fraglens_ui

import android.app.Application
import androidx.activity.ComponentActivity
import com.sushobh.fraglens.FLConfig
import com.sushobh.fraglens.FLCurrentActivityListener
import com.sushobh.fraglens.FragLens
import com.sushobh.fraglens_ui.screens.InAppOverlay

object FraglensUi {
    var overlay : InAppOverlay? = null

    fun initStart(app: Application, config: FLConfig) {
        FragLens.init(app, config)
        FragLens.setActivityLifecycleListener(object : FLCurrentActivityListener {
            override fun onResumed(activity: ComponentActivity) {
                overlay = InAppOverlay(activity)
                overlay?.showOverlay()
            }

            override fun onPaused(activity: ComponentActivity) {
                overlay?.removeOverlay()
            }

        })
    }

}