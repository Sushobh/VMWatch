package com.sushobh.fraglens

import android.app.Application
import com.sushobh.fraglens_ui.FraglensUi

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        FraglensUi.initStart(this, FLConfig(arrayListOf()))
    }


}