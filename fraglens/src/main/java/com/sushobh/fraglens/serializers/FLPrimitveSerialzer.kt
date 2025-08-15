package com.sushobh.fraglens.serializers

import com.sushobh.fraglens.FLDisplayableValue
import com.sushobh.fraglens.FLPropertySerialzer


class FLPrimitveSerialzer : FLPropertySerialzer {
    override fun parseFullDisplayable(value: Any): FLDisplayableValue {
        return FLDisplayableValue(value.toString(),value.toString())
    }

    override fun parseShortDisplayable(value: Any): FLDisplayableValue {
        return FLDisplayableValue(value.toString(),value.toString())
    }
}