package com.sushobh.fraglens

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels


class MainActivity : ComponentActivity() {

    val vm : TestViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        vm.toString()
        startActivity(Intent(this, com.sushobh.fraglens_ui.screens.FragLensActivity::class.java))
    }
}
