package com.sushobh.fraglens

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

internal object activityLifecycleCallback : ActivityLifecycleCallbacks {

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
        addFragmentCallback(p0)
    }


    override fun onActivityStarted(p0: Activity) {

    }

    override fun onActivityResumed(p0: Activity) {
        FragLens.onResumedActivity(p0 as ComponentActivity)
    }

    override fun onActivityPaused(p0: Activity) {

    }

    override fun onActivityStopped(p0: Activity) {
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {

    }

    override fun onActivityDestroyed(p0: Activity) {
        removeFragmentCallback(p0)
        FragLens.onDestroyActivity(p0 as ComponentActivity)
    }

    private fun addFragmentCallback(p0: Activity) {
        if (p0 is AppCompatActivity) {
            p0.supportFragmentManager.registerFragmentLifecycleCallbacks(
                FragmentLifeCycleCallback,
                true
            )
        }
    }

    private fun removeFragmentCallback(p0: Activity) {
        if (p0 is AppCompatActivity) {
            p0.supportFragmentManager.unregisterFragmentLifecycleCallbacks(FragmentLifeCycleCallback)
        }
    }
}


internal object FragmentLifeCycleCallback : FragmentManager.FragmentLifecycleCallbacks() {

    override fun onFragmentAttached(fm: FragmentManager, f: Fragment, context: Context) {
        super.onFragmentAttached(fm, f, context)

    }

    override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
        super.onFragmentCreated(fm, f, savedInstanceState)
    }

    override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
        super.onFragmentResumed(fm, f)
        FragLens.onResumedFragment(f)
    }

    override fun onFragmentPaused(fm: FragmentManager, f: Fragment) {
        super.onFragmentPaused(fm, f)
    }

    override fun onFragmentStopped(fm: FragmentManager, f: Fragment) {
        super.onFragmentStopped(fm, f)

    }

    override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
        super.onFragmentDestroyed(fm, f)
        FragLens.onDestroyFragment(f)
    }

    override fun onFragmentDetached(fm: FragmentManager, f: Fragment) {
        super.onFragmentDetached(fm, f)
    }
}