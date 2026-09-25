package com.sushobh.fraglens.methodtimer

import java.util.PriorityQueue

internal data class MethodTotalTime(
    val methodName: String,
    val totalTime: Double
)

internal class TotalTimeInsight :
    MethodTimerInsight<List<MethodTotalTime>> {

    companion object {
        private const val MAX_METHODS = 100
    }


    private val totals = HashMap<String, Double>()


    private val topMethods =
        PriorityQueue<MethodTotalTime> { a, b ->
            a.totalTime.compareTo(b.totalTime)
        }

    override fun addItem(methodEvent: MethodEvent) {

        val methodName = methodEvent.methodName

        val newTotal =
            (totals[methodName] ?: 0.0) + methodEvent.timeTaken

        totals[methodName] = newTotal

        // If this method is already in the top 100,
        // its heap entry needs to be updated.
        val existing = topMethods
            .firstOrNull { it.methodName == methodName }

        if (existing != null) {
            topMethods.remove(existing)
            topMethods.add(
                MethodTotalTime(
                    methodName = methodName,
                    totalTime = newTotal
                )
            )
            return
        }

        // Method isn't currently in the top 100.
        if (topMethods.size < MAX_METHODS) {
            topMethods.add(
                MethodTotalTime(
                    methodName = methodName,
                    totalTime = newTotal
                )
            )
        } else if (newTotal > topMethods.peek().totalTime) {

            topMethods.poll()

            topMethods.add(
                MethodTotalTime(
                    methodName = methodName,
                    totalTime = newTotal
                )
            )
        }
    }

    override fun getDescription(): String =
        "Top ${TotalTimeInsight.MAX_METHODS} methods by total execution time"

    override fun getInformation(): List<MethodTotalTime> =
        topMethods
            .sortedByDescending { it.totalTime }
}