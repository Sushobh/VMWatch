package com.sushobh.fraglens.serializers

import com.sushobh.fraglens.FLDisplayableValue
import com.sushobh.fraglens.FLPropertySerialzer

class FLMapSerializer  : FLPropertySerialzer {
    override fun parseFullDisplayable(value: Any): FLDisplayableValue {
        val valueGson =  SafeGson.toJson(value, maxSize = 20000)
        return FLDisplayableValue("Click to show", valueGson)
    }

    override fun parseShortDisplayable(value: Any): FLDisplayableValue {

        when(value){
            is Map<*,*> -> {
                return FLDisplayableValue("Map of Size ${value.size}")
            }
        }

        return FLDisplayableValue("Click to show")
    }
}