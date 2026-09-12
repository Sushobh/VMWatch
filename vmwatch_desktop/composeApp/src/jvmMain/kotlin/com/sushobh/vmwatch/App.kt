package com.sushobh.vmwatch

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.sushobh.vmwatch.actcycle.ActCycleViewModel
import com.sushobh.vmwatch.actcycle.ActCycleViewer
import com.sushobh.vmwatch.config.ConfigApi
import com.sushobh.vmwatch.ui.AppBar
import com.sushobh.vmwatch.ui.VMWatchStateApiImpl
import com.sushobh.vmwatch.ui.polling.FLPollingDetailsView
import com.sushobh.vmwatch.ui.polling.PollingViewModel
import com.sushobh.vmwatch.ui.theme.ThemeViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    val configApi = remember { ConfigApi() }
    val themeViewModel = remember { ThemeViewModel() }
    val vmWatchStateApi = remember { VMWatchStateApiImpl() }
    val pollingViewModel = remember { PollingViewModel(configApi, vmWatchStateApi) }
    val actCycleViewModel = remember { ActCycleViewModel(configApi) }

    val availableThemes by themeViewModel.themes.collectAsState()
    val currentTheme by themeViewModel.currentTheme.collectAsState()

    val currentSelectedApp by themeViewModel.currentSelectedApp.collectAsState()
    val availableApps by themeViewModel.apps.collectAsState()

    MaterialTheme(
        colorScheme = currentTheme.colorScheme
    ) {
        Scaffold(
            topBar = {
                AppBar(
                    appName = configApi.getAppName(),
                    availableThemes = availableThemes,
                    selectedTheme = currentTheme,
                    onThemeSelected = { theme -> themeViewModel.onThemeSelected(theme) },
                    availableApps = availableApps,
                    onAppSelected = {
                        themeViewModel.onAppSelected(it)
                    },
                    selectedApp = currentSelectedApp
                )
            }
        ) { paddingValues ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when(currentSelectedApp){
                    VMWatchApps.AppActCycle -> {
                        ActCycleViewer(actCycleViewModel)
                    }
                    VMWatchApps.AppViewModelCheck -> {
                        FLPollingDetailsView(pollingViewModel)
                    }
                }
            }
        }
    }
}