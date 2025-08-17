package com.sushobh.fraglens

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sushobh.fraglens.TestViewModel.Person
import kotlinx.coroutines.flow.MutableStateFlow





open class BaseViewModel : ViewModel() {
    private val personFlow0 = MutableStateFlow(
        Person("John Doe", 30, true)
    )
    private val stateFlow0 = MutableStateFlow(false)
    private val liveData0 = MutableLiveData(false)

    private var someText0 = "Hello, World!"
    private val soomBool0 = false
    private val sumInt0 = 412
}

class TestViewModel : BaseViewModel() {

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
