package com.example.servicebound


import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlin.random.Random

class AppService: Service() {

    private var threadID: Long = -1
    private var _randomNumber: MutableLiveData<Int> = MutableLiveData<Int>(0)
    val randomNumber: LiveData<Int> get() = _randomNumber

    private  var isRunning: Boolean = false

    inner class AppServiceBinder: Binder() {
        fun getService(): AppService {
            return this@AppService
        }
    }

    private val myServiceBinder: AppServiceBinder = AppServiceBinder()

    override fun onCreate() {
        super.onCreate()
        println("[GN] onCreate MyService on Thread $threadID")
    }

    override fun onBind(intent: Intent?): IBinder? {
        println("[GN] onBind MyService on Thread $threadID")
        return  myServiceBinder
    }

    override fun onRebind(intent: Intent?) {
        println("[GN] onRebind MyService on Thread $threadID")
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        println("[GN] onUnbind MyService on Thread $threadID")
        return super.onUnbind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(!isRunning) {
            println("[GN] onStartCommand MyService on Thread $threadID")
            isRunning = true
            Thread{
                threadID = Thread.currentThread().id
                println("[GN] Running Thread $threadID")
                while (isRunning) {
                    _randomNumber.apply {
                        postValue(value?.plus(1))
                    }
                    println("[GN] Generated randomNumber: ${_randomNumber.value} on Thread $threadID")
                    Thread.sleep(2000)
                }
                stopSelf()
            }.start()
        }
        return START_STICKY
    }

    override fun stopService(name: Intent?): Boolean {
        if(isRunning) {
            println("[GN] stopService MyService on Thread $threadID")
            isRunning = false
        }
        return super.stopService(name)
    }

    override fun onDestroy() {
        println("[GN] onDestroy MyService on Thread $threadID")
        super.onDestroy()
    }
}