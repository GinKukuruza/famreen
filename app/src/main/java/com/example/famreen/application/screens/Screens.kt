package com.example.famreen.application.screens

import android.content.Context
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import com.example.famreen.R
import com.example.famreen.application.items.ScreensSpinnerItem
import com.example.famreen.application.logging.Logger
import com.example.famreen.application.preferences.AppPreferences
import com.example.famreen.states.ScreenStates
import io.reactivex.*
import io.reactivex.observers.DisposableObserver

class Screens(private val serviceContext: Context) {
    companion object{
        const val DIARY_SCREEN = "DIARY_SCREEN"
        const val SEARCH_SCREEN = "SEARCH_SCREEN"
        const val TRANSLATE_SCREEN = "TRANSLATE_SCREEN"
        const val DEFAULT_SCREEN = "TURN_SCREEN"
    }

    private lateinit var observer: Observer<ScreenStates>
    private lateinit var screensListener: AdapterView.OnItemSelectedListener
    private var currentView: View? = null
    private val windowManager: WindowManager = serviceContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    init{
        initObserver()
        initSpinnerListener()
        start()
    }
    private fun start(){
        observer.onNext(ScreenStates.OpenState(DEFAULT_SCREEN))
    }
    private fun initObserver(){
        observer = object : DisposableObserver<ScreenStates>() {
            override fun onNext(it: ScreenStates) {
                when(it){
                    is ScreenStates.CreateState ->{
                        createView(it)
                    }
                    is ScreenStates.RemoveState ->{
                        removeView(it)
                    }
                    is ScreenStates.UpdateState ->{
                        updateView(it)
                    }
                    is ScreenStates.OpenState ->{
                        openScreen(it.screen)
                    }
                }
            }

            override fun onError(e: Throwable) {
                Logger.log(9, "init screens core exception", e)
            }

            override fun onComplete() {

            }
        }
    }
    private fun openScreen(screen: String){
        when (screen) {
            DIARY_SCREEN -> DiaryScreen(serviceContext,observer, screensListener)
            SEARCH_SCREEN -> SearchScreen(serviceContext,observer, screensListener)
            TRANSLATE_SCREEN -> TranslationScreen(serviceContext,observer, screensListener)
            DEFAULT_SCREEN -> DefaultScreen(serviceContext,observer)
        }
    }
    private fun createView(it: ScreenStates.CreateState){
        currentView = it.view
        windowManager.addView(currentView,it.params)
    }
    private fun removeView(it: ScreenStates.RemoveState){
        currentView = it.view
        windowManager.removeViewImmediate(currentView)
    }
    private fun updateView(it: ScreenStates.UpdateState){
        currentView = it.view
        windowManager.updateViewLayout(currentView,it.params)
    }

    private fun initSpinnerListener(){
        screensListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val screensSpinnerItem = parent.getItemAtPosition(position) as ScreensSpinnerItem
                when (screensSpinnerItem.image) {
                    R.drawable.img_screens_diary -> {
                        AppPreferences.getProvider()!!.writeLastScreen(DIARY_SCREEN)
                        observer.onNext(ScreenStates.RemoveState(currentView as View))
                        observer.onNext(ScreenStates.OpenState(DIARY_SCREEN))
                    }
                    R.drawable.img_screens_search -> {
                        AppPreferences.getProvider()!!.writeLastScreen(SEARCH_SCREEN)
                        observer.onNext(ScreenStates.RemoveState(currentView as View))
                        observer.onNext(ScreenStates.OpenState(SEARCH_SCREEN))
                    }
                    R.drawable.img_screens_translate -> {
                        AppPreferences.getProvider()!!.writeLastScreen(TRANSLATE_SCREEN)
                        observer.onNext(ScreenStates.RemoveState(currentView as View))
                        observer.onNext(ScreenStates.OpenState(TRANSLATE_SCREEN))
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
}