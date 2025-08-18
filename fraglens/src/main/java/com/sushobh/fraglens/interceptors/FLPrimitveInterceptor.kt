package com.sushobh.fraglens.interceptors

import com.sushobh.fraglens.FLProperty
import com.sushobh.fraglens.FLReferencePath
import com.sushobh.fraglens.serializers.FLPrimitveSerialzer
import java.lang.reflect.Field

internal class FLPrimitveInterceptor(private val primtiveSerialzer: FLPrimitveSerialzer) : FLBasePropertyParserInterceptor() {
    override fun intercept(
        owner: Any,
        field: Field,
        fullFieldValue : Boolean
    ): FLProperty? {
        field.isAccessible = true

        var fieldValue: String? = null
        var displayValue: String? = null

        val value = field.get(owner)
        val type = field.type

        if(value == null){
            return null
        }

        val kClass = value::class
        if (kClass.java.isPrimitive ||
            value is String ||
            value is Number ||
            value is Boolean
        ) {
            val (short,long) = if(fullFieldValue){
                primtiveSerialzer.parseFullDisplayable(value)
            }
            else {
                primtiveSerialzer.parseShortDisplayable(value)
            }
            return FLProperty(
                name = field.name,
                type = type.name,
                value = short,
                isMutable = !java.lang.reflect.Modifier.isFinal(field.modifiers),
                fieldValue = long,
                refPath = FLReferencePath(owner.hashCode(),value.hashCode())
            )
        }
        return null
    }

}