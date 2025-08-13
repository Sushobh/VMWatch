package com.sushobh.fraglens

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle

class MainActivity : ComponentActivity() {

    val vm : TestViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        vm.toString()
        startActivity(Intent(this, com.sushobh.fraglens_ui.screens.MainActivity::class.java))
    }
}

/*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyListApp(vm: TestViewModel) {
    vm.toString()
    var frag = FragLens.viewModelIdFlow.collectAsStateWithLifecycle()
    frag.value.forEach {
        FragLens.parseProperties(it)?.name
        Log.d("asfs", FragLens.parseProperties(it)?.name.toString())
    }


    */
/*Scaffold(
        topBar = { TopAppBar(title = { Text("StateFlow List Example") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(items) { item ->
                    ListItem(
                        text = {
                            Text(item)
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }*//*

}*/
