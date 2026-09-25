package com.sushobh.fraglens.methodtimer

internal interface MethodTimerInsight<X> {
    fun addItem(methodEvent: MethodEvent)
    fun getDescription() : String
    fun getInformation() : X
}