package com.sushobh.fraglens.interceptors

import com.sushobh.fraglens.FLProperty
import com.sushobh.fraglens.serializers.FLDataClassSerialzer
import com.sushobh.fraglens.serializers.FLPrimitveSerialzer
import java.lang.reflect.Field

class FLStateFlowInterceptor(
    private val flDataClassSerialzer: FLDataClassSerialzer,private val flPrimitveSerializer: FLPrimitveSerialzer
) : FLBasePropertyParserInterceptor() {
    override fun intercept(
        owner: Any,
        field: Field
    ): FLProperty? {
        field.isAccessible = true

        var fieldValue: String? = null
        var displayValue: String? = null

        val value = field.get(owner)
        val type = field.type

        val isStateFlow = try {
            val stateFlowClass = Class.forName("kotlinx.coroutines.flow.StateFlow")
            stateFlowClass.isAssignableFrom(type)
        } catch (e: Exception) {
            false
        }
        if (!isStateFlow) {
            return null
        }
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
                val (short,long) = flPrimitveSerializer.parseShortDisplayable(stateFlowValue)
                return FLProperty(
                    name = field.name,
                    type = type.name,
                    value = short,
                    isMutable = !java.lang.reflect.Modifier.isFinal(field.modifiers),
                    fieldValue = long
                )
            } else if (kClass.isData) {
                val (short,long) = flDataClassSerialzer.parseShortDisplayable(stateFlowValue)
                return FLProperty(
                    name = field.name,
                    type = type.name,
                    value = short,
                    isMutable = !java.lang.reflect.Modifier.isFinal(field.modifiers),
                    fieldValue = long
                )
            }
        }


        return null
    }
}