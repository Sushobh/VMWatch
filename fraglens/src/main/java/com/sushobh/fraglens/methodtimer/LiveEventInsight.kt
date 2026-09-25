package com.sushobh.fraglens.methodtimer

import java.util.PriorityQueue

internal data class LiveMethodCallCount(
    val methodName: String,
    val callCount: Long
)

internal class LiveEventCalledInsight :
    MethodTimerInsight<List<LiveMethodCallCount>> {

    companion object {
        private const val MAX_METHODS = 100
    }

    // Complete count for each method.
    private val callCounts = HashMap<String, Long>()

    // Only the current top 100 methods.
    // The least-called method is at the head.
    private val topMethods =
        PriorityQueue<LiveMethodCallCount> { a, b ->
            a.callCount.compareTo(b.callCount)
        }

    override fun addItem(methodEvent: MethodEvent) {

        val methodName = methodEvent.methodName

        val newCount =
            (callCounts[methodName] ?: 0L) + 1

        callCounts[methodName] = newCount

        // Method is already in the top 100.
        val existing = topMethods
            .firstOrNull { it.methodName == methodName }

        if (existing != null) {
            topMethods.remove(existing)

            topMethods.add(
                LiveMethodCallCount(
                    methodName = methodName,
                    callCount = newCount
                )
            )

            return
        }

        // We still have room.
        if (topMethods.size < MAX_METHODS) {

            topMethods.add(
                LiveMethodCallCount(
                    methodName = methodName,
                    callCount = newCount
                )
            )

        } else if (newCount > topMethods.peek().callCount) {

            // Remove the least-called method.
            topMethods.poll()

            topMethods.add(
                LiveMethodCallCount(
                    methodName = methodName,
                    callCount = newCount
                )
            )
        }
    }

    override fun getDescription(): String =
        "Top 100 most called methods"

    override fun getInformation(): List<LiveMethodCallCount> =
        topMethods.sortedByDescending { it.callCount }
}