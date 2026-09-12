package com.sushobh.vmwatch.actcycle

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sushobh.vmwatch.ActLifecycleEvents


@Composable
fun ActCycleViewer(actCycleViewModel: ActCycleViewModel) {

    LaunchedEffect(Unit) {
        actCycleViewModel.dispatch(ActcycleEvent.StartPollingEvents)
    }

    val state = actCycleViewModel.state.collectAsState()
    Column(modifier = Modifier.fillMaxSize().
         background(MaterialTheme.colorScheme.surface), verticalArrangement = Arrangement.Top) {
        LifecycleEventsView(state.value.events)
    }
}

@Composable
fun LifecycleEventsView(events: ActLifecycleEvents, modifier: Modifier = Modifier) {
    val items = listOf(
        "onStarted" to events.onStarted,
        "onResumed" to events.onResumed,
        "onPaused" to events.onPaused,
        "onStopped" to events.onStopped,
    )
    LazyVerticalGrid(
        modifier =
            modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        columns = GridCells.Fixed(4)
    ) {
        items(
            items.size
        ) {  index ->
            val  (header, names) = items[index]
            Card(modifier = Modifier.padding(20.dp)) {
                Column(modifier = Modifier.width(300.dp).padding(16.dp)) {
                    Text(
                        text = header,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Spacer(Modifier.background(MaterialTheme.colorScheme.primary).height(1.dp).fillMaxWidth())
                    if (names.isEmpty()) {
                        Text(text = "None", style = MaterialTheme.typography.bodySmall)
                    } else {
                        names.forEach { name ->
                            key(name){
                                Text(
                                    text = name,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(vertical = 2.dp).padding(vertical = 10.dp)
                                )
                                Spacer(Modifier.background(MaterialTheme.colorScheme.primary).height(0.5.dp).fillMaxWidth())
                            }

                        }
                    }
                }
            }
        }
    }
}