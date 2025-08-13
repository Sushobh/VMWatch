package com.sushobh.fraglens_ui.screens

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sushobh.fraglens.FLPropertyOwner
import com.sushobh.fraglens.FragLens
import com.sushobh.fraglens_ui.screens.theme.ComposeBasicTheme

class `FragLensActivity` : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeBasicTheme {
                Column(modifier = Modifier.padding(30.dp)) {
                    MyListApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyListApp() {
    var viewModeList = FragLens.viewModelIdFlow.collectAsStateWithLifecycle()
    var hasDetailsBeenClicked by remember { mutableStateOf(false) }
    var details  by remember { mutableStateOf<FLPropertyOwner?>(null) }
    if(hasDetailsBeenClicked){
        LazyColumn {
            items(details?.properties ?: emptyList()) {
                Row(modifier = Modifier.fillMaxWidth().clickable(onClick = {

                })) {
                    Text(it.name, modifier = Modifier.weight(1f))
                    Text(it.value ?:"",Modifier.weight(1f))
                }
            }
        }
    }
    else {
        LazyColumn {
            items(viewModeList.value) {
                Row(modifier = Modifier.clickable(onClick = {
                    val props = FragLens.parseProperties(it)
                    hasDetailsBeenClicked = true
                    details = props
                })) {
                    Text(it.name)
                }
            }
        }
    }
}