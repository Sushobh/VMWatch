package com.sushobh.fraglens

import androidx.activity.ComponentActivity
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


    data class ActivityHolder(val activity : ComponentActivity? = null)

    data class Person(
        val name: String,
        val age: Int,
        val isEmployed: Boolean,
        val hobbies : List<Hobby> =
            arrayListOf(Hobby("Cricket"),Hobby("Chess")),
        val skills: Array<Skill> = arrayOf(Skill("Programming"),Skill("Writing"))
    )

    val hobbiesOfCat : List<Hobby> =
        arrayListOf(Hobby("Meowing"),Hobby("Running"))
    val skillsOfCat: Array<Skill> = arrayOf(Skill("Jumping"),Skill("Hunting"))

    data class Hobby(val name : String)
    data class Skill(val name : String)

    private val personFlow = MutableStateFlow(
        Person("John Doe", 30, true)
    )
    private val stateFlow = MutableStateFlow(false)
    private val liveData = MutableLiveData(false)
    private val activityFlow = MutableStateFlow<ActivityHolder?>(null)
    private var someText = "Hello, World!"
    private val soomBool = false
    private val sumInt = 412
    private val nestedObj : Level1 = NestedObj.obj
    val geographyToAnimals: Map<String, List<String>> = mapOf(
        "Africa" to listOf("Lion", "Elephant", "Giraffe"),
        "Asia" to listOf("Tiger", "Panda", "Komodo Dragon"),
        "Australia" to listOf("Kangaroo", "Koala", "Emu"),
        "North America" to listOf("Bison", "Bald Eagle", "Grizzly Bear"),
        "South America" to listOf("Jaguar", "Sloth", "Anaconda")
    )


    val personObj = Person(
        name = "Jane Doe",
        age = 25,
        isEmployed = false
    )

    fun setActivity(componentActivity: ComponentActivity){
        activityFlow.value = ActivityHolder(componentActivity)
    }
}
