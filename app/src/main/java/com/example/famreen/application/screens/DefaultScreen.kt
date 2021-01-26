package com.example.famreen.application.screens

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import com.example.famreen.R
import com.example.famreen.application.logging.Logger
import com.example.famreen.application.preferences.AppPreferences
import com.example.famreen.databinding.ScreenTurnBinding
import com.example.famreen.states.ScreenStates
import io.reactivex.Observer

class DefaultScreen(private val mServiceContext: Context, val mObserver: Observer<ScreenStates>) {
    private var mIsMoved = false

    init{
        create()
    }
    @SuppressLint("ClickableViewAccessibility")
    private fun create() {
        val layoutInflater = LayoutInflater.from(mServiceContext)
        val binding = ScreenTurnBinding.inflate(layoutInflater)
        //set background color
        val drawable = mServiceContext.resources.getDrawable(R.drawable.selector_screens_background, null)
        drawable.colorFilter = PorterDuffColorFilter(AppPreferences.getProvider()!!.readScreensColor(), PorterDuff.Mode.SRC)
        binding.root.background = drawable
        //set Window params
        val myParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT)
        myParams.x = AppPreferences.getProvider()!!.readXTurnedScreenLocation()
        myParams.y = AppPreferences.getProvider()!!.readYTurnedScreenLocation()
        mObserver.onNext(ScreenStates.CreateState(binding.root,myParams))
        binding.btTurnOpen.setOnClickListener {
            if (!mIsMoved) {
                mObserver.onNext(ScreenStates.RemoveState(binding.root))
                open(AppPreferences.getProvider()!!.readLastScreen())
            }
            mIsMoved = false
        }
        try {
            val onTurnedTouchListener: View.OnTouchListener = object : View.OnTouchListener {
                private var initialX = 0
                private var initialY = 0
                private var initialTouchX = 0f
                private var initialTouchY = 0f
                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            initialX = myParams.x
                            initialY = myParams.y
                            initialTouchX = event.rawX
                            initialTouchY = event.rawY
                        }
                        MotionEvent.ACTION_MOVE -> {
                            myParams.x = initialX + (event.rawX - initialTouchX).toInt()
                            myParams.y = initialY + (event.rawY - initialTouchY).toInt()
                            mObserver.onNext(ScreenStates.UpdateState(binding.root,myParams))
                            mIsMoved = true
                        }
                        MotionEvent.ACTION_UP -> {
                            myParams.x = initialX + (event.rawX - initialTouchX).toInt()
                            myParams.y = initialY + (event.rawY - initialTouchY).toInt()
                            AppPreferences.getProvider()!!.writeXTurnedScreenLocation(myParams.x)
                            AppPreferences.getProvider()!!.writeYTurnedScreenLocation(myParams.y)
                        }
                    }
                    return false
                }
            }
            //for moving the picture on touch and slide
            binding.root.setOnTouchListener(onTurnedTouchListener)
            binding.btTurnOpen.setOnTouchListener(onTurnedTouchListener)
        } catch (e: Exception) {
            Logger.log(Log.ERROR,"default screen exception",e)
        }
    }
    private fun open(screen: String) {
        mObserver.onNext(ScreenStates.OpenState(screen))
    }
}