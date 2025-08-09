package com.sushobh.fraglens

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.MutableStateFlow

class FLPropertyParserImpl : FLPropertyParser{

    
    override fun parseProperties(owner: Any): FLPropertyOwner {
        val properties = mutableListOf<FLProperty>()
        val clazz = owner::class.java

        clazz.declaredFields.forEach { field ->
            field.isAccessible = true
            val value = field.get(owner)
            val type = field.type

            var fieldValue: String? = null
            var displayValue: String? = null

            val isLiveData = androidx.lifecycle.LiveData::class.java.isAssignableFrom(type)
            val isStateFlow = try {
                val stateFlowClass = Class.forName("kotlinx.coroutines.flow.StateFlow")
                stateFlowClass.isAssignableFrom(type)
            } catch (e: Exception) { false }

            if (isLiveData) {
                val liveDataValue = try {
                    val getValueMethod = value?.javaClass?.getMethod("getValue")
                    getValueMethod?.isAccessible = true
                    getValueMethod?.invoke(value)
                } catch (e: Exception) { null }

                if (liveDataValue != null) {
                    val kClass = liveDataValue::class
                    if (kClass.java.isPrimitive ||
                        liveDataValue is String ||
                        liveDataValue is Number ||
                        liveDataValue is Boolean
                    ) {
                        fieldValue = liveDataValue.toString()
                        displayValue = liveDataValue.toString()
                    } else if (kClass.isData) {
                        fieldValue = liveDataValue.toString()
                        displayValue = liveDataValue.toString()
                    }
                }
                properties.add(
                    FLProperty(
                        name = field.name,
                        type = type.name,
                        value = displayValue,
                        isMutable = !java.lang.reflect.Modifier.isFinal(field.modifiers),
                        fieldValue = fieldValue
                    )
                )
            } else if (isStateFlow) {
                val stateFlowValue = try {
                    val getValueMethod = value?.javaClass?.getMethod("getValue")
                    getValueMethod?.isAccessible = true
                    getValueMethod?.invoke(value)
                } catch (e: Exception) { null }

                if (stateFlowValue != null) {
                    val kClass = stateFlowValue::class
                    if (kClass.java.isPrimitive ||
                        stateFlowValue is String ||
                        stateFlowValue is Number ||
                        stateFlowValue is Boolean
                    ) {
                        fieldValue = stateFlowValue.toString()
                        displayValue = stateFlowValue.toString()
                    } else if (kClass.isData) {
                        fieldValue = stateFlowValue.toString()
                        displayValue = stateFlowValue.toString()
                    }
                }
                properties.add(
                    FLProperty(
                        name = field.name,
                        type = type.name,
                        value = displayValue,
                        isMutable = !java.lang.reflect.Modifier.isFinal(field.modifiers),
                        fieldValue = fieldValue
                    )
                )
            } else if (value != null) {
                val kClass = value::class
                if (kClass.java.isPrimitive ||
                    value is String ||
                    value is Number ||
                    value is Boolean
                ) {
                    fieldValue = value.toString()
                    displayValue = value.toString()
                } else if (kClass.isData) {
                    fieldValue = value.toString()
                    displayValue = value.toString()
                }
                if (fieldValue != null) {
                    properties.add(
                        FLProperty(
                            name = field.name,
                            type = type.name,
                            value = displayValue,
                            isMutable = !java.lang.reflect.Modifier.isFinal(field.modifiers),
                            fieldValue = fieldValue
                        )
                    )
                }
            }
        }

        return FLPropertyOwner(
            name = clazz.simpleName,
            type = clazz.name,
            properties = properties,
            ownerObject = owner
        )
    }

    override fun refresh(propertyOwner: FLPropertyOwner): FLPropertyOwner {
        return parseProperties(propertyOwner.ownerObject ?: return propertyOwner)
    }


}