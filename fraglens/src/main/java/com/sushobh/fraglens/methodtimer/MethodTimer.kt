package com.sushobh.fraglens.methodtimer

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun onMethodEnded9898(methodName : String, timeTaken : Double) {
    Log.i("MethodTimer","Received ${methodName.toString()}")
    MethodTimer.methodCalled(MethodEvent(methodName,timeTaken))
}


internal class MethodEvent(val methodName : String,val timeTaken: Double)

internal object MethodTimer {

    private val queue : ArrayDeque<MethodEvent> = ArrayDeque()
    val top100Slowest = Top100Slowest()
    val top100MostFrequent = MostCalledInsight()
    val totalTime = TotalTimeInsight()
    val liveEvents = LiveEventCalledInsight()
    private val scope = CoroutineScope(Dispatchers.Default)
    private var taskStarted = false

    fun startProcessing(){
        taskStarted = true
        try {
            scope.launch {
                while(true){
                    val item = queue.removeFirstOrNull()
                    if(item != null){
                        listOf(top100Slowest,top100MostFrequent,totalTime,liveEvents).forEach {
                            it.addItem(item)
                        }
                    }
                    else {
                        delay(2000)
                    }
                }
            }
        }
        catch (e : Exception){
            e.printStackTrace()
        }
    }

    fun methodCalled(methodEvent: MethodEvent){
        if(!taskStarted){
            startProcessing()
        }
        queue.addLast(methodEvent)
    }



}