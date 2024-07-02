package com.example.servicebound

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.servicebound.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var bindingView: ActivityMainBinding
    private lateinit var appServiceIntent: Intent
    private var appService: AppService? = null
    private val _isAppServiceBound: MutableLiveData<Boolean> = MutableLiveData(false)
    private  val isAppServiceBound: LiveData<Boolean> = _isAppServiceBound


    private lateinit  var appServiceConnection:  ServiceConnection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingView = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingView.root)

        appServiceIntent = Intent(applicationContext,AppService::class.java)
        appServiceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as AppService.AppServiceBinder
                appService = binder.getService()
                _isAppServiceBound.postValue(true)
                println("[GN] Bind AppService from Thread ${Thread.currentThread().id}")
                appService?.pushMessageLog("Bind AppService from Thread ${Thread.currentThread().id}")
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                appService = null
                _isAppServiceBound.postValue(false)
            }
        }

        bindAppServiceObservable()

        bindingView.startButton.setOnClickListener {
            startAppService()
        }
        bindingView.stopButton.setOnClickListener {
            stopAppService()
        }
        bindingView.bindButton.setOnClickListener {
            bindAppService()
        }
        bindingView.unbindButton.setOnClickListener {
            unbindAppService()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindAppService()
    }

    private fun bindAppServiceObservable() {
        isAppServiceBound.observe(this) { isAppServiceBound ->
            when(isAppServiceBound) {
                true -> createAppServiceObservables()
                false -> bindingView.previewTextView.text = "Unbound AppService"
            }
        }
    }
    private fun startAppService() {
        appService?.startService(appServiceIntent)
    }

    private fun stopAppService() {
        appService?.stopService(appServiceIntent)
    }
    private fun bindAppService() {
        if(isAppServiceBound.value == false) {
            bindService(appServiceIntent, appServiceConnection,Context.BIND_AUTO_CREATE)
        }
    }

    private fun unbindAppService() {
        if(isAppServiceBound.value == true) {
            appService?.pushMessageLog("Unbind AppService")
            removeAppServiceObservables()
            unbindService(appServiceConnection)
            appService = null
            _isAppServiceBound.postValue(false)
        }
    }

    private fun createAppServiceObservables() {
        appService?.randomNumber?.observe(this) {
            bindingView.previewTextView.text = it.toString()
        }
        appService?.messageLog?.observe(this) {
            Snackbar.make(bindingView.root,it,Snackbar.LENGTH_SHORT).show()
        }
    }

    private  fun  removeAppServiceObservables() {
        appService?.randomNumber?.removeObservers(this)
        appService?.messageLog?.removeObservers(this)
    }
}