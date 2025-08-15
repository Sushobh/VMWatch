package com.sushobh.fraglens.interceptors

import com.sushobh.fraglens.FLProperty
import com.sushobh.fraglens.serializers.FLDataClassSerialzer
import java.lang.reflect.Field

class FLDataclassInterceptor(private val flDataclassInterceptor: FLDataClassSerialzer) : FLBasePropertyParserInterceptor() {
    override fun intercept(
        owner: Any,
        field: Field
    ): FLProperty? {

        field.isAccessible = true

        val value = field.get(owner)
        val type = field.type

        if (value == null) {
            return null
        }

        val kClass = value::class
        if (kClass.isData) {
            val (short,long) = flDataclassInterceptor.parseShortDisplayable(value)
            return FLProperty(
                name = field.name,
                type = type.name,
                value = short,
                isMutable = !java.lang.reflect.Modifier.isFinal(field.modifiers),
                fieldValue = long
            )
        }
        return null
    }


}