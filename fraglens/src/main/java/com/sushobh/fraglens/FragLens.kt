package com.sushobh.fraglens

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object FragLens : FragLensApi {
    internal val propertyInterceptors = arrayListOf<FLPropertyParserInterceptor>()

    private var application: Application? = null
    private val activityStore = HashMap<Activity,List<Any>>()
    private val fragmentStore = HashMap<Fragment, List<Any>>()

    private val _viewModelFlow = MutableStateFlow(emptyList<FLViewModelId>())
    override val viewModelIdFlow = _viewModelFlow.asStateFlow()

    override fun parseProperties(flViewModelId: FLViewModelId) : FLPropertyOwner?{
        val allViewModelStore = getAllViewModels()
        val viewModel = allViewModelStore.find { it.hashCode() == flViewModelId.code } ?: return null
        val propertyParser = FLPropertyParserImpl()
        return propertyParser.parseProperties(viewModel)
    }

    fun addPropertyInterceptor(interceptor: FLPropertyParserInterceptor){
        propertyInterceptors.add(interceptor)
    }



    fun init(application: Application){
        this.application = application
        application.registerActivityLifecycleCallbacks(activityLifecycleCallback)
    }


    internal fun unRegisterActivityCallback(callback : ActivityLifecycleCallbacks){
        application?.registerActivityLifecycleCallbacks(callback)
    }


    internal fun onStartedActivity(activity: androidx.activity.ComponentActivity){
        val viewModels = getAllViewModels(activity)
        activityStore[activity] = viewModels
        updateFlow()
    }

    internal fun onStopActivity(activity: androidx.activity.ComponentActivity){
          activityStore.remove(activity)
          updateFlow()
    }

    internal fun onStartedFragment(fragment: Fragment){
         val viewModels = getAllViewModels(fragment)
         fragmentStore[fragment] = viewModels
         updateFlow()
    }

    internal fun onStopFragment(fragment: Fragment){
        fragmentStore.remove(fragment)
        updateFlow()
    }

    private fun updateFlow(){
        _viewModelFlow.value = getAllViewModels().map { FLViewModelId(it.hashCode(),it::class.java.simpleName) }
    }

    private fun getAllViewModels() : List<ViewModel>{
        val viewModels = mutableListOf<ViewModel>()
        activityStore.values.forEach {
            viewModels.addAll(it as Collection<ViewModel>)
        }
        fragmentStore.values.forEach {
            viewModels.addAll(it as Collection<ViewModel>)
        }
        return viewModels
    }

    internal fun getAllViewModels(owner: ViewModelStoreOwner): List<ViewModel> {
        return try {

            val storeField = ViewModelStoreOwner::class.java.getDeclaredMethod("getViewModelStore")
            val store = storeField.invoke(owner) as ViewModelStore

            val mapField = ViewModelStore::class.java.getDeclaredField("map")
            mapField.isAccessible = true
            val mMap = mapField.get(store) as Map<String, ViewModel>

            mMap.values.toList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

}