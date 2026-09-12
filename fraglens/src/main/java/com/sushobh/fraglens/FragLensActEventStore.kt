package com.sushobh.fraglens

import android.util.Log
import androidx.constraintlayout.widget.Group
import com.sushobh.activitytracker.lib.ActEvent
import com.sushobh.activitytracker.lib.ActTracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch



object FLNActEventStore {
    val scope = CoroutineScope(Dispatchers.Default)
    private val map = HashMap<String, HashSet<String>>()
    val getActState : Map<String, HashSet<String>> get() = map.filter { it.key in  arrayOf("onResumed","onPaused","onStopped","onStarted") }
    fun start(){
         scope.launch {
              while(true){
                  val items = ActTracker.getEventsForApi().reversed()
                  items.forEach {
                      addItemToGroup(it)
                  }
                  delay(500)
              }
         }
    }

    private fun addItemToGroup(item : ActEvent) = try{
        val lifecycleItem = item.data.split("=>").get(0).trim()
        val lifecycleState = item.data.split("=>").get(1).trim()
        for(entry in this.map) {
            if(entry.value.contains(lifecycleItem)){
                entry.value.remove(lifecycleItem)
            }
        }
        if(!map.contains(lifecycleState)){
            map.put(lifecycleState,hashSetOf())
        }
        map.get(lifecycleState)!!.add(lifecycleItem)
        Unit
    }catch (e : Exception) {
          e.printStackTrace()
    }
}