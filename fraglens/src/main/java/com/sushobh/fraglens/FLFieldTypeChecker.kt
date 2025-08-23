package com.sushobh.fraglens

import com.sushobh.fraglens.serializers.FLDataClassSerialzer
import com.sushobh.fraglens.serializers.FLIterableSerializer
import com.sushobh.fraglens.serializers.FLMapSerializer
import com.sushobh.fraglens.serializers.FLPrimitveSerialzer
import java.lang.reflect.Field
import kotlin.reflect.full.memberFunctions

class FLFieldTypeChecker(private val flDataClassSerialzer: FLDataClassSerialzer,
                         private val flPrimitveSerialzer: FLPrimitveSerialzer, private val flMapSerializer: FLMapSerializer,
                         val flIterableSerializer: FLIterableSerializer
) {

    fun getSerializerForType(value: Any) : FLPropertySerialzer? {
        return when(checkType(value)){
            FLFieldType.DataClass -> flDataClassSerialzer
            FLFieldType.Iterable -> flIterableSerializer
            FLFieldType.Map -> flMapSerializer
            FLFieldType.Primitive -> flPrimitveSerialzer
            else -> null
        }
    }

    fun checkType(value : Any) : FLFieldType{
        val kClass = value::class
        val type = value::class.java
        if(kClass.java.isPrimitive ||
            value is String ||
            value is Number ||
            value is Boolean){
            return FLFieldType.Primitive
        }
        else if(kClass.isData) {
            return FLFieldType.DataClass
        }
        else if(androidx.lifecycle.LiveData::class.java.isAssignableFrom(type)) {
            return FLFieldType.LiveData
        }
        else if(Class.forName("kotlinx.coroutines.flow.StateFlow").isAssignableFrom(type)){
            return FLFieldType.StateFlow
        }
        else if(Iterable::class.java.isAssignableFrom(type) ||
            kClass.memberFunctions.any { it.name == "iterator" }){
            return FLFieldType.Iterable
        }
        else if(Map::class.java.isAssignableFrom(type)){
            return FLFieldType.Map
        }
        else {
            return FLFieldType.Unknown
        }
    }


    fun checkType(owner: Any, field: Field) : FLFieldType {
        field.isAccessible = true
        val value = field.get(owner)
        return checkType(value)
    }

}