package com.sushobh.fraglens_ui.screens.models

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
    val value: String
)