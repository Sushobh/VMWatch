package com.sushobh.fraglens.interceptors

import com.sushobh.fraglens.FLProperty
import com.sushobh.fraglens.FLReferencePath
import com.sushobh.fraglens.serializers.FLDataClassSerialzer
import java.lang.reflect.Field

internal class FLDataclassInterceptor(private val flDataclassSerializer: FLDataClassSerialzer) : FLBasePropertyParserInterceptor() {
    override fun intercept(
        owner: Any,
        field: Field,
        fullFieldValue : Boolean
    ): FLProperty? {

        field.isAccessible = true

        val value = field.get(owner)
        val type = field.type

        if (value == null) {
             return FLProperty.forNull(field,owner)
        }
        val (short,long) = if(fullFieldValue){
            flDataclassSerializer.parseFullDisplayable(value)
        }
        else {
            flDataclassSerializer.parseShortDisplayable(value)
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


}