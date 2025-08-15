package com.sushobh.fraglens

import androidx.lifecycle.MutableLiveData
import com.sushobh.fraglens.interceptors.FLDataclassInterceptor
import com.sushobh.fraglens.interceptors.FLLiveDataInterceptor
import com.sushobh.fraglens.interceptors.FLPrimitveInterceptor
import com.sushobh.fraglens.interceptors.FLStateFlowInterceptor
import com.sushobh.fraglens.serializers.FLDataClassSerialzer
import com.sushobh.fraglens.serializers.FLPrimitveSerialzer
import kotlinx.coroutines.flow.MutableStateFlow

internal class FLPropertyParserImpl : FLPropertyParser{

    private val flPrimitveSerialzer = FLPrimitveSerialzer()
    private val flDataClassSerialzer = FLDataClassSerialzer()
    private val primitiveInterceptor = FLPrimitveInterceptor(flPrimitveSerialzer)
    private val dataClassInterceptor = FLDataclassInterceptor(flDataClassSerialzer)
    private val liveDataInterceptor = FLLiveDataInterceptor(flDataClassSerialzer,flPrimitveSerialzer)
    private val stateFlowInterceptor = FLStateFlowInterceptor(flDataClassSerialzer,flPrimitveSerialzer)
    
    override fun parseProperties(owner: Any): FLPropertyOwner {
        val properties = mutableListOf<FLProperty>()
        val clazz = owner::class.java

        clazz.declaredFields.forEach { field ->

            val interceptors = FragLens.propertyInterceptors.toMutableList().apply {
                add(stateFlowInterceptor)
                add(liveDataInterceptor)
                add(primitiveInterceptor)
                add(dataClassInterceptor)
            }

            for(interceptor in interceptors){
                val parsedProperty = interceptor.intercept(owner,field)
                if(parsedProperty != null){
                    properties.add(parsedProperty)
                    return@forEach
                }
            }
        }

        return FLPropertyOwner(
            name = clazz.simpleName,
            type = clazz.name,
            properties = properties,
            ownerObject = owner
        )
    }

    override fun refresh(propertyOwner: FLPropertyOwner): FLPropertyOwner {
        return parseProperties(propertyOwner.ownerObject ?: return propertyOwner)
    }

}