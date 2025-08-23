package com.sushobh.fraglens.interceptors

import com.sushobh.fraglens.FLFieldType
import com.sushobh.fraglens.FLFieldTypeChecker
import com.sushobh.fraglens.FLProperty
import com.sushobh.fraglens.FLReferencePath
import com.sushobh.fraglens.serializers.FLDataClassSerialzer
import com.sushobh.fraglens.serializers.FLPrimitveSerialzer
import java.lang.reflect.Field

internal class FLLiveDataInterceptor(
    private val fieldTypeChecker : FLFieldTypeChecker
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

        if(liveDataValue == null){
            return null
        }

        val serializer = fieldTypeChecker.getSerializerForType(liveDataValue)
        if(serializer == null){
            return null
        }
        val (short, long) = if (fullFieldValue) {
            serializer.parseFullDisplayable(liveDataValue)
        } else {
            serializer.parseShortDisplayable(liveDataValue)
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