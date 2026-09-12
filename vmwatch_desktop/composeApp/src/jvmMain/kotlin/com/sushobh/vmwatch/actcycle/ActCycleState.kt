package com.sushobh.vmwatch.actcycle

import com.sushobh.vmwatch.ActLifecycleEvents


data class ActCycleState(val isDisconnected : Boolean = true,val events : ActLifecycleEvents = ActLifecycleEvents())


sealed class ActcycleEvent {
    data object StartPollingEvents : ActcycleEvent()
    data class OnItemsFetched(val actLifecycleEvents: ActLifecycleEvents?) : ActcycleEvent()
}