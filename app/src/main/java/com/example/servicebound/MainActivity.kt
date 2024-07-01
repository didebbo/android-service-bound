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
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                appService = null
                _isAppServiceBound.postValue(false)
            }
        }

        updateTextPreview()

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

    private fun updateTextPreview() {
        isAppServiceBound.observe(this) { isAppServiceBound ->
            println("[GN] updateTextPreview isAppServiceBound $isAppServiceBound")
            when(isAppServiceBound) {
                true -> appService?.randomNumber?.observe(this) {
                    bindingView.previewTextView.text = it.toString()
                }
                false -> bindingView.previewTextView.text = "Service Not Found"
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
            println("[GN] bindAppService")
            bindService(appServiceIntent, appServiceConnection,Context.BIND_AUTO_CREATE)
        }
    }

    private fun unbindAppService() {
        if(isAppServiceBound.value == true) {
            println("[GN] unbindAppService")
            appService?.randomNumber?.removeObservers(this)
            unbindService(appServiceConnection)
            _isAppServiceBound.postValue(false)
            appService = null
        }
    }
}