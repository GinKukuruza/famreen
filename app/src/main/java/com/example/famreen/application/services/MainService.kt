package com.example.famreen.application.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.famreen.application.screens.Screens


class MainService : Service() {
    override fun onCreate() {
        Screens(baseContext)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}
