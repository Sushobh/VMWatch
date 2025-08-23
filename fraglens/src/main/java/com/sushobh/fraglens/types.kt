package com.sushobh.fraglens

import kotlinx.coroutines.flow.StateFlow
import java.lang.reflect.Field

data class FLProperty(
    val name: String,
    val type: String,
    val value: String? = null,
    val isMutable: Boolean = false,
    val fieldValue : String? = null,
    val isClickToShow : Boolean = false,
    val refPath : FLReferencePath
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


interface FLPropertyParser {
    fun parseProperties(owner: Any): FLPropertyOwner
    fun refresh(propertyOwner : FLPropertyOwner) : FLPropertyOwner
    fun serializeFieldOfViewModel(owner : Any,path : FLReferencePath) : FLProperty?
}


interface FLPropertyParserInterceptor {
    fun intercept(owner : Any,field : Field,fullFieldValue : Boolean) : FLProperty?
}


interface FragLensApi {
    val viewModelIdFlow : StateFlow<List<FLViewModelId>>
    fun parseProperties(flViewModelId: FLViewModelId) : FLPropertyOwner?
    fun serializeFieldOfViewModel(referencePath : FLReferencePath) : FLProperty?
}

sealed class FLViewModelOwnerType(val name : String) {
    data object Activity : FLViewModelOwnerType("Activity")
    data object Fragment : FLViewModelOwnerType("Fragment")
}


data class FLViewModelId(val code : Int,val name : String,
                         val ownerName : String,val ownerCode : Int,val ownerType : String)

data class FLParserApiResponse(val isSuccess : Boolean = false,val items : List<FLProperty> = emptyList(), val viewmodelName : String)
data class FLSerializeFieldResponse(val isSuccess : Boolean = false,val value : FLProperty? = null)


data class FLDisplayableValue(val shortDisplable : String,val fullDisplayable : String? = null)

interface FLPropertySerialzer {
    fun parseFullDisplayable(value : Any) : FLDisplayableValue
    fun parseShortDisplayable(value : Any) : FLDisplayableValue
}

data class FLConfig(val interceptors : List<FLPropertyParserInterceptor>)

data class FLReferencePath(val viewModelCode : Int,val fieldCode : Int) {

    operator fun get(index : Int) : Int {
        if(index == 0) return viewModelCode
        if(index == 1) return fieldCode
        return -1
    }
}

interface FLCurrentActivityListener {
    fun onResumed(activity : androidx.activity.ComponentActivity)
    fun onPaused(activity : androidx.activity.ComponentActivity)
}


sealed interface FLFieldType {
    data object StateFlow : FLFieldType
    data object DataClass : FLFieldType
    data object Iterable : FLFieldType
    data object Map : FLFieldType
    data object LiveData : FLFieldType
    data object Primitive : FLFieldType
    data object Unknown : FLFieldType
}
