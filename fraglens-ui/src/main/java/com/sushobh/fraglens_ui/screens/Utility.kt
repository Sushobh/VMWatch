package com.sushobh.fraglens_ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.sushobh.fraglens_ui.theme.GreenJC

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonTopBar(title: String, onBack: (() -> Unit)? = null, onClose: () -> Unit) {

    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if(onBack != null){
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        }, actions = {
            IconButton(onClick = {
                onClose.invoke()
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ExitToApp,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }
        },colors = TopAppBarDefaults.topAppBarColors(
            containerColor = GreenJC,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White
        )
    )
}


@Composable
fun ScreenWithTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    onClose: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = { CommonTopBar(title, onBack,onClose) }
    ) { padding ->
        content(padding)
    }
}