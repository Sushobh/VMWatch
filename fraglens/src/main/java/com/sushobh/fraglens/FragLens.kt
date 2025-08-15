package com.sushobh.fraglens

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.ranrings.libs.androidapptorest.AndroidRestServer
import com.ranrings.libs.androidapptorest.Base.GetRequestHandler
import com.ranrings.libs.androidapptorest.Base.PostRequestHandler
import com.sushobh.fraglens.interceptors.FLLiveDataInterceptor
import com.sushobh.fraglens.interceptors.FLPrimitveInterceptor
import com.sushobh.fraglens.interceptors.FLStateFlowInterceptor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object FragLens : FragLensApi {
    internal val propertyInterceptors = arrayListOf<FLPropertyParserInterceptor>()
    private val port = 56440;
    private var application: Application? = null
    private val activityStore = HashMap<Activity, List<Any>>()
    private val fragmentStore = HashMap<Fragment, List<Any>>()
    private var server: AndroidRestServer? = null
    private val _viewModelFlow = MutableStateFlow(emptyList<FLViewModelId>())
    override val viewModelIdFlow = _viewModelFlow.asStateFlow()

    override fun parseProperties(flViewModelId: FLViewModelId): FLPropertyOwner? {
        val allViewModelStore = getAllViewModels()
        val viewModel =
            allViewModelStore.find { it.hashCode() == flViewModelId.code } ?: return null
        val propertyParser = FLPropertyParserImpl()
        return propertyParser.parseProperties(viewModel)
    }

    fun addPropertyInterceptor(interceptor: FLPropertyParserInterceptor) {
        propertyInterceptors.add(interceptor)
    }


    fun init(application: Application) {
        this.application = application
        application.registerActivityLifecycleCallbacks(activityLifecycleCallback)
        startApiServer(application)
    }

    fun stopServer(){
        server?.stop()
    }

    internal fun unRegisterActivityCallback(callback: ActivityLifecycleCallbacks) {
        application?.registerActivityLifecycleCallbacks(callback)
    }


    internal fun onStartedActivity(activity: androidx.activity.ComponentActivity) {
        val viewModels = getAllViewModels(activity)
        activityStore[activity] = viewModels
        updateFlow()
    }

    internal fun onStopActivity(activity: androidx.activity.ComponentActivity) {
        activityStore.remove(activity)
        updateFlow()
    }

    internal fun onStartedFragment(fragment: Fragment) {
        val viewModels = getAllViewModels(fragment)
        fragmentStore[fragment] = viewModels
        updateFlow()
    }

    internal fun onStopFragment(fragment: Fragment) {
        fragmentStore.remove(fragment)
        updateFlow()
    }

    private fun updateFlow() {
        _viewModelFlow.value =
            getAllViewModels().map { FLViewModelId(it.hashCode(), it::class.java.simpleName) }
    }

    private fun getAllViewModels(): List<ViewModel> {
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

    private fun startApiServer(application: Application) {
        server = AndroidRestServer.Builder()
            .setApplication(application)
            .setPort(port)
            .addRequestHandler(object : GetRequestHandler<Any>() {

                override fun onGetRequest(uri: String): Any {
                    return viewModelIdFlow.value
                }

                override fun getMethodName(): String {
                    return "getallviewmodels"
                }

            }).addRequestHandler(object :
                PostRequestHandler<FLViewModelId, Any>(FLViewModelId::class) {
                override fun getMethodName(): String {
                    return "getpropsforviewmodel"
                }

                override fun onRequest(requestBody: FLViewModelId): Any {
                    val props = parseProperties(requestBody)
                    if (props != null) {
                        return FLParserApiResponse(isSuccess = true, items = props.properties, viewmodelName = props.name)
                    }
                    return FLParserApiResponse(isSuccess = false, items = emptyList(), viewmodelName = requestBody.name)
                }

            }).startWebApp(false).build()
        server?.start()
    }

}