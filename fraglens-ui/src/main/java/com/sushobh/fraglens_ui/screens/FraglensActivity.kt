package com.sushobh.fraglens_ui.screens

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sushobh.fraglens.FLViewModelId
import com.sushobh.fraglens.FragLens
import com.sushobh.fraglens_ui.models.ListItem
import com.sushobh.fraglens_ui.models.OwnerGroup
import com.sushobh.fraglens_ui.theme.ComposeBasicTheme
import com.sushobh.fraglens_ui.theme.GreenJC

class FraglensActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            ComposeBasicTheme {
                NavHost(navController, startDestination = "Home") {
                    composable("Home") {
                        var viewmodelList = FragLens.viewModelIdFlow.collectAsStateWithLifecycle()
                        val gson = Gson()
                        val jsonString = gson.toJson(viewmodelList.value)
                        val itemType = object : TypeToken<List<ListItem>>() {}.type
                        val items: List<ListItem> = gson.fromJson(jsonString, itemType)
                        val grouped = groupItemsByOwner(items)
                        val activity = LocalActivity.current as ComponentActivity
                        val overlay =  InAppOverlay(activity)
                        ExpandableGroupedList(grouped) { clicked ->
                            overlay.showOverlay()
                            /*val flViewModelId = FLViewModelId(
                                clicked.code,
                                clicked.name,
                                clicked.ownerName,
                                clicked.ownerCode,
                                clicked.ownerType
                            )
                            val props = FragLens.parseProperties(flViewModelId)
                            val jsonString = gson.toJson(props)
                            navController.navigate("Detail/$jsonString")*/
                        }
                    }
                    composable(
                        "Detail/{listAsJson}",
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
fun ExpandableGroupedList(groups: List<OwnerGroup>, onItemClick: (ListItem) -> Unit) {

    var expandedGroup by remember { mutableStateOf<Int?>(null) }
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
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)

        ) {
            items(groups) { group ->
                ExpandableGroup(
                    group = group,
                    isExpanded = expandedGroup == group.ownerCode,
                    onExpandToggle = {
                        expandedGroup =
                            if (expandedGroup == group.ownerCode) null else group.ownerCode
                    },
                    onItemClick = onItemClick
                )
            }
        }
    }
}

@Composable
fun ExpandableGroup(
    group: OwnerGroup,
    isExpanded: Boolean,
    onExpandToggle: () -> Unit,
    onItemClick: (ListItem) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onExpandToggle() },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${group.ownerName} (${group.ownerType})",
                    style = MaterialTheme.typography.titleMedium
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(Modifier.padding(top = 8.dp)) {
                    group.viewModels.forEach { vm ->
                        Column(Modifier.padding(12.dp)) {
                            Text(
                                vm.name, style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.clickable(onClick = { onItemClick(vm) }))
                        }
                    }
                }
            }
        }
    }
}

fun groupItemsByOwner(items: List<ListItem>): List<OwnerGroup> {
    return items.groupBy { it.ownerCode }
        .map { (ownerCode, viewModels) ->
            OwnerGroup(
                ownerCode = ownerCode,
                ownerName = viewModels.first().ownerName,
                ownerType = viewModels.first().ownerType,
                viewModels = viewModels
            )
        }
}
