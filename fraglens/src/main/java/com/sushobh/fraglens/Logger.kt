package com.sushobh.fraglens

import android.util.Log

object FLLogger {

    fun log(message: String) {
        Log.i("FragLensDebubLogs",message)
    }

    fun error(message: String) {
        Log.e("FragLensDebubLogs",message)
    }

}