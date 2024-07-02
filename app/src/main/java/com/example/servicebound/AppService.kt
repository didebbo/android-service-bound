package com.example.servicebound


import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class AppService: Service() {

    private var threadID: Long? = null
    private  val hasThread: Boolean get() = threadID != null

    private val _randomNumber: MutableLiveData<Int> = MutableLiveData<Int>(0)
    val randomNumber: LiveData<Int> = _randomNumber

    private val _messageLog: MutableLiveData<String> = MutableLiveData()
    val messageLog: LiveData<String> = _messageLog

    inner class AppServiceBinder: Binder() {
        fun getService(): AppService {
            return this@AppService
        }
    }

    private val myServiceBinder: AppServiceBinder = AppServiceBinder()

    override fun onCreate() {
        super.onCreate()
        println("[GN] onCreate AppService")
    }

    override fun onBind(intent: Intent?): IBinder {
        println("[GN] onBind AppService")
        return  myServiceBinder
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        println("[GN] onRebind AppService")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        println("[GN] onUnbind AppService")
        return super.onUnbind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(!hasThread) {
            println("[GN] onStartCommand AppService")
            Thread{
                threadID = Thread.currentThread().id
                println("[GN] Start AppService on Thread $threadID")
                _messageLog.postValue("Start AppService on Thread $threadID")
                while (hasThread) {
                    _randomNumber.apply {
                        postValue(value?.plus(1))
                    }
                    println("[GN] Generated randomNumber: ${_randomNumber.value}")
                    Thread.sleep(2000)
                }
                stopSelf()
            }.start()
        }
        return START_STICKY
    }

    override fun stopService(name: Intent?): Boolean {
        if(hasThread) {
            println("[GN] Stop AppService on Thread $threadID")
            pushMessageLog("Stop AppService on Thread $threadID")
            threadID = null
        }
        return super.stopService(name)
    }

    override fun onDestroy() {
        println("[GN] onDestroy AppService")
        super.onDestroy()
    }

    fun pushMessageLog(message: String) {
        _messageLog.value = message
    }
}