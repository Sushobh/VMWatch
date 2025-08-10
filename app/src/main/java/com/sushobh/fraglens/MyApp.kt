package com.sushobh.fraglens

import android.app.Application

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        FragLens.init(this)
    }
}