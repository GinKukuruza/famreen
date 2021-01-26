package com.example.famreen.application.viewmodels

import androidx.lifecycle.MutableLiveData
import com.example.famreen.states.States
import com.example.famreen.utils.extensions.default

class DialogTextSizeViewModel {
    private val mState = MutableLiveData<States>().default(initialValue = States.DefaultState())
    /**
     * **/
    fun getState() = mState
}