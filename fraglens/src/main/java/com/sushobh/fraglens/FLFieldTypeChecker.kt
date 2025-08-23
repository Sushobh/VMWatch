package com.sushobh.fraglens

import java.lang.reflect.Field
import kotlin.reflect.full.memberFunctions

class FLFieldTypeChecker {

    fun checkType(owner: Any, field: Field) : FLFieldType {
        field.isAccessible = true
        val value = field.get(owner)
        val kClass = value::class
        val type = field.type
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

}