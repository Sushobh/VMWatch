package com.sushobh.fraglens.interceptors

import com.sushobh.fraglens.FLProperty
import com.sushobh.fraglens.FLReferencePath
import com.sushobh.fraglens.serializers.FLDataClassSerialzer
import com.sushobh.fraglens.serializers.FLPrimitveSerialzer
import java.lang.reflect.Field

internal class FLLiveDataInterceptor(
    private val flDataClassSerialzer: FLDataClassSerialzer,
    private val flPrimitveSerializer: FLPrimitveSerialzer
) : FLBasePropertyParserInterceptor() {
    override fun intercept(
        owner: Any,
        field: Field,
        fullFieldValue: Boolean
    ): FLProperty? {
        field.isAccessible = true


        val value = field.get(owner)
        if (value == null) {
            return null
        }
        val type = field.type

        val liveDataValue = try {
            val getValueMethod = value.javaClass?.getMethod("getValue")
            getValueMethod?.isAccessible = true
            getValueMethod?.invoke(value)
        } catch (e: Exception) {
            null
        }

        if (liveDataValue != null) {
            val kClass = liveDataValue::class
            if (kClass.java.isPrimitive ||
                liveDataValue is String ||
                liveDataValue is Number ||
                liveDataValue is Boolean
            ) {
                val (short, long) = if (fullFieldValue) {
                    flPrimitveSerializer.parseFullDisplayable(liveDataValue)
                } else {
                    flPrimitveSerializer.parseShortDisplayable(liveDataValue)
                }
                return FLProperty(
                    name = field.name,
                    type = type.name,
                    value = short,
                    isMutable = !java.lang.reflect.Modifier.isFinal(field.modifiers),
                    fieldValue = long,
                    refPath = FLReferencePath(owner.hashCode(), value.hashCode())
                )
            } else if (kClass.isData) {
                val (short, long) = if (fullFieldValue) {
                    flDataClassSerialzer.parseFullDisplayable(liveDataValue)
                } else {
                    flDataClassSerialzer.parseShortDisplayable(liveDataValue)
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

        return null
    }

}