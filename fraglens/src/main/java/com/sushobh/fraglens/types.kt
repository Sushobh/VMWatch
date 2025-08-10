package com.sushobh.fraglens

import java.lang.reflect.Field


data class FLProperty(
    val name: String,
    val type: String,
    val value: String? = null,
    val isMutable: Boolean = false,
    val fieldValue : String? = null // This can be used to store the actual field value if needed
) {
    override fun toString(): String {
        return "Property(name='$name', type='$type', value=$value, isMutable=$isMutable)"
    }
}

data class FLPropertyOwner(
    val name: String,
    val type: String,
    val properties: List<FLProperty>,
    val ownerObject : Any? = null // This can be used to store the actual owner object if needed
) {
    override fun toString(): String {
        return "PropertyOwner(name='$name', type='$type', properties=$properties)"
    }
}

data class FLReflectionProperty(private val field : Field,private val owner : Any)


interface FLPropertyParser {
    fun parseProperties(owner: Any): FLPropertyOwner
    fun refresh(propertyOwner : FLPropertyOwner) : FLPropertyOwner
}

interface FLPropertyStore {
    val propertyOwners : MutableMap<String, FLPropertyOwner>
}

interface FLPropertyParserInterceptor {
    fun intercept(owner : Any,field : Field) : List<FLProperty>
}