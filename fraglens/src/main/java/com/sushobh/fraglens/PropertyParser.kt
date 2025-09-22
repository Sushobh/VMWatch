package com.sushobh.fraglens

import com.sushobh.fraglens.interceptors.FLDataclassInterceptor
import com.sushobh.fraglens.interceptors.FLIterableInterceptor
import com.sushobh.fraglens.interceptors.FLLiveDataInterceptor
import com.sushobh.fraglens.interceptors.FLMapInterceptor
import com.sushobh.fraglens.interceptors.FLPrimitveInterceptor
import com.sushobh.fraglens.interceptors.FLStateFlowInterceptor
import com.sushobh.fraglens.serializers.FLDataClassSerialzer
import com.sushobh.fraglens.serializers.FLIterableSerializer
import com.sushobh.fraglens.serializers.FLMapSerializer
import com.sushobh.fraglens.serializers.FLPrimitveSerialzer
import java.lang.reflect.Field

internal class FLPropertyParserImpl : FLPropertyParser{

    private val mapSerialzer = FLMapSerializer()
    private val flPrimitveSerialzer = FLPrimitveSerialzer()
    private val flDataClassSerialzer = FLDataClassSerialzer()
    private val flIterableSerializer = FLIterableSerializer()

    private val flFieldTypeChecker = FLFieldTypeChecker(flDataClassSerialzer,flPrimitveSerialzer,mapSerialzer,flIterableSerializer)
    private val primitiveInterceptor = FLPrimitveInterceptor(flPrimitveSerialzer)
    private val dataClassInterceptor = FLDataclassInterceptor(flDataClassSerialzer)
    private val liveDataInterceptor = FLLiveDataInterceptor(flFieldTypeChecker)
    private val stateFlowInterceptor = FLStateFlowInterceptor(flFieldTypeChecker)
    private val iterableInterceptor = FLIterableInterceptor(flIterableSerializer)
    private val mapInterceptor = FLMapInterceptor(mapSerialzer)
    private val interceptors = FragLens.propertyInterceptors.toMutableList()

    fun getDeclaredFieldsUpToLevel2(clazz: Class<*>): List<Field> {
        val fields = mutableListOf<Field>()
        var current: Class<*>? = clazz
        var level = 0

        while (current != null && level <= 2) {
            fields += current.declaredFields
            current = current.superclass
            level++
        }

        return fields
    }


    override fun parseProperties(owner: Any): FLPropertyOwner {
        val properties = mutableListOf<FLProperty>()
        val clazz = owner::class.java

        getDeclaredFieldsUpToLevel2(clazz).forEach { field ->
            val parsedProperty = parseField(owner,field,false)
            if(parsedProperty != null){
                properties.add(parsedProperty)
                return@forEach
            }
        }

        return FLPropertyOwner(
            name = clazz.simpleName,
            type = clazz.name,
            properties = properties,
            ownerObject = owner
        )
    }

    private fun parseField(owner: Any, field: Field,fullFieldValue : Boolean) : FLProperty?{
        for(interceptor in interceptors){
            val parsedProperty = interceptor.intercept(owner,field,fullFieldValue)
            if(parsedProperty != null){
                return parsedProperty
            }
        }
        return when(flFieldTypeChecker.checkTypeBasedOnField(field)){
            FLFieldType.DataClass -> dataClassInterceptor.intercept(owner,field,fullFieldValue)
            FLFieldType.Iterable -> iterableInterceptor.intercept(owner,field,fullFieldValue)
            FLFieldType.LiveData -> liveDataInterceptor.intercept(owner,field,fullFieldValue)
            FLFieldType.Map -> mapInterceptor.intercept(owner,field,fullFieldValue)
            FLFieldType.Primitive -> primitiveInterceptor.intercept(owner,field,fullFieldValue)
            FLFieldType.StateFlow -> stateFlowInterceptor.intercept(owner,field,fullFieldValue)
            FLFieldType.Unknown ->  return null
        }
    }

    override fun refresh(propertyOwner: FLPropertyOwner): FLPropertyOwner {
        return parseProperties(propertyOwner.ownerObject ?: return propertyOwner)
    }

    override fun serializeFieldOfViewModel(
        owner: Any,
        path: FLReferencePath
    ): FLProperty? {

        try {
            val clazz = owner::class.java

            getDeclaredFieldsUpToLevel2(clazz).forEach { field ->
                field.isAccessible = true
                val value = field.get(owner)
                if(value != null){
                    if(value.hashCode() == path[1]){
                        val parsedProperty = parseField(owner,field,true)
                        if(parsedProperty != null){
                            return parsedProperty
                        }
                    }
                }

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

}