package com.sushobh.fraglens_ui.models

import androidx.annotation.Keep
import com.sushobh.fraglens.FLViewModelId

@Keep
data class OwnerGroup(
    val ownerCode: Int,
    val ownerName: String,
    val ownerType: String,
    val viewModels: List<FLViewModelId>
)

data class ListItem(
    val code: Int,
    val name: String,
    val ownerCode: Int,
    val ownerName: String,
    val ownerType: String
)
