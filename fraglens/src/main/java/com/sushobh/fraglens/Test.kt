package com.sushobh.fraglens

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class TestViewModel : ViewModel() {

    data class Person(
        val name: String,
        val age: Int,
        val isEmployed: Boolean
    )

    private val personFlow = MutableStateFlow(
        Person("John Doe", 30, true)
    )
    private val stateFlow = MutableStateFlow(false)
    private val liveData = MutableLiveData(false)

    private var someText = "Hello, World!"
    private val soomBool = false
    private val sumInt = 412
    val personObj = Person(
        name = "Jane Doe",
        age = 25,
        isEmployed = false
    )
}
