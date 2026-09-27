package com.sushobh.vmwatch.methodtimer

import com.sushobh.libs.com.sushobh.libs.sbstate.StateStore
import com.sushobh.libs.com.sushobh.libs.sbstate.Store
import com.sushobh.vmwatch.common.MethodCallCount
import com.sushobh.vmwatch.common.MethodEvent
import com.sushobh.vmwatch.common.MethodEventGroup
import com.sushobh.vmwatch.common.MethodTotalTime
import com.sushobh.vmwatch.config.ConfigApi
import com.sushobh.vmwatch.methodtimer.LiveEventState.*
import com.sushobh.vmwatch.methodtimer.TopFreqState.*
import com.sushobh.vmwatch.methodtimer.TopSlowestState.*
import com.sushobh.vmwatch.methodtimer.TopTotalTimeState.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow

class MethodTimerViewModel(private val configApi: ConfigApi) : Store<MethodTimerState, MethodTimerStateEvent> {

    private val viewModelScope = CoroutineScope(Dispatchers.IO)
    override val state: StateFlow<MethodTimerState>
        get() = stateStore.state

    private val stateStore = StateStore<MethodTimerState, MethodTimerStateEvent>(
        initialState = MethodTimerState(),
        reducerFn = ::reducer,
        middlewares = listOf({ state, event, dispatch ->
            when (event) {

                MethodTimerStateEvent.OnViewDestroyed -> {
                    viewModelScope.cancel()
                }

                MethodTimerStateEvent.OnViewLoaded -> {
                    viewModelScope.launch {
                        tickingFlow(1000).collect {
                            launch {
                                updateLiveEvents()
                            }
                            launch {
                                loadSelectedInsight()
                            }
                        }
                    }
                }

                MethodTimerStateEvent.OnResetRequest -> {
                    resetData()
                }

                else -> {

                }
            }
        })
    )

    private fun resetData() {
           viewModelScope.launch {
               val clear: Any? = try {
                   configApi.httpClient.get("${configApi.getApiHost()}/mtClear").body()
               } catch (e: Exception) {
                   null
               }
           }
    }

    private suspend fun updateLiveEvents() {
        val liveEvents: List<MethodEventGroup>? = try {
            configApi.httpClient.get("${configApi.getApiHost()}/mtLiveEvents").body()
        } catch (e: Exception) {
            null
        }
        dispatch(MethodTimerStateEvent.OnLiveEventsFetched(liveEvents ?: emptyList()))
    }

    private suspend fun loadSelectedInsight() {
        when (state.value.currentlySelectedInsight) {
            InsightType.TopFreq -> {
                val topFreqEvents: List<MethodCallCount>? = try {
                    configApi.httpClient.get("${configApi.getApiHost()}/mtFreq").body()
                } catch (e: Exception) {
                    null
                }
                dispatch(MethodTimerStateEvent.OnTopFreqEventsFetched(topFreqEvents ?: emptyList()))

            }

            InsightType.TopSlowest -> {
                val slowEvents: List<MethodEvent>? = try {
                    configApi.httpClient.get("${configApi.getApiHost()}/mtSlow").body()
                } catch (e: Exception) {
                    null
                }
                dispatch(MethodTimerStateEvent.OnSlowEventsFetched(slowEvents ?: emptyList()))
            }

            InsightType.TopTotalTime -> {
                val totalTimeEvents: List<MethodTotalTime>? = try {
                    configApi.httpClient.get("${configApi.getApiHost()}/mtTotalTime").body()
                } catch (e: Exception) {
                    null
                }
                dispatch(MethodTimerStateEvent.OnTopTotalTimeEventsFetched(totalTimeEvents ?: emptyList()))
            }
        }
    }

    private fun tickingFlow(time: Long) = flow<Unit> {
        while (true) {
            if (!currentCoroutineContext().isActive) {
                break
            }
            delay(time)
            emit(Unit)
        }
    }

    override fun dispatch(event: MethodTimerStateEvent) {
        stateStore.dispatch(event)
    }


    override fun reducer(
        state: MethodTimerState,
        event: MethodTimerStateEvent
    ): MethodTimerState {
        return when(event){
            is MethodTimerStateEvent.InsighTypeSelected -> {
                 state.copy(currentlySelectedInsight = event.type)
            }
            is MethodTimerStateEvent.OnLiveEventsFetched -> {
                 state.copy(liveEventState = LiveEventState.Items(event.items.filter {
                     filterGeneratedMethods(it.methodName)
                 }))
            }
            is MethodTimerStateEvent.OnSlowEventsFetched ->
            {
                 state.copy(topSlowestState = TopSlowestState.Items(event.items.filter {
                     filterGeneratedMethods(it.methodName)
                 }))
            }
            is MethodTimerStateEvent.OnTopFreqEventsFetched -> {

                 state.copy(topFreqState = TopFreqState.Items(event.items.filter {
                     filterGeneratedMethods(it.methodName)
                 }))
            }
            is MethodTimerStateEvent.OnTopTotalTimeEventsFetched -> {
                 state.copy(totalTimeState = TopTotalTimeState.Items(event.items.filter {
                     filterGeneratedMethods(it.methodName)
                 }))
            }

            else -> state
        }
    }

    private fun filterGeneratedMethods(name : String) : Boolean {
        return !name.contains("$")
    }

}