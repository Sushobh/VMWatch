package com.sushobh.fraglens.interceptors

import com.sushobh.fraglens.FLProperty
import com.sushobh.fraglens.FLReferencePath
import com.sushobh.fraglens.serializers.FLDataClassSerialzer
import com.sushobh.fraglens.serializers.FLPrimitveSerialzer
import java.lang.reflect.Field

internal class FLStateFlowInterceptor(
    private val flDataClassSerialzer: FLDataClassSerialzer,private val flPrimitveSerializer: FLPrimitveSerialzer
) : FLBasePropertyParserInterceptor() {
    override fun intercept(
        owner: Any,
        field: Field,
        fullFieldValue: Boolean
    ): FLProperty? {
        field.isAccessible = true

        val value = field.get(owner)
        val type = field.type

        val stateFlowValue = try {
            val getValueMethod = value?.javaClass?.getMethod("getValue")
            getValueMethod?.isAccessible = true
            getValueMethod?.invoke(value)
        } catch (e: Exception) {
            null
        }

        if (stateFlowValue != null) {
            val kClass = stateFlowValue::class
            if (kClass.java.isPrimitive ||
                stateFlowValue is String ||
                stateFlowValue is Number ||
                stateFlowValue is Boolean
            ) {
                val (short,long) = if(fullFieldValue){
                    flPrimitveSerializer.parseFullDisplayable(stateFlowValue)
                }
                else {
                    flPrimitveSerializer.parseShortDisplayable(stateFlowValue)
                }
                return FLProperty(
                    name = field.name,
                    type = type.name,
                    value = short,
                    isMutable = !java.lang.reflect.Modifier.isFinal(field.modifiers),
                    fieldValue = long,
                    refPath = FLReferencePath(owner.hashCode(),value.hashCode())
                )
            } else if (kClass.isData) {
                val (short,long) = if(fullFieldValue){
                    flDataClassSerialzer.parseFullDisplayable(stateFlowValue)
                }
                else {
                    flDataClassSerialzer.parseShortDisplayable(stateFlowValue)
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


        return null
    }
}