package com.sushobh.fraglens_ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.gson.Gson
import com.sushobh.fraglens.FragLens
import com.sushobh.fraglens_ui.models.TestViewModel
import com.sushobh.fraglens_ui.theme.GreenJC

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(json: String, onBack: () -> Unit, onClose: () -> Unit) {
    val gson = remember { Gson() }
    var showDialog by remember { mutableStateOf(false) }
    var propertyFlData by remember { mutableStateOf<String>("") }

    if (showDialog) {
        FullScreenDialog(
            showDialog = showDialog,
            onDismiss = { showDialog = false },
            items = propertyFlData
        )
    } else {
        val testModelList = gson.fromJson(json, TestViewModel::class.java)
        ScreenWithTopBar(title = "Details", onBack = onBack, onClose = onClose) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(testModelList.properties) { property ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 12.dp)
                    ) {
                        Text(
                            text = property.name,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = property.value,
                            modifier = Modifier
                                .weight(1f)
                                .clickable(onClick = {
                                    showDialog = true
                                    propertyFlData =
                                        FragLens.serializeFieldOfViewModel(property.refPath)?.fieldValue.orEmpty()
                                })
                        )
                    }
                    Spacer(Modifier.height(1.dp))
                }
            }
        }
    }
}


