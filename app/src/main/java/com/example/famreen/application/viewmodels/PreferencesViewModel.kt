package com.example.famreen.application.viewmodels

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.example.famreen.states.States
import com.example.famreen.utils.extensions.default

class PreferencesViewModel {
    private val mState = MutableLiveData<States>().default(initialValue = States.DefaultState())
    /**
     * Вызывается для создания intent, с помощью которого можно поделиться приложением
     * **/
    fun createShareIntent(): Intent{
        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/plain"
        share.putExtra(Intent.EXTRA_SUBJECT, "Hey, it's FAMREEN")
        share.putExtra(Intent.EXTRA_TEXT, "I'am happy to use this app, try it out ! 'link to app' ")
        return share
    }
    /**
     * **/
    fun getState() = mState
}