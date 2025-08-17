package com.sushobh.fraglens.serializers

import com.sushobh.fraglens.FLDisplayableValue
import com.sushobh.fraglens.FLPropertySerialzer


class FLDataClassSerialzer : FLPropertySerialzer {
    override fun parseFullDisplayable(value: Any): FLDisplayableValue {
        return FLDisplayableValue("Click to show",value.toString())
    }

    override fun parseShortDisplayable(value: Any): FLDisplayableValue {
        return FLDisplayableValue("Click to show")
    }
}