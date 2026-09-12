package com.sushobh.vmwatch.ui.theme

import com.sushobh.vmwatch.VMWatchApps
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ThemeViewModel {
    private val _themes = MutableStateFlow(AppTheme.values().toList())
    val themes: StateFlow<List<AppTheme>> = _themes

    private val _apps = MutableStateFlow(listOf(VMWatchApps.AppActCycle, VMWatchApps.AppViewModelCheck))
    val apps : StateFlow<List<VMWatchApps>> = _apps


    private val _currentSelectedApp : MutableStateFlow<VMWatchApps> = MutableStateFlow(VMWatchApps.AppActCycle)
    val currentSelectedApp : StateFlow<VMWatchApps> = _currentSelectedApp

    private val _currentTheme = MutableStateFlow(AppTheme.MINT)
    val currentTheme: StateFlow<AppTheme> = _currentTheme


    fun onAppSelected(app : VMWatchApps){
        _currentSelectedApp.value = app
    }

    fun onThemeSelected(theme: AppTheme) {
        _currentTheme.value = theme
    }
}
