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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object FragLens : FragLensApi {
    internal var propertyInterceptors : List<FLPropertyParserInterceptor> = arrayListOf()
    private val port = 56440;
    private var application: Application? = null
    private val activityStore = HashMap<Activity, List<Any>>()
    private val fragmentStore = HashMap<Fragment, List<Any>>()
    private var server: AndroidRestServer? = null
    private val _viewModelFlow = MutableStateFlow(emptyList<FLViewModelId>())
    private lateinit var propertyParser : FLPropertyParser
    override val viewModelIdFlow = _viewModelFlow.asStateFlow()

    override fun parseProperties(flViewModelId: FLViewModelId): FLPropertyOwner? {
        val allViewModelStore = getAllViewModelsIds()
        val viewModelId =
            allViewModelStore.find { it.code == flViewModelId.code } ?: return null
        val viewModel = getAllViewModels().find { it.hashCode() == viewModelId.code }
        if(viewModel == null){
            return null
        }
        return propertyParser.parseProperties(viewModel)
    }

    override fun serializeFieldOfViewModel(referencePath: FLReferencePath): FLProperty? {
        val allViewModelStore = getAllViewModelsIds()
        val viewModelId =
            allViewModelStore.find { it.code == referencePath[0] } ?: return null
        val viewModel = getAllViewModels().find { it.hashCode() == viewModelId.code }
        if(viewModel == null){
            return null
        }
        return propertyParser.serializeFieldOfViewModel(viewModel,referencePath)
    }



    fun init(application: Application,config : FLConfig) {
        this.application = application
        this.propertyInterceptors = config.interceptors
        this.propertyParser = FLPropertyParserImpl()
        application.registerActivityLifecycleCallbacks(activityLifecycleCallback)
        startApiServer(application)
    }

    fun stopServer(){
        server?.stop()
    }

    internal fun unRegisterActivityCallback(callback: ActivityLifecycleCallbacks) {
        application?.registerActivityLifecycleCallbacks(callback)
    }


    internal fun onResumedActivity(activity: androidx.activity.ComponentActivity) {
        val viewModels = getAllViewModelsIds(activity)
        activityStore[activity] = viewModels
        updateFlow()
    }

    internal fun onDestroyActivity(activity: androidx.activity.ComponentActivity) {
        activityStore.remove(activity)
        updateFlow()
    }

    internal fun onResumedFragment(fragment: Fragment) {
        val viewModels = getAllViewModelsIds(fragment)
        fragmentStore[fragment] = viewModels
        updateFlow()
    }

    internal fun onDestroyFragment(fragment: Fragment) {
        fragmentStore.remove(fragment)
        updateFlow()
    }

    private fun updateFlow() {
        _viewModelFlow.value = getAllViewModelsIds()
    }

    private fun getAllViewModels() : List<ViewModel> {
        return (activityStore.values + fragmentStore.values).flatten() as List<ViewModel>
    }

    private fun getAllViewModelsIds(): List<FLViewModelId> {
        val viewModels = mutableListOf<FLViewModelId>()
        activityStore.forEach { entry ->
            val viewModelList = (entry.value as Collection<ViewModel>).map {
                FLViewModelId(it.hashCode(),
                    it.javaClass.simpleName,entry.key.javaClass.simpleName,entry.key.hashCode(),
                    FLViewModelOwnerType.Activity.name)
            }
            viewModels.addAll(viewModelList)
        }
        fragmentStore.forEach { entry ->
            val viewModelList = (entry.value as Collection<ViewModel>).map {
                FLViewModelId(it.hashCode(),
                    it.javaClass.simpleName,entry.key.javaClass.simpleName,entry.key.hashCode(),
                    FLViewModelOwnerType.Fragment.name)
            }
            viewModels.addAll(viewModelList)
        }
        return viewModels
    }

    internal fun getAllViewModelsIds(owner: ViewModelStoreOwner): List<ViewModel> {
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
                    return viewModelIdFlow.value.sortedBy { it.name }
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
                        return FLParserApiResponse(isSuccess = true, items = props.properties.sortedBy { it.name }, viewmodelName = props.name)
                    }
                    return FLParserApiResponse(isSuccess = false, items = emptyList(), viewmodelName = requestBody.name)
                }

            }).addRequestHandler(object :
                PostRequestHandler<FLReferencePath, Any>(FLReferencePath::class) {
                override fun getMethodName(): String {
                    return "getdetailsfromprop"
                }

                override fun onRequest(requestBody: FLReferencePath): Any {
                    val result = serializeFieldOfViewModel(requestBody)
                    return FLSerializeFieldResponse(isSuccess = result != null,result)
                }

            }).startWebApp(false).build()
        server?.start()
    }

}