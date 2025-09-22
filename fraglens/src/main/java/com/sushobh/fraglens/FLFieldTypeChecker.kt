package com.sushobh.fraglens

import com.sushobh.fraglens.serializers.FLDataClassSerialzer
import com.sushobh.fraglens.serializers.FLIterableSerializer
import com.sushobh.fraglens.serializers.FLMapSerializer
import com.sushobh.fraglens.serializers.FLPrimitveSerialzer
import java.lang.reflect.Field
import kotlin.reflect.KClass
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.jvm.kotlinProperty

class FLFieldTypeChecker(private val flDataClassSerialzer: FLDataClassSerialzer,
                         private val flPrimitveSerialzer: FLPrimitveSerialzer, private val flMapSerializer: FLMapSerializer,
                         val flIterableSerializer: FLIterableSerializer
) {

    fun getSerializerForType(value: Any) : FLPropertySerialzer? {
        return when(checkTypeBasedOnValue(value)){
            FLFieldType.DataClass -> flDataClassSerialzer
            FLFieldType.Iterable -> flIterableSerializer
            FLFieldType.Map -> flMapSerializer
            FLFieldType.Primitive -> flPrimitveSerialzer
            else -> null
        }
    }

    // --- common internal function ---
    private fun findFieldType(type: Class<*>, kClass: KClass<*>? = null): FLFieldType {
        val effectiveKClass = kClass ?: type.kotlin

        return when {
            // primitives & simple types
            type.isPrimitive ||
                    type == String::class.java ||
                    Number::class.java.isAssignableFrom(type) ||
                    type == java.lang.Boolean::class.java -> FLFieldType.Primitive

            // data classes
            effectiveKClass.isData -> FLFieldType.DataClass

            // LiveData
            androidx.lifecycle.LiveData::class.java.isAssignableFrom(type) -> FLFieldType.LiveData

            // StateFlow
            try {
                Class.forName("kotlinx.coroutines.flow.StateFlow").isAssignableFrom(type)
            } catch (_: ClassNotFoundException) {
                false
            } -> FLFieldType.StateFlow

            // collections
            Iterable::class.java.isAssignableFrom(type) ||
                    effectiveKClass.memberFunctions.any { it.name == "iterator" } -> FLFieldType.Iterable

            Map::class.java.isAssignableFrom(type) -> FLFieldType.Map

            else -> FLFieldType.Unknown
        }
    }

    // --- public API for value ---
    fun checkTypeBasedOnValue(value: Any): FLFieldType {
        return findFieldType(value::class.java, value::class)
    }

    // --- public API for field ---
    fun checkTypeBasedOnField(field: Field): FLFieldType {
        val kClass = field.kotlinProperty?.returnType?.classifier as? KClass<*>
        return findFieldType(field.type, kClass)
    }

}