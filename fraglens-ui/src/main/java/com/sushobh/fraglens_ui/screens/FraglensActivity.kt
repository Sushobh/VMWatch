package com.sushobh.fraglens_ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.sushobh.fraglens.FLViewModelId
import com.sushobh.fraglens.FragLens
import com.sushobh.fraglens_ui.screens.theme.ComposeBasicTheme
import com.sushobh.fraglens_ui.screens.theme.GreenJC

class FraglensActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            ComposeBasicTheme {
                NavHost(navController, startDestination = "Home") {
                    composable("Home") { MyAppHomeScreen(navController) }
                   // composable("Detail") { DetailsScreen(navController) }

                    composable("Detail/{listAsJson}",
                        arguments = listOf(navArgument("listAsJson") { defaultValue = "[]" })
                    ) { backStackEntry ->
                        val json = backStackEntry.arguments!!.getString("listAsJson") ?: "[]"
                        DetailsScreen(navController, json)
                    }

                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppHomeScreen(navController: NavController) {
    var viewmodelList = FragLens.viewModelIdFlow.collectAsStateWithLifecycle()
    val gson = remember { Gson() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ViewModel Lists") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GreenJC,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(viewmodelList.value) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clickable(onClick = {
                            val props = FragLens.parseProperties(it)
                            val jsonString = gson.toJson(props)
                            navController.navigate("Detail/$jsonString")
                            //navController.navigate("Detail")
                        }),
                    shape = MaterialTheme.shapes.large,
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = it.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun HomeScreen(flData: FLViewModelId, navController: NavController) {
    /*val gson = remember { Gson() }
    val type = object : TypeToken<List<String>>() {}.type
    val listJson: List<FLViewModelId> = gson.fromJson(listJson, type)*/

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = {
                val props = FragLens.parseProperties(flData)
                navController.navigate("Detail")
            }),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = flData.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        }
    }
}