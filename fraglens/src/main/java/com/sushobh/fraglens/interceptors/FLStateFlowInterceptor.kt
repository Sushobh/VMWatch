package com.sushobh.fraglens.interceptors

import com.sushobh.fraglens.FLFieldTypeChecker
import com.sushobh.fraglens.FLProperty
import com.sushobh.fraglens.FLReferencePath
import java.lang.reflect.Field

internal class FLStateFlowInterceptor(
    private val fieldTypeChecker : FLFieldTypeChecker
) : FLBasePropertyParserInterceptor() {
    override fun intercept(
        owner: Any,
        field: Field,
        fullFieldValue: Boolean
    ): FLProperty? {
        field.isAccessible = true

        val value = field.get(owner)
        val type = field.type

        if(value == null){
            return null
        }

        val stateFlowValue = try {
            val getValueMethod = value.javaClass?.getMethod("getValue")
            getValueMethod?.isAccessible = true
            getValueMethod?.invoke(value)
        } catch (e: Exception) {
            null
        }

        if (stateFlowValue != null) {
            val serializer = fieldTypeChecker.getSerializerForType(stateFlowValue)
            if(serializer == null){
                return null
            }
            val (short, long) = if (fullFieldValue) {
                serializer.parseFullDisplayable(stateFlowValue)
            } else {
                serializer.parseShortDisplayable(stateFlowValue)
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


        return FLProperty.forNull(field,owner)
    }
}