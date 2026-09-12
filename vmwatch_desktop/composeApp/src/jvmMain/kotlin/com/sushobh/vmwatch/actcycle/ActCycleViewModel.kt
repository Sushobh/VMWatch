package com.sushobh.vmwatch.actcycle

import com.sushobh.libs.com.sushobh.libs.sbstate.StateStore
import com.sushobh.libs.com.sushobh.libs.sbstate.Store
import com.sushobh.vmwatch.ActLifecycleEvents
import com.sushobh.vmwatch.config.ConfigApi
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class ActCycleViewModel(private val configApi: ConfigApi) : Store<ActCycleState, ActcycleEvent> {

    private val viewModelScope = CoroutineScope(Dispatchers.IO)
    private var pollingJob : Job? = null

    private val store = StateStore(
        initialState = ActCycleState(),
        reducerFn = ::reducer,
        middlewares = listOf({_,event,dispatch ->
            when(event) {
                ActcycleEvent.StartPollingEvents -> {

                    if(pollingJob != null){
                        pollingJob?.cancel()
                    }
                    pollingJob = viewModelScope.launch {
                        while(true) {
                            val actLifecycleEvents : ActLifecycleEvents? = try {
                                configApi.httpClient.get("${configApi.getApiHost()}/getactstate").body()
                            }
                            catch (e : Exception){
                                null
                            }
                            dispatch(ActcycleEvent.OnItemsFetched(actLifecycleEvents))
                            delay(1000)
                        }
                    }
                }
                else -> {

                }
            }
        }),
        coroutineScope = viewModelScope
    )

    override val state: StateFlow<ActCycleState>
        get() = store.state

    override fun dispatch(event: ActcycleEvent) {
        store.dispatch(event)
    }

    override fun reducer(
        state: ActCycleState,
        event: ActcycleEvent
    ): ActCycleState {
        return when(event) {

            is ActcycleEvent.OnItemsFetched -> {
                if(event.actLifecycleEvents == null){
                    state.copy(isDisconnected = true,events = ActLifecycleEvents())
                }
                else {
                    state.copy(isDisconnected = false,events = event.actLifecycleEvents)
                }
            }
            else -> state
        }
    }

    private fun dummyEvents() = ActLifecycleEvents(
        onResumed = listOf("MainFragment","MainActivity","ProfileFragment"),
        onStopped = listOf("LoginFragment","LoginActivity"),
        onPaused = listOf("AuthLoadFragment","AuthLoadActivity"),
        onStarted =  listOf()
    )

}