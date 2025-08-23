package com.sushobh.fraglens.interceptors

import com.sushobh.fraglens.FLProperty
import com.sushobh.fraglens.FLReferencePath
import com.sushobh.fraglens.serializers.FLDataClassSerialzer
import com.sushobh.fraglens.serializers.FLIterableSerializer
import java.lang.reflect.Field
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.jvm.isAccessible

internal class FLIterableInterceptor(
    private val flIterableSerializer: FLIterableSerializer
) : FLBasePropertyParserInterceptor() {

    override fun intercept(owner: Any, field: Field, fullFieldValue: Boolean): FLProperty? {
        field.isAccessible = true

        val type = field.type
        val value = field.get(owner) ?:  return FLProperty.forNull(field,owner)
        val (short,long) = if(fullFieldValue){
            flIterableSerializer.parseFullDisplayable(value)
        }
        else {
            flIterableSerializer.parseShortDisplayable(value)
        }

        return FLProperty(
            name = field.name,
            type = type.name,
            value = short,
            isMutable = !java.lang.reflect.Modifier.isFinal(field.modifiers),
            fieldValue = long,
            refPath = FLReferencePath(owner.hashCode(), value.hashCode())
        )
    }
}
