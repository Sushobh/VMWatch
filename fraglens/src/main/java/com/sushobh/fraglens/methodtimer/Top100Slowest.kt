package com.sushobh.fraglens.methodtimer

import java.util.PriorityQueue

internal class Top100Slowest : MethodTimerInsight<List<MethodEvent>> {


    private  val MAX_METHODS = 100


    private val eventsByMethod = HashMap<String, MethodEvent>()


    private val top100SlowestMethods =
        PriorityQueue<MethodEvent> { m1, m2 ->
            m1.timeTaken.compareTo(m2.timeTaken)
        }


    override fun addItem(methodEvent: MethodEvent) {

        val methodName = methodEvent.methodName


        val existing = eventsByMethod[methodName]

        if (existing != null) {

            // Replace it only if this invocation is slower.
            if (methodEvent.timeTaken > existing.timeTaken) {
                eventsByMethod[methodName] = methodEvent

                // The old event is still sitting inside the heap,
                // so remove it.
                top100SlowestMethods.remove(existing)

                top100SlowestMethods.add(methodEvent)
            }

            return
        }

        // First time seeing this method.
        if (top100SlowestMethods.size < MAX_METHODS) {

            eventsByMethod[methodName] = methodEvent
            top100SlowestMethods.add(methodEvent)

        } else if (methodEvent.timeTaken > top100SlowestMethods.peek().timeTaken) {

            // New method is slower than the fastest method
            // currently in our top 100.

            val removed = top100SlowestMethods.poll()

            eventsByMethod.remove(removed.methodName)

            eventsByMethod[methodName] = methodEvent
            top100SlowestMethods.add(methodEvent)
        }
    }

    override fun getDescription(): String {
        return "Top ${MAX_METHODS} slowest methods"
    }

    override fun getInformation(): List<MethodEvent> {
        return top100SlowestMethods
            .sortedByDescending { it.timeTaken }
    }
}