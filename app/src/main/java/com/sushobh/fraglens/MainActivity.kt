package com.sushobh.fraglens
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import com.sushobh.fraglens.main.R


class MainActivity : FragmentActivity() {

    val vm : TestViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        vm.toString()
        setContentView(R.layout.activity_main)
    }
}
