package com.sushobh.vmwatch

import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable
import java.lang.reflect.Field

@Serializable
data class FLProperty(
    val name: String,
    val type: String,
    val value: String? = null,
    val isMutable: Boolean = false,
    val fieldValue: String? = null,
    val isClickToShow: Boolean = false,
    val refPath: FLReferencePath
) {
    override fun toString(): String {
        return "Property(name='$name', type='$type', value=$value, isMutable=$isMutable)"
    }
}


@Serializable
data class FLSerializeFieldResponse(val isSuccess: Boolean = false, val value: FLProperty? = null)


@Serializable
data class FLViewModelId(
    val code: Int,
    val name: String,
    val ownerName: String,
    val ownerCode: Int,
    val ownerType: String
)

@Serializable
data class FLParserApiResponse(
    val isSuccess: Boolean = false,
    val items: List<FLProperty> = emptyList(),
    val viewmodelName: String
)

@Serializable
data class FLReferencePath(val viewModelCode: Int, val fieldCode: Int) {

    operator fun get(index: Int): Int {
        if (index == 0) return viewModelCode
        if (index == 1) return fieldCode
        return -1
    }
}


sealed class FLCListViewItem(open val code: String) {
    data class FLCListViewModel(val viewModelId: FLViewModelId, override val code: String) : FLCListViewItem(code)
    data class FLCListViewModelOwner(val isSelected: Boolean = false, val name: String, override val code: String) :
        FLCListViewItem(code)
}