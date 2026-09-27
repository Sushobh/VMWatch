package com.sushobh.fraglens.methodtimer

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigInteger

fun onMethodEnded9898(methodName : String, timeTaken : Double) {
    MethodTimer.methodCalled(methodName,timeTaken)
}




internal object MethodTimer {
    private var count = BigInteger("0")
    private val queue : ArrayDeque<MethodEvent> = ArrayDeque()
    val top100Slowest = Top100Slowest()
    val top100MostFrequent = MostCalledInsight()
    val totalTime = TotalTimeInsight()
    val liveEvents = LiveEventsInsight()
    private val scope = CoroutineScope(Dispatchers.Default)
    private var taskStarted = false
    private val allInsights = listOf(top100Slowest,top100MostFrequent,totalTime,liveEvents)
    fun startProcessing(){
        taskStarted = true
        try {
            scope.launch {
                while(true){
                    val item = queue.removeFirstOrNull()
                    if(item != null){
                        allInsights.forEach {
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

        }
    }

    fun methodCalled(methodName : String,time : Double){
        if(!taskStarted){
            startProcessing()
        }
        count = count.plus(BigInteger("1"))
        queue.addLast(MethodEvent(methodName,time,count))
    }

    fun clearData() {
        try {
            scope.launch {
               allInsights.forEach { it.resetData() }
            }
        }
        catch (e : Exception){

        }
    }


}