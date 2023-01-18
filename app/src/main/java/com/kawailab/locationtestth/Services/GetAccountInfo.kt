/*
* Created by 濱口　滋久
* 研究に使用するためのコード
* 収集する情報は任意の時間帯に使用されたアプリの名前、使用時間、最後に使用した時間
* 他の情報も追加するかも
*
* 収集したい情報があるなら、関数を追加すれば良い。
* */



package com.kawailab.locationtestth.Services

import android.app.usage.UsageEvents
import android.app.usage.UsageEvents.Event
import android.app.usage.UsageStatsManager
import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import com.kawailab.locationtestth.database.Usage
import com.kawailab.locationtestth.database.AppDatabase
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetAccountInfo(
context: Context,
workerParameters: WorkerParameters
):CoroutineWorker(context,workerParameters){
    override suspend fun doWork(): Result = withContext(Dispatchers.IO){
        try {
            getUsageStatsManager(applicationContext)
            val startTime = System.currentTimeMillis()
            val events = usageStatsManager!!.queryEvents(startTime,startTime-fixHour(BETWEEN))
            val usageList = toEventUsageList(events)
            val database = AppDatabase.getInstance(applicationContext)

            database.UsageDao().insert(usageList)
            Log.d(TAG,"success",)
            Result.success()
        }catch (ex:java.lang.Exception){
            Log.e(TAG,"Error",ex)
            Result.failure()
        }

    }
    private fun getUsageStatsManager(context: Context){
        usageStatsManager= ContextCompat.getSystemService(context,UsageStatsManager::class.java) as UsageStatsManager
    }


    private fun toEventUsageList(events:UsageEvents):List<Usage>{
        val list = mutableListOf<Usage>()
        var event = Event()
        while (events.hasNextEvent()){
            events.getNextEvent(event)
            if(checkUsefulData(event)){
                list.add(Usage(event.timeStamp,event.packageName,event.eventType))
            }
        }
        return list
    }

    private fun checkUsefulData(event:Event):Boolean{
        return event.eventType in usefulType
    }

    private fun fixMinutes(x:Int):Long{
        val v = x*60*1000
        return v.toLong()
    }
    private fun fixHour(x:Int):Long{
        val v = x*60*60*1000
        return v.toLong()
    }

    companion object{
        private const val TAG = "AccountInfoClass"
        private const val BETWEEN = 24
        private val usefulType = mutableListOf(
            Event.ACTIVITY_PAUSED,
            Event.ACTIVITY_RESUMED,
            Event.DEVICE_SHUTDOWN,
            Event.DEVICE_STARTUP,
            Event.SCREEN_INTERACTIVE,
            Event.SCREEN_NON_INTERACTIVE
        )
        private var  usageStatsManager: UsageStatsManager ?= null
    }






}