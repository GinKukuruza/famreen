package com.example.famreen.application.screens

import android.annotation.SuppressLint
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
import com.example.famreen.application.interfaces.DiaryRoomRepository
import com.example.famreen.application.items.NoteItem
import com.example.famreen.application.items.ScreensSpinnerItem
import com.example.famreen.application.logging.Logger
import com.example.famreen.application.preferences.AppPreferences.Companion.getProvider
import com.example.famreen.application.interfaces.ScreenInit
import com.example.famreen.databinding.ScreenDiaryBinding
import com.example.famreen.states.ScreenStates
import com.example.famreen.utils.Utils.getNoteTime
import io.reactivex.Observer
import javax.inject.Inject

class DiaryScreen(private val mServiceContext: Context, val mObserver: Observer<ScreenStates>, private val mScreensListener: AdapterView.OnItemSelectedListener) : ScreenInit {
    @Inject lateinit var mDiaryRoomRepository: DiaryRoomRepository
    private lateinit var mScreensSpinnerAdapter: ScreensSpinnerAdapter
    private var mTempCurTheme = ""
    private var mTempCurDesc = ""
    private val mTempCurTag = ""
    private var mTempIsCurImportant = false

    init{
        App.appComponent.inject(this@DiaryScreen)
        initScreensSpinner()
        create()
    }
    @SuppressLint("ClickableViewAccessibility")
    override fun create(){
        val layoutInflater = LayoutInflater.from(mServiceContext)
        val binding = ScreenDiaryBinding.inflate(layoutInflater)
        val drawable = mServiceContext.resources.getDrawable(R.drawable.selector_screens_background, null)

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
        spinner.adapter = mScreensSpinnerAdapter
        spinner.isSelected = false //TODO Solve
        spinner.setSelection(0, true)
        spinner.onItemSelectedListener = mScreensListener
        //Add ll as View to WindowManager
        mObserver.onNext(ScreenStates.CreateState(binding.root,myParams))
        //Set SAVED temp values
        binding.etDiaryDescription.setText(mTempCurDesc)
        binding.etDiaryTheme.setText(mTempCurTheme)
        binding.etDiaryTag.setText(mTempCurTag)
        if (mTempIsCurImportant) {
            binding.btDiaryIsImportant.setImageResource(R.drawable.img_screens_star)
        } else {
            binding.btDiaryIsImportant.setImageResource(R.drawable.img_screens_star_empty)
        }
        //ImageButton change important state
        binding.btDiaryIsImportant.setOnClickListener { v: View ->
            val animImportant: Animation
            if (!mTempIsCurImportant) {
                animImportant = AnimationUtils.loadAnimation(App.getAppContext(), R.anim.bt_is_important)
                v.startAnimation(animImportant) //TODO
                binding.btDiaryIsImportant.setImageResource(R.drawable.img_screens_star)
                mTempIsCurImportant = true
            } else {
                animImportant = AnimationUtils.loadAnimation(App.getAppContext(), R.anim.bt_is_not_important)
                v.startAnimation(animImportant) //TODO
                binding.btDiaryIsImportant.setImageResource(R.drawable.img_screens_star_empty)
                mTempIsCurImportant = false
            }
        }
        //SAVE temp values from pop-up screen
        binding.btDiarySwap.setOnClickListener {
            mObserver.onNext(ScreenStates.RemoveState(binding.root))
            mTempCurDesc = binding.etDiaryDescription.text.toString() //TODO CHANGE
            mTempCurTheme = binding.etDiaryTheme.text.toString()
            mObserver.onNext(ScreenStates.OpenState(Screens.DEFAULT_SCREEN))
        }
        //SAVE data to SQLite DB
        binding.btDiaryGo.setOnClickListener {
            //Note item creating
            val item = NoteItem()
            item.mDescription = binding.etDiaryDescription.text.toString()
            item.mTag = binding.etDiaryTag.text.toString()
            item.mTime = getNoteTime()
            item.mImportant = mTempIsCurImportant
            item.mTitle = binding.etDiaryTheme.text.toString()
            mDiaryRoomRepository.insertNote(item)
            mObserver.onNext(ScreenStates.OpenState(Screens.DEFAULT_SCREEN))
            mObserver.onNext(ScreenStates.RemoveState(binding.root))
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
                            mObserver.onNext(ScreenStates.UpdateState(binding.root,myParams))
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
            Logger.log(Log.ERROR,"diary screen exception",e)
        }
    }

    override fun initScreensSpinner() {
        val arrayList = ArrayList<ScreensSpinnerItem>()
        arrayList.add(ScreensSpinnerItem(R.drawable.img_screens_diary))
        arrayList.add(ScreensSpinnerItem(R.drawable.img_screens_search))
        arrayList.add(ScreensSpinnerItem(R.drawable.img_screens_translate))
        mScreensSpinnerAdapter = ScreensSpinnerAdapter(mServiceContext, arrayList)
    }
}