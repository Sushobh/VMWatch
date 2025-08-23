package com.sushobh.fraglens.nav

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {

    private val _sharedData = MutableLiveData<String>()
    val sharedData: LiveData<String> = _sharedData

    fun updateData(newData: String) {
        _sharedData.value = newData
    }

    init {
        _sharedData.value = "Initial Data from ViewModel"
    }
}
