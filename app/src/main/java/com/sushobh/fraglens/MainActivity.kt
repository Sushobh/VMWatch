package com.sushobh.fraglens

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    val vm : TestViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        vm.toString()
        lifecycleScope.launch {
            FragLens.viewModelIdFlow.collect {
                Log.i("MyLog22", it.firstOrNull()?.let { flViewModelId -> FragLens.parseProperties(flViewModelId) }
                    .toString())
            }
        }
    }
}

