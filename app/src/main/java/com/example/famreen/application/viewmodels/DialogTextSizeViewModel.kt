package com.example.famreen.application.viewmodels

import androidx.lifecycle.MutableLiveData
import com.example.famreen.states.States
import com.example.famreen.utils.default

class DialogTextSizeViewModel {
    val state = MutableLiveData<States>().default(initialValue = States.DefaultState())
}