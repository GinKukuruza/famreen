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

class Screens(private val mServiceContext: Context) {
    companion object{
        const val DIARY_SCREEN = "DIARY_SCREEN"
        const val SEARCH_SCREEN = "SEARCH_SCREEN"
        const val TRANSLATE_SCREEN = "TRANSLATE_SCREEN"
        const val DEFAULT_SCREEN = "TURN_SCREEN"
    }

    private lateinit var mObserver: Observer<ScreenStates>
    private lateinit var mScreensListener: AdapterView.OnItemSelectedListener
    private var mCurrentView: View? = null
    private val mWindowManager: WindowManager = mServiceContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    init{
        initObserver()
        initSpinnerListener()
        start()
    }
    private fun start(){
        mObserver.onNext(ScreenStates.OpenState(DEFAULT_SCREEN))
    }
    private fun initObserver(){
        mObserver = object : DisposableObserver<ScreenStates>() {
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
            DIARY_SCREEN -> DiaryScreen(mServiceContext,mObserver, mScreensListener)
            SEARCH_SCREEN -> SearchScreen(mServiceContext,mObserver, mScreensListener)
            TRANSLATE_SCREEN -> TranslationScreen(mServiceContext,mObserver, mScreensListener)
            DEFAULT_SCREEN -> DefaultScreen(mServiceContext,mObserver)
        }
    }
    private fun createView(it: ScreenStates.CreateState){
        mCurrentView = it.view
        mWindowManager.addView(mCurrentView,it.params)
    }
    private fun removeView(it: ScreenStates.RemoveState){
        mCurrentView = it.view
        mWindowManager.removeViewImmediate(mCurrentView)
    }
    private fun updateView(it: ScreenStates.UpdateState){
        mCurrentView = it.view
        mWindowManager.updateViewLayout(mCurrentView,it.params)
    }

    private fun initSpinnerListener(){
        mScreensListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val screensSpinnerItem = parent.getItemAtPosition(position) as ScreensSpinnerItem
                when (screensSpinnerItem.mImage) {
                    R.drawable.img_screens_diary -> {
                        AppPreferences.getProvider()!!.writeLastScreen(DIARY_SCREEN)
                        mObserver.onNext(ScreenStates.RemoveState(mCurrentView as View))
                        mObserver.onNext(ScreenStates.OpenState(DIARY_SCREEN))
                    }
                    R.drawable.img_screens_search -> {
                        AppPreferences.getProvider()!!.writeLastScreen(SEARCH_SCREEN)
                        mObserver.onNext(ScreenStates.RemoveState(mCurrentView as View))
                        mObserver.onNext(ScreenStates.OpenState(SEARCH_SCREEN))
                    }
                    R.drawable.img_screens_translate -> {
                        AppPreferences.getProvider()!!.writeLastScreen(TRANSLATE_SCREEN)
                        mObserver.onNext(ScreenStates.RemoveState(mCurrentView as View))
                        mObserver.onNext(ScreenStates.OpenState(TRANSLATE_SCREEN))
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
}