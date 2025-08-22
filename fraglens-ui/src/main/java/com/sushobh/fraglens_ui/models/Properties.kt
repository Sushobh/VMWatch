package com.sushobh.fraglens_ui.models

import com.sushobh.fraglens.FLReferencePath

data class TestViewModel(
    val name: String,
    val properties: List<Property>,
    val type: String
)

data class Property(
    val fieldValue: String,
    val isMutable: Boolean,
    val name: String,
    val type: String,
    val value: String,
    val refPath: FLReferencePath
)
