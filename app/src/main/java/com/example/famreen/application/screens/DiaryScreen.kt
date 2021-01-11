package com.example.famreen.application.screens

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import com.example.famreen.R
import com.example.famreen.application.App
import com.example.famreen.application.adapters.ScreensSpinnerAdapter
import com.example.famreen.application.items.NoteItem
import com.example.famreen.application.items.ScreensSpinnerItem
import com.example.famreen.application.preferences.AppPreferences.Companion.getProvider
import com.example.famreen.application.room.repositories.DiaryRoomRepository
import com.example.famreen.databinding.ScreenDiaryBinding
import com.example.famreen.network.DiaryRepository
import com.example.famreen.states.RoomStates
import com.example.famreen.states.ScreenStates
import com.example.famreen.utils.Utils.getNoteTime
import io.reactivex.Observer
import io.reactivex.observers.DisposableObserver

class DiaryScreen(private val serviceContext: Context, val observer: Observer<ScreenStates>, private val screensListener: AdapterView.OnItemSelectedListener) : ScreenInit {
    private val diaryRoomRepository = DiaryRoomRepository(DiaryRepository())
    private lateinit var screensSpinnerAdapter: ScreensSpinnerAdapter
    private var tempCurTheme = ""
    private var tempCurDesc = ""
    private val tempCurTag = ""
    private var tempIsCurImportant = false

    init{
        initScreensSpinner()
        create()
    }

    override fun create(){
        val layoutInflater = LayoutInflater.from(serviceContext)
        val binding = ScreenDiaryBinding.inflate(layoutInflater)
        val drawable = serviceContext.resources.getDrawable(R.drawable.selector_screens_background, null)

        drawable.colorFilter = PorterDuffColorFilter(getProvider()!!.readScreensColor(), PorterDuff.Mode.SRC)
        binding.root.background = drawable
        //Create WindowManager Params
        val myParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN,
            PixelFormat.TRANSLUCENT)
        myParams.gravity = Gravity.TOP or Gravity.CENTER
        myParams.x = 0
        myParams.y = 80
        //Custom Spinner adding
        val spinner = binding.spinnerDairyChoice
        spinner.adapter = screensSpinnerAdapter
        spinner.isSelected = false //TODO Solve
        spinner.setSelection(0, true)
        spinner.onItemSelectedListener = screensListener
        //Add ll as View to WindowManager
        observer.onNext(ScreenStates.CreateState(binding.root,myParams))
        //Set SAVED temp values
        binding.etDiaryDescription.setText(tempCurDesc)
        binding.etDiaryTheme.setText(tempCurTheme)
        binding.etDiaryTag.setText(tempCurTag)
        if (tempIsCurImportant) {
            binding.btDiaryIsImportant.setImageResource(R.drawable.img_screens_star)
        } else {
            binding.btDiaryIsImportant.setImageResource(R.drawable.img_screens_star_empty)
        }
        //ImageButton change important state
        binding.btDiaryIsImportant.setOnClickListener { v: View ->
            val animImportant: Animation
            if (!tempIsCurImportant) {
                animImportant = AnimationUtils.loadAnimation(App.getAppContext(), R.anim.bt_is_important)
                v.startAnimation(animImportant) //TODO
                binding.btDiaryIsImportant.setImageResource(R.drawable.img_screens_star)
                tempIsCurImportant = true
            } else {
                animImportant = AnimationUtils.loadAnimation(App.getAppContext(), R.anim.bt_is_not_important)
                v.startAnimation(animImportant) //TODO
                binding.btDiaryIsImportant.setImageResource(R.drawable.img_screens_star_empty)
                tempIsCurImportant = false
            }
        }
        //SAVE temp values from pop-up screen
        binding.btDiarySwap.setOnClickListener {
            observer.onNext(ScreenStates.RemoveState(binding.root))
            tempCurDesc = binding.etDiaryDescription.text.toString() //TODO CHANGE
            tempCurTheme = binding.etDiaryTheme.text.toString()
            observer.onNext(ScreenStates.OpenState(Screens.DEFAULT_SCREEN))
        }
        //SAVE data to SQLite DB
        binding.btDiaryGo.setOnClickListener {
            //Note item creating
            val item = NoteItem()
            item.description = binding.etDiaryDescription.text.toString()
            item.tag = binding.etDiaryTag.text.toString()
            item.time = getNoteTime()
            item.important = tempIsCurImportant
            item.title = binding.etDiaryTheme.text.toString()
            diaryRoomRepository.insertNote(item)
            observer.onNext(ScreenStates.OpenState(Screens.DEFAULT_SCREEN))
            observer.onNext(ScreenStates.RemoveState(binding.root))
        }
        //Drawing and creating pop-up screen
        try {
            val onUnturnedTouchListener: View.OnTouchListener = object : View.OnTouchListener {
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
                        MotionEvent.ACTION_UP -> {
                        }
                        MotionEvent.ACTION_MOVE -> {
                            myParams.x = initialX + (event.rawX - initialTouchX).toInt()
                            myParams.y = initialY + (event.rawY - initialTouchY).toInt()
                            observer.onNext(ScreenStates.UpdateState(binding.root,myParams))
                        }
                    }
                    return false
                }
            }
            //For multiTouching of the popup Views of ll
            binding.root.setOnTouchListener(onUnturnedTouchListener)
            binding.spinnerDairyChoice.setOnTouchListener(onUnturnedTouchListener)
            binding.btDiarySwap.setOnTouchListener(onUnturnedTouchListener)
            binding.etDiaryDescription.setOnTouchListener(onUnturnedTouchListener)
            binding.etDiaryTheme.setOnTouchListener(onUnturnedTouchListener)
            binding.etDiaryTag.setOnTouchListener(onUnturnedTouchListener)
            binding.btDiaryGo.setOnTouchListener(onUnturnedTouchListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun initScreensSpinner() {
        val arrayList = ArrayList<ScreensSpinnerItem>()
        arrayList.add(ScreensSpinnerItem(R.drawable.img_screens_diary))
        arrayList.add(ScreensSpinnerItem(R.drawable.img_screens_search))
        arrayList.add(ScreensSpinnerItem(R.drawable.img_screens_translate))
        screensSpinnerAdapter = ScreensSpinnerAdapter(serviceContext, arrayList)
    }
}