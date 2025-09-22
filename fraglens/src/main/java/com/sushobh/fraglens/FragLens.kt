package com.sushobh.fraglens

import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavGraph
import com.ranrings.libs.androidapptorest.AndroidRestServer
import com.ranrings.libs.androidapptorest.Base.GetRequestHandler
import com.ranrings.libs.androidapptorest.Base.PostRequestHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.navigation.fragment.NavHostFragment
import kotlin.system.measureTimeMillis
import kotlin.time.measureTimedValue

object FragLens : FragLensApi {
    internal var propertyInterceptors : List<FLPropertyParserInterceptor> = arrayListOf()
    private val port = 56440;
    private var application: Application? = null
    private val activityStore = HashMap<ComponentActivity, List<Any>>()
    private val fragmentStore = HashMap<Fragment, List<Any>>()
    private var server: AndroidRestServer? = null
    private val _viewModelFlow = MutableStateFlow(emptyList<FLViewModelId>())
    private lateinit var propertyParser : FLPropertyParser
    override val viewModelIdFlow = _viewModelFlow.asStateFlow()
    private var listener : FLCurrentActivityListener? = null

    override fun parseProperties(flViewModelId: FLViewModelId): FLPropertyOwner? {
        val allViewModelStore = getAllViewModelsIds()
        val viewModelId =
            allViewModelStore.find { it.code == flViewModelId.code } ?: return null.also {
                FLLogger.log("Returning null because could not find viewmodel id")
            }
        val viewModel = getAllViewModels().find { it.hashCode() == viewModelId.code }
        if(viewModel == null){
            FLLogger.log("Returning null because could not find viewmodel based on id")
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


    fun setActivityLifecycleListener(listener: FLCurrentActivityListener){
        this.listener = listener
    }

    fun init(application: Application,config : FLConfig) {
        this.application = application
        this.propertyInterceptors = config.interceptors
        this.propertyParser = FLPropertyParserImpl()
        application.registerActivityLifecycleCallbacks(activityLifecycleCallback)
        startApiServer(application)
    }

    fun stop(){
        server?.stop()
        unRegisterActivityCallback(activityLifecycleCallback)
    }

    internal fun unRegisterActivityCallback(callback: ActivityLifecycleCallbacks) {
        application?.unregisterActivityLifecycleCallbacks(callback)
    }


    internal fun onResumedActivity(activity: ComponentActivity) {
        activityStore[activity] = mutableListOf<Any>()
        updateFlow()
        listener?.onResumed(activity)
        updateFlow()
    }

    internal fun onPausedActivity(activity: ComponentActivity) {
        listener?.onPaused(activity)
    }

    internal fun onDestroyActivity(activity: androidx.activity.ComponentActivity) {
        activityStore.remove(activity)
        updateFlow()
    }

    internal fun onResumedFragment(fragment: Fragment) {
        fragmentStore[fragment] = mutableListOf<Any>()
        updateFlow()
    }

    internal fun onDestroyFragment(fragment: Fragment) {
        fragmentStore.remove(fragment)
        updateFlow()
    }

    private fun updateFlow() {
        activityStore.forEach {
            val viewModels = getAllViewModels(it.key)
            activityStore[it.key] = viewModels
        }
        fragmentStore.forEach {
            val viewModels = getAllViewModels(it.key)
            val navGraphBasedViewModels = getNavGraphViewModelStoreOwner(it.key)?.run { getAllViewModels(this) } ?: emptyList()
            fragmentStore[it.key] = viewModels.toMutableList()+navGraphBasedViewModels
        }
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
                    val (resp,time) = measureTimedValue {
                        updateFlow()
                        viewModelIdFlow.value.sortedBy { it.name }
                    }

                    FLLogger.log("getallviewmodels took ${time} millis")
                    return resp
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
                    val (response,timeTaken) =  measureTimedValue  {
                         val props = parseProperties(requestBody)
                         if (props != null) {
                              FLParserApiResponse(isSuccess = true, items = props.properties.sortedBy { it.name }, viewmodelName = props.name)
                         }
                         else {
                             FLParserApiResponse(isSuccess = false, items = emptyList(), viewmodelName = requestBody.name)
                         }
                     }
                    FLLogger.log("getdetailsfromprop took ${timeTaken} millis")
                    return response
                }

            }).addRequestHandler(object :
                PostRequestHandler<FLReferencePath, Any>(FLReferencePath::class) {
                override fun getMethodName(): String {
                    return "getdetailsfromprop"
                }

                override fun onRequest(requestBody: FLReferencePath): Any {
                    val (resp,time) = measureTimedValue {
                        val result = serializeFieldOfViewModel(requestBody)
                        FLSerializeFieldResponse(isSuccess = result != null,result)
                    }
                    FLLogger.log("getdetailsfromprop took ${time} millis")
                    return resp
                }

            }).startWebApp(false).build()
        server?.start()
    }


    fun getNavGraphViewModelStoreOwner(fragment: Fragment): ViewModelStoreOwner? {
        return try {
            val navController = NavHostFragment.findNavController(fragment)

            // Find the nearest NavGraph parent of this destination
            val destination = navController.currentBackStackEntry?.destination
            val parentGraph = generateSequence(destination) { it.parent }
                .firstOrNull { it is NavGraph } as? NavGraph

            // If found, return its ViewModelStoreOwner
            parentGraph?.let { navController.getViewModelStoreOwner(it.id) }
        } catch (e: Exception) {
            FLLogger.log("No navgraph ViewModelStoreOwner for ${fragment::class.java.simpleName}: ${e.message}")
            null
        }
    }

}