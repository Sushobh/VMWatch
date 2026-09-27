package com.sushobh.vmwatch.common

import kotlinx.serialization.Serializable
import java.math.BigInteger


interface MethodTimerInsight<X> {
    fun addItem(methodEvent: MethodEvent)
    fun getDescription() : String
    fun getInformation() : X
}

@Serializable
data class MethodEventGroup(
    val methodName: String,
    val events: MutableList<MethodEvent> = mutableListOf(),
    val id : Long
) {
    val count: Int
        get() = events.size

    val totalTime: Double
        get() = events.sumOf { it.timeTaken }
}

@Serializable
class MethodEvent(val methodName : String,val timeTaken: Double,val id : Long)

@Serializable
data class MethodCallCount(
    val methodName: String,
    val callCount: Long
)

@Serializable
data class MethodTotalTime(
    val methodName: String,
    val totalTime: Double
)
