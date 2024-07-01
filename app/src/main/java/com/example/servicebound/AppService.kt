package com.example.servicebound

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class AppService: Service() {

    private var number: Int = 0
    private var isBind: Boolean = false
    private  var isRunning: Boolean = false

    class MyServiceBinder: Binder() {
        fun getService(): AppService {
            return AppService()
        }
    }

    private val myServiceBinder: MyServiceBinder = MyServiceBinder()

    override fun onCreate() {
        super.onCreate()
        println("[GN] onCreate MyService")
    }

    override fun onBind(intent: Intent?): IBinder? {
        println("[GN] onBind MyService")
        isBind = true
        return  myServiceBinder
    }

    override fun onRebind(intent: Intent?) {
        println("[GN] onRebind MyService")
        isBind = true
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        println("[GN] onUnbind MyService")
        isBind = false
        return super.onUnbind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("[GN] onStartCommand MyService")
        isRunning = true
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        println("[GN] onDestroy MyService")
        isRunning = false
        super.onDestroy()
    }
}