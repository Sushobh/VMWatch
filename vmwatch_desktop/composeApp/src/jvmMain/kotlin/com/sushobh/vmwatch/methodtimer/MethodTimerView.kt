import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sushobh.vmwatch.methodtimer.*

@Composable
fun MethodTimerView(methodTimerViewModel: MethodTimerViewModel) {
    val state = methodTimerViewModel.state.collectAsStateWithLifecycle()
    val selectedInsight = state.value.currentlySelectedInsight
    val liveEventState = state.value.liveEventState

    LaunchedEffect(Unit) {
        methodTimerViewModel.dispatch(MethodTimerStateEvent.OnViewLoaded)
    }
    Column {
        Row(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
            Button(onClick = {
                methodTimerViewModel.dispatch(MethodTimerStateEvent.OnResetRequest)
            }) {
                Text("Reset")
            }
        }
        Row(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
            Column(Modifier.weight(0.5f).fillMaxHeight()) {
                MethodTimerHeaderText("Live Events")
                LiveMethodsView(liveEventState)
            }
            Spacer(Modifier.fillMaxHeight().width(10.dp))
            Column(Modifier.weight(0.5f).fillMaxHeight()) {
                val insightName = selectedInsight.title
                Row(Modifier.padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    MethodInsightSelector(
                        listOf(InsightType.TopFreq, InsightType.TopSlowest, InsightType.TopTotalTime),
                        selectedInsight,
                        {
                            methodTimerViewModel.dispatch(MethodTimerStateEvent.InsighTypeSelected(it))
                        })
                }
                when (selectedInsight) {
                    InsightType.TopFreq -> {
                        TopFreqView(state.value.topFreqState)
                    }

                    InsightType.TopSlowest -> {
                        TopSlowestView(state.value.topSlowestState)
                    }

                    InsightType.TopTotalTime -> {
                        TopTotalTimeView(state.value.totalTimeState)
                    }
                }
            }
        }
    }


}

@Composable
fun MethodTimerHeaderText(text: String) {
    Text(
        text = text, fontWeight = FontWeight.Bold, fontSize = 32.sp,
        modifier = Modifier.padding(10.dp), color = MaterialTheme.colorScheme.primary
    )
}


@Composable
private fun LiveMethodsView(liveEventState: LiveEventState) {
    if (liveEventState !is LiveEventState.Items) return

    val listState = rememberLazyListState()
    val items = liveEventState.methodEventGroups

    // Detect when a new item becomes the top-ranked item.
    LaunchedEffect(items.firstOrNull()?.id) {
        if (items.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxWidth()
    ) {


        items(
            items = items,
            key = { item -> item.id }
        ) { item ->

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .animateItem(),
                colors = CardDefaults.cardColors()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        "Id => ${item.id}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(5.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            item.methodName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(Modifier.width(20.dp))

                        Text("${item.events.size} times", fontSize = 12.sp)
                    }
                    Spacer(Modifier.height(5.dp))
                    Text(
                        "Max time => ${item.events.maxBy { it.timeTaken }.timeTaken} ms",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            Divider()
        }
    }
}