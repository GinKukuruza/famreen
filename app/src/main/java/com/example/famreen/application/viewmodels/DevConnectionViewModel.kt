package com.example.famreen.application.viewmodels

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.example.famreen.states.States
import com.example.famreen.utils.extensions.default
import com.example.famreen.utils.extensions.set

class DevConnectionViewModel {
    val state = MutableLiveData<States>().default(initialValue = States.DefaultState())

    fun createSendIntent(title: String, description: String): Intent?{
        if (title == "" || description == "") {
            state.set(States.ErrorState("Заполните поля"))
            return null
        }
        val send = Intent(Intent.ACTION_SEND)
        send.putExtra(Intent.EXTRA_SUBJECT, title)
        send.putExtra(Intent.EXTRA_TEXT, description)
        send.putExtra(Intent.EXTRA_EMAIL, arrayOf("gin.tasanka@gmail.com"))
        send.type = "application/octet-stream"
        return send
    }
}