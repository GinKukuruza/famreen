package com.example.famreen.application.screens

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.Spinner
import com.example.famreen.R
import com.example.famreen.application.App
import com.example.famreen.application.adapters.ScreenSpinnerTranslateAdapter
import com.example.famreen.application.adapters.ScreensSpinnerAdapter
import com.example.famreen.application.interfaces.ScreenInit
import com.example.famreen.application.interfaces.TranslateRoomRepository
import com.example.famreen.application.interfaces.YandexTranslateRepository
import com.example.famreen.application.items.ScreenSpinnerTranslateItem
import com.example.famreen.application.items.ScreensSpinnerItem
import com.example.famreen.application.items.TranslateItem
import com.example.famreen.application.logging.Logger
import com.example.famreen.application.preferences.AppPreferences
import com.example.famreen.application.room.DBConnection
import com.example.famreen.databinding.ScreenTranslateBinding
import com.example.famreen.states.ScreenStates
import com.example.famreen.translateApi.gson.TranslateResp
import com.example.famreen.utils.observers.ItemObserver
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class TranslationScreen(val mServiceContext: Context, val mObserver: Observer<ScreenStates>, private val mScreensListener: AdapterView.OnItemSelectedListener) : ScreenInit {
    @Inject lateinit var mTranslateRoomRepository: TranslateRoomRepository
    @Inject lateinit var mTranslateRepositoryImpl: YandexTranslateRepository //TODO изменить на интерфейс
    private lateinit var mScreensSpinnerAdapter: ScreensSpinnerAdapter
    private lateinit var mScreenSpinnerTranslateAdapter: ScreenSpinnerTranslateAdapter
    private lateinit var mLangListener: AdapterView.OnItemSelectedListener

    private var mFirst = 0
    private var mTwice = 0
    private var mStateTranslate = false
    private val mTranslatePREF = "-"

    init{
        App.appComponent.inject(this@TranslationScreen)
        initScreensSpinner()
        initLangListener()
        create()
    }
    private fun initLangListener(){
        mLangListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val item = parent.getItemAtPosition(position) as ScreenSpinnerTranslateItem
                when (parent.id) {
                    R.id.spinner_translate_from -> {
                        AppPreferences.getProvider()!!.writeTranslateLangFrom(item.mLangUI)
                        mFirst = position
                    }
                    R.id.spinner_translate_to -> {
                        AppPreferences.getProvider()!!.writeTranslateLangTo(item.mLangUI)
                        mTwice = position
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun initTranslateSpinners(s1: Spinner, s2: Spinner) {
        val dbConnection = DBConnection.getDbConnection()
        dbConnection!!.translateDAO.allLangs!!
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableSingleObserver<List<ScreenSpinnerTranslateItem>?>() {
                override fun onSuccess(list: List<ScreenSpinnerTranslateItem>) {
                    mScreenSpinnerTranslateAdapter = ScreenSpinnerTranslateAdapter(mServiceContext, list)
                    s1.adapter = mScreenSpinnerTranslateAdapter
                    s1.isSelected = true //TODO Solve
                    s1.onItemSelectedListener = mLangListener
                    s2.adapter = mScreenSpinnerTranslateAdapter
                    s2.isSelected = true //TODO Solve
                    s2.onItemSelectedListener = mLangListener
                    val from = AppPreferences.getProvider()!!.readTranslateLangFrom()
                    val to = AppPreferences.getProvider()!!.readTranslateLangTo()
                    for (i in list.indices) {
                        if (from == list[i].mLangUI) {
                            s1.setSelection(i, true)
                        }
                        if (to == list[i].mLangUI) {
                            s2.setSelection(i, true)
                        }
                    }
                }

                override fun onError(e: Throwable) {
                    Logger.log(Log.ERROR, "translate spinners exception", e)
                }
            })
    }
    //save Translate Data
    private fun saveTranslateData(fromTranslate: String, toTranslate: String, fromLang: String?, toLang: String?) {
        //ItemCreating
        val item = TranslateItem()
        item.mFromLang = fromLang
        item.mFromTranslate = fromTranslate
        item.mToLang = toLang
        item.mToTranslate = toTranslate
        //item saving in db
        mTranslateRoomRepository.insertTranslate(item)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun create() {
        //Main ll view for WindowManager
        val layoutInflater = LayoutInflater.from(mServiceContext)
        val binding = ScreenTranslateBinding.inflate(layoutInflater)
        //set background color
        val drawable = mServiceContext.resources.getDrawable(R.drawable.selector_screens_background, null)
        drawable.colorFilter = PorterDuffColorFilter(AppPreferences.getProvider()!!.readScreensColor(), PorterDuff.Mode.SRC)
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
        binding.spinnerTranslateChoice.adapter = mScreensSpinnerAdapter
        binding.spinnerTranslateChoice.isSelected = false //TODO Solve
        binding.spinnerTranslateChoice.setSelection(0, true)
        binding.spinnerTranslateChoice.onItemSelectedListener = mScreensListener
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        initTranslateSpinners(binding.spinnerTranslateFrom, binding.spinnerTranslateTo)
        //Add ll as View to WindowManager
        mObserver.onNext(ScreenStates.CreateState(binding.root,myParams))
        //Set SAVED temp values
        binding.etTranslateReq.setText(AppPreferences.getProvider()!!.readTranslateReq())
        binding.tvTranslateResp.text = AppPreferences.getProvider()!!.readTranslateResp()
        //SAVE temp values from pop-up screen
        binding.btTranslateSwap.setOnClickListener {
            mObserver.onNext(ScreenStates.RemoveState(binding.root))
            mObserver.onNext(ScreenStates.OpenState(Screens.DEFAULT_SCREEN))
        }
        binding.ibTranslateClear.setOnClickListener {
            binding.etTranslateReq.setText("")
            binding.tvTranslateResp.text = ""
        }
        binding.btTranslateSwitch.setOnClickListener {
            mStateTranslate = if (!mStateTranslate) {
                binding.spinnerTranslateFrom.setSelection(mTwice, true)
                binding.spinnerTranslateTo.setSelection(mFirst, true)
                true
            } else {
                binding.spinnerTranslateFrom.setSelection(mFirst, true)
                binding.spinnerTranslateTo.setSelection(mTwice, true)
                false
            }
        }
        //SAVE data to SQLite DB
        binding.btTranslateGo.setOnClickListener {
            val text = binding.etTranslateReq.text.toString()
            if (text != "") {
                val lang = AppPreferences.getProvider()!!.readTranslateLangFrom() +
                        mTranslatePREF +
                        AppPreferences.getProvider()!!.readTranslateLangTo()
                //request to get translate data result
                mTranslateRepositoryImpl.translate(text,lang,object : ItemObserver<TranslateResp>{
                    @SuppressLint("SetTextI18n")
                    override fun getItem(item: TranslateResp) {
                        if(item.mText == null) binding.tvTranslateResp.text = "internal translate error: response text error"
                        item.mText?.let {
                            val resp = StringBuilder()
                            for (i in it.indices) {
                                resp.append(it[i])
                            }
                            AppPreferences.getProvider()!!.writeTranslateResp(resp.toString())
                            AppPreferences.getProvider()!!.writeTranslateReq(binding.etTranslateReq.text.toString()) //TODO CHECK
                            val firstLang = binding.spinnerTranslateFrom.getItemAtPosition(mFirst) as ScreenSpinnerTranslateItem
                            val twiceLang = binding.spinnerTranslateTo.getItemAtPosition(mTwice) as ScreenSpinnerTranslateItem
                            saveTranslateData(binding.etTranslateReq.text.toString(), resp.toString(), firstLang.mLangName, twiceLang.mLangName)
                            binding.tvTranslateResp.text = resp
                        }
                    }

                })
            }
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
            binding.btTranslateSwap.setOnTouchListener(onUnturnedTouchListener)
            binding.btTranslateGo.setOnTouchListener(onUnturnedTouchListener)
            binding.spinnerTranslateChoice.setOnTouchListener(onUnturnedTouchListener)
            binding.etTranslateReq.setOnTouchListener(onUnturnedTouchListener)
            binding.tvTranslateResp.setOnTouchListener(onUnturnedTouchListener)
            binding.spinnerTranslateTo.setOnTouchListener(onUnturnedTouchListener)
            binding.spinnerTranslateFrom.setOnTouchListener(onUnturnedTouchListener)
        } catch (e: Exception) {
            Logger.log(Log.ERROR,"translate screen exception",e)
        }
    }

    override fun initScreensSpinner() {
        val arrayList = ArrayList<ScreensSpinnerItem>()
        arrayList.add(ScreensSpinnerItem(R.drawable.img_screens_translate))
        arrayList.add(ScreensSpinnerItem(R.drawable.img_screens_diary))
        arrayList.add(ScreensSpinnerItem(R.drawable.img_screens_search))
        mScreensSpinnerAdapter = ScreensSpinnerAdapter(mServiceContext, arrayList)
    }
}