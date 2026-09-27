package com.sushobh.vmwatch.methodtimer

import com.sushobh.vmwatch.common.MethodCallCount
import com.sushobh.vmwatch.common.MethodEvent
import com.sushobh.vmwatch.common.MethodEventGroup
import com.sushobh.vmwatch.common.MethodTotalTime

data class MethodTimerState(
    val liveEventState: LiveEventState = LiveEventState.Waiting,
    val topFreqState: TopFreqState = TopFreqState.Waiting,
    val topSlowestState: TopSlowestState = TopSlowestState.Waiting,
    val totalTimeState: TopTotalTimeState = TopTotalTimeState.Waiting,
    val currentlySelectedInsight: InsightType = InsightType.TopFreq
) {
}

sealed class InsightType(val title: String) {
    data object TopSlowest : InsightType("Top Slowest methods")
    data object TopFreq : InsightType("Top Frequency methods")
    data object TopTotalTime : InsightType("Top total time methods")
}

sealed class LiveEventState {
    data class Items(val methodEventGroups: List<MethodEventGroup> = emptyList()) : LiveEventState()
    data class Error(val message: String) : LiveEventState()
    data object Loading : LiveEventState()
    data object Waiting : LiveEventState()
}


sealed class TopSlowestState {
    data class Error(val message: String) : TopSlowestState()
    data object Loading : TopSlowestState()
    data class Items(val list: List<MethodEvent>) : TopSlowestState()
    data object Waiting : TopSlowestState()
}

sealed class TopFreqState {
    data object Waiting : TopFreqState()
    data class Error(val message: String) : TopFreqState()
    data object Loading : TopFreqState()
    data class Items(val list: List<MethodCallCount>) : TopFreqState()
}

sealed class TopTotalTimeState {
    data object Waiting : TopTotalTimeState()
    data class Items(val list: List<MethodTotalTime>) : TopTotalTimeState()
    data class Error(val message: String) : TopTotalTimeState()
    data object Loading : TopTotalTimeState()
}

sealed class MethodTimerStateEvent {
    data class InsighTypeSelected(val type: InsightType) : MethodTimerStateEvent()
    data object OnViewLoaded : MethodTimerStateEvent()
    data object OnViewDestroyed : MethodTimerStateEvent()
    data object OnResetRequest : MethodTimerStateEvent()
    data class OnSlowEventsFetched(val items: List<MethodEvent>) : MethodTimerStateEvent()
    data class OnTopFreqEventsFetched(val items: List<MethodCallCount>) : MethodTimerStateEvent()
    data class OnTopTotalTimeEventsFetched(val items: List<MethodTotalTime>) : MethodTimerStateEvent()
    data class OnLiveEventsFetched(val items : List<MethodEventGroup>) : MethodTimerStateEvent()
}
