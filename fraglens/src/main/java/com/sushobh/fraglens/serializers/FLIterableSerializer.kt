package com.sushobh.fraglens.serializers

import com.sushobh.fraglens.FLDisplayableValue
import com.sushobh.fraglens.FLPropertySerialzer
import com.sushobh.fraglens.serializers.SafeGson

class FLIterableSerializer : FLPropertySerialzer {

    override fun parseFullDisplayable(value: Any): FLDisplayableValue {
        val json = when (value) {
            is Iterable<*> -> SafeGson.toJson(value, maxSize = 20000)
            is Array<*> -> SafeGson.toJson(value.toList(), maxSize = 20000)
            is IntArray -> SafeGson.toJson(value.toList(), maxSize = 20000)
            is LongArray -> SafeGson.toJson(value.toList(), maxSize = 20000)
            is DoubleArray -> SafeGson.toJson(value.toList(), maxSize = 20000)
            is FloatArray -> SafeGson.toJson(value.toList(), maxSize = 20000)
            is BooleanArray -> SafeGson.toJson(value.toList(), maxSize = 20000)
            is CharArray -> SafeGson.toJson(value.toList(), maxSize = 20000)
            else -> SafeGson.toJson(value, maxSize = 20000)
        }
        return FLDisplayableValue("Click to show", json)
    }

    override fun parseShortDisplayable(value: Any): FLDisplayableValue {
        val size = when (value) {
            is Iterable<*> -> value.count()
            is Array<*> -> value.size
            is IntArray -> value.size
            is LongArray -> value.size
            is DoubleArray -> value.size
            is FloatArray -> value.size
            is BooleanArray -> value.size
            is CharArray -> value.size
            else -> -1
        }

        val shortText = if (size >= 0) "Iterable of size $size" else "Iterable"
        return FLDisplayableValue(shortText)
    }
}
