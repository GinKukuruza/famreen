package com.example.famreen.application

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import com.example.famreen.BuildConfig
import com.example.famreen.application.di.AppComponent
import com.example.famreen.application.di.DaggerAppComponent
import com.example.famreen.application.interfaces.YandexTranslateRepository
import com.example.famreen.application.preferences.AppPreferences
import com.firebase.client.Firebase
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject

class App : Application() {
    companion object {
        private lateinit var mContext: Context
        val appComponent: AppComponent =  DaggerAppComponent.create()
        /**
         * **/
        fun getAppContext(): Context{
            return mContext
        }
    }
    @Inject lateinit var translateRepositoryImpl: YandexTranslateRepository
    override fun onCreate() {
        super.onCreate()
        init()
    }
    private fun init(){
        mContext = applicationContext
        appComponent.inject(this@App)
        initFirebase()
        initCrashlytics()
        main()
    }
    private fun main(){
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks{
            @SuppressLint("SourceLockedOrientationActivity")
            override fun onActivityCreated(p0: Activity?, p1: Bundle?) {
                p0?.let {
                    it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }
            }
            override fun onActivityStarted(p0: Activity?) {}
            override fun onActivityResumed(p0: Activity?) {}
            override fun onActivityPaused(p0: Activity?) {}
            override fun onActivityStopped(p0: Activity?) {}
            override fun onActivitySaveInstanceState(p0: Activity?, p1: Bundle?) {}
            override fun onActivityDestroyed(p0: Activity?) {}
        })
        translateRepositoryImpl.setUpLanguages()
        checkFirstRun()
    }
    private fun initCrashlytics(){
        //TODO set debug type
        if(BuildConfig.DEBUG && BuildConfig.BUILD_TYPE == "debug"){
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false)
        }else{
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
            FirebaseCrashlytics.getInstance().setUserId(AdvertisingIdClient.getAdvertisingIdInfo(this).id)
        }
    }
    private fun initFirebase() {
        Firebase.setAndroidContext(this)
        if (FirebaseApp.getApps(this).isNotEmpty()) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        }
    }
    private fun checkFirstRun(){
        val currentVersionCode = BuildConfig.VERSION_CODE
        val savedVersionCode = AppPreferences.getProvider()!!.readVersionCode()
        when {
            currentVersionCode == savedVersionCode -> {

            }
            savedVersionCode == AppPreferences.INT_DEFAULT_VALUE -> {

            }
            savedVersionCode < currentVersionCode -> {

            }
        }
        AppPreferences.getProvider()!!.writeVersionCode(currentVersionCode)
    }
}