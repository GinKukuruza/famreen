package com.example.famreen.application.screens

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.*
import android.widget.AdapterView
import android.widget.Spinner
import com.example.famreen.R
import com.example.famreen.application.adapters.ScreenSpinnerTranslateAdapter
import com.example.famreen.application.adapters.ScreensSpinnerAdapter
import com.example.famreen.application.items.ScreenSpinnerTranslateItem
import com.example.famreen.application.items.ScreensSpinnerItem
import com.example.famreen.application.items.TranslateItem
import com.example.famreen.application.logging.Logger
import com.example.famreen.application.preferences.AppPreferences
import com.example.famreen.application.room.DBConnection
import com.example.famreen.utils.observers.ItemObserver
import com.example.famreen.application.room.repositories.TranslateRoomRepository
import com.example.famreen.application.interfaces.ScreenInit
import com.example.famreen.databinding.ScreenTranslateBinding
import com.example.famreen.firebase.repositories.TranslateRepository
import com.example.famreen.translateApi.repositories.YandexTranslateRepository
import com.example.famreen.states.ScreenStates
import com.example.famreen.translateApi.gson.TranslateResp
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class TranslationScreen(val serviceContext: Context, val observer: Observer<ScreenStates>, private val screensListener: AdapterView.OnItemSelectedListener) :
    ScreenInit {
    private val translateRoomRepository = TranslateRoomRepository(TranslateRepository())
    private val translateRepository: YandexTranslateRepository = YandexTranslateRepository(translateRoomRepository)
        private lateinit var screensSpinnerAdapter: ScreensSpinnerAdapter
    private lateinit var screenSpinnerTranslateAdapter: ScreenSpinnerTranslateAdapter
    private lateinit var langListener: AdapterView.OnItemSelectedListener

    private var first = 0
    private var twice = 0
    private var stateTranslate = false
    private val translatePREF = "-"

    init{
        initScreensSpinner()
        initLangListener()
        create()
    }
    private fun initLangListener(){
        langListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val item = parent.getItemAtPosition(position) as ScreenSpinnerTranslateItem
                when (parent.id) {
                    R.id.spinner_translate_from -> {
                        AppPreferences.getProvider()!!.writeTranslateLangFrom(item.langUI)
                        first = position
                    }
                    R.id.spinner_translate_to -> {
                        AppPreferences.getProvider()!!.writeTranslateLangTo(item.langUI)
                        twice = position
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
            .subscribe(object : DisposableSingleObserver<List<ScreenSpinnerTranslateItem?>?>() {
                override fun onSuccess(list: List<ScreenSpinnerTranslateItem?>) {
                    screenSpinnerTranslateAdapter = ScreenSpinnerTranslateAdapter(serviceContext, list)
                    s1.adapter = screenSpinnerTranslateAdapter
                    s1.isSelected = true //TODO Solve
                    s1.onItemSelectedListener = langListener
                    s2.adapter = screenSpinnerTranslateAdapter
                    s2.isSelected = true //TODO Solve
                    s2.onItemSelectedListener = langListener
                    val from = AppPreferences.getProvider()!!.readTranslateLangFrom()
                    val to = AppPreferences.getProvider()!!.readTranslateLangTo()
                    for (i in list.indices) {
                        if (from == list[i]!!.langUI) {
                            s1.setSelection(i, true)
                        }
                        if (to == list[i]!!.langUI) {
                            s2.setSelection(i, true)
                        }
                    }
                }

                override fun onError(e: Throwable) {
                    Logger.log(9, "translate spinners exception", e)
                }
            })
    }
    //save Translate Data
    private fun saveTranslateData(fromTranslate: String, toTranslate: String, fromLang: String?, toLang: String?) {
        //ItemCreating
        val item = TranslateItem()
        item.from_lang = fromLang
        item.from_translate = fromTranslate
        item.to_lang = toLang
        item.to_translate = toTranslate
        //item saving in db
        translateRoomRepository.insertTranslate(item)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun create() {
        //Main ll view for WindowManager
        val layoutInflater = LayoutInflater.from(serviceContext)
        val binding = ScreenTranslateBinding.inflate(layoutInflater)
        //set background color
        val drawable = serviceContext.resources.getDrawable(R.drawable.selector_screens_background, null)
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
        binding.spinnerTranslateChoice.adapter = screensSpinnerAdapter
        binding.spinnerTranslateChoice.isSelected = false //TODO Solve
        binding.spinnerTranslateChoice.setSelection(0, true)
        binding.spinnerTranslateChoice.onItemSelectedListener = screensListener
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        initTranslateSpinners(binding.spinnerTranslateFrom, binding.spinnerTranslateTo)
        //Add ll as View to WindowManager
        observer.onNext(ScreenStates.CreateState(binding.root,myParams))
        //Set SAVED temp values
        binding.etTranslateReq.setText(AppPreferences.getProvider()!!.readTranslateReq())
        binding.tvTranslateResp.text = AppPreferences.getProvider()!!.readTranslateResp()
        //SAVE temp values from pop-up screen
        binding.btTranslateSwap.setOnClickListener {
            observer.onNext(ScreenStates.RemoveState(binding.root))
           observer.onNext(ScreenStates.OpenState(Screens.DEFAULT_SCREEN))
        }
        binding.ibTranslateClear.setOnClickListener {
            binding.etTranslateReq.setText("")
            binding.tvTranslateResp.text = ""
        }
        binding.btTranslateSwitch.setOnClickListener {
            stateTranslate = if (!stateTranslate) {
                binding.spinnerTranslateFrom.setSelection(twice, true)
                binding.spinnerTranslateTo.setSelection(first, true)
                true
            } else {
                binding.spinnerTranslateFrom.setSelection(first, true)
                binding.spinnerTranslateTo.setSelection(twice, true)
                false
            }
        }
        //SAVE data to SQLite DB
        binding.btTranslateGo.setOnClickListener {
            val text = binding.etTranslateReq.text.toString()
            if (text != "") {
                val lang = AppPreferences.getProvider()!!.readTranslateLangFrom() +
                        translatePREF +
                        AppPreferences.getProvider()!!.readTranslateLangTo()
                //request to get translate data result

                translateRepository.translate(text,lang,object : ItemObserver<TranslateResp>{
                    override fun getItem(item: TranslateResp) {
                        if(item.text == null) binding.tvTranslateResp.text = "internal translate error: response text error"
                        item.text?.let {
                            val resp = StringBuilder()
                            for (i in it.indices) {
                                resp.append(it[i])
                            }
                            AppPreferences.getProvider()!!.writeTranslateResp(resp.toString())
                            AppPreferences.getProvider()!!.writeTranslateReq(binding.etTranslateReq.text.toString()) //TODO CHECK
                            val firstLang = binding.spinnerTranslateFrom.getItemAtPosition(first) as ScreenSpinnerTranslateItem
                            val twiceLang = binding.spinnerTranslateTo.getItemAtPosition(twice) as ScreenSpinnerTranslateItem
                            saveTranslateData(binding.etTranslateReq.text.toString(), resp.toString(), firstLang.langName, twiceLang.langName)
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
                            observer.onNext(ScreenStates.UpdateState(binding.root,myParams))
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
            Logger.log(7,"translate screen exception",e)
        }
    }

    override fun initScreensSpinner() {
        val arrayList = ArrayList<ScreensSpinnerItem>()
        arrayList.add(ScreensSpinnerItem(R.drawable.img_screens_translate))
        arrayList.add(ScreensSpinnerItem(R.drawable.img_screens_diary))
        arrayList.add(ScreensSpinnerItem(R.drawable.img_screens_search))
        screensSpinnerAdapter = ScreensSpinnerAdapter(serviceContext, arrayList)
    }
}