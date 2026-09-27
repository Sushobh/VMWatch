package com.sushobh.fraglens.methodtimer

import java.math.BigInteger




internal class LiveEventsInsight(
    private val maxEvents: Int = 2_000
) : MethodTimerInsight<List<MethodEventGroup>> {
    private var id : BigInteger = BigInteger("0")
    private val groups = ArrayDeque<MethodEventGroup>()

    private var eventCount = 0

    override fun addItem(methodEvent: MethodEvent) {
        val lastGroup = groups.lastOrNull()

        if (lastGroup != null &&
            lastGroup.methodName == methodEvent.methodName
        ) {
            lastGroup.events.add(methodEvent)
        } else {
            id = id.plus(BigInteger("1"))
            groups.addLast(
                MethodEventGroup(
                    methodName = methodEvent.methodName,
                    events = mutableListOf(methodEvent),
                    id
                )
            )
        }

        eventCount++
        trimToMaxEvents()
    }

    private fun trimToMaxEvents() {
        while (eventCount > maxEvents) {
            val firstGroup = groups.first()

            if (firstGroup.events.size == 1) {
                groups.removeFirst()
            } else {
                firstGroup.events.removeAt(0)
            }

            eventCount--
        }
    }

    override fun getDescription(): String =
        "Live method events"

    override fun getInformation(): List<MethodEventGroup> =
        groups.map { group ->
            group.copy(
                events = group.events.toMutableList()
            )
        }.sortedBy { it.events.map { it.id.multiply(BigInteger("-1")) }.min() }

    override fun resetData() {
        groups.clear()
        eventCount = 0
    }
}

