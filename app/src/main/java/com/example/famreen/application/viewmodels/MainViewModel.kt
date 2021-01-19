package com.example.famreen.application.viewmodels

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.example.famreen.application.App
import com.example.famreen.states.States
import com.example.famreen.application.services.MainService
import com.example.famreen.utils.extensions.default

class MainViewModel {
    val state = MutableLiveData<States>().default(initialValue = States.DefaultState())

    fun startService(){
        val intentService = Intent(App.getAppContext(), MainService::class.java)
        App.getAppContext().startService(intentService)
    }
}