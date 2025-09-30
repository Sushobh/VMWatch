package com.sushobh.fraglens.serializers

import com.sushobh.fraglens.FLDisplayableValue
import com.sushobh.fraglens.FLPropertySerialzer

class FLCommonSerializer : FLPropertySerialzer {
    override fun parseFullDisplayable(value: Any): FLDisplayableValue {
        return FLDisplayableValue("Click to show", SafeGson.toJson(obj = value, maxSize = 20000))
    }

    override fun parseShortDisplayable(value: Any): FLDisplayableValue {
        return FLDisplayableValue("Click to show")
    }
}