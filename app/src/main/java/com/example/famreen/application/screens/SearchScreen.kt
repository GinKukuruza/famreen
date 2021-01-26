package com.example.famreen.application.screens

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.net.Uri
import android.view.*
import android.widget.AdapterView
import com.example.famreen.R
import com.example.famreen.application.App
import com.example.famreen.application.adapters.ScreensSpinnerAdapter
import com.example.famreen.application.items.ScreensSpinnerItem
import com.example.famreen.application.logging.Logger
import com.example.famreen.application.preferences.AppPreferences
import com.example.famreen.application.interfaces.ScreenInit
import com.example.famreen.databinding.ScreenSearchBinding
import com.example.famreen.states.ScreenStates
import io.reactivex.Observer

class SearchScreen(private val mServiceContext: Context, val mObserver: Observer<ScreenStates>, private val mScreensListener: AdapterView.OnItemSelectedListener) : ScreenInit {
    private lateinit var mScreensSpinnerAdapter: ScreensSpinnerAdapter

    init{
        initScreensSpinner()
        create()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun create() {
        //Main ll view for WindowManager
        val layoutInflater = LayoutInflater.from(mServiceContext)
        val binding = ScreenSearchBinding.inflate(layoutInflater)
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
        binding.spinnerSearchChoice.adapter = mScreensSpinnerAdapter
        binding.spinnerSearchChoice.isSelected = false //TODO Solve
        binding.spinnerSearchChoice.setSelection(0, true)
        binding.spinnerSearchChoice.onItemSelectedListener = mScreensListener
        //Add ll as View to WindowManager
        mObserver.onNext(ScreenStates.CreateState(binding.root,myParams))
        //ClickListeners of the main XML layout
        binding.btSearchSwap.setOnClickListener {
            mObserver.onNext(ScreenStates.RemoveState(binding.root))
            mObserver.onNext(ScreenStates.OpenState(Screens.DEFAULT_SCREEN))
        }
        binding.btSearchGo.setOnClickListener {    //TODO WORK RIGHT NOW
            //get query from edittext
            val q = binding.etSearch.text.toString()
            //remove current popup window
            mObserver.onNext(ScreenStates.RemoveState(binding.root))
            //create turn screen
            mObserver.onNext(ScreenStates.OpenState(Screens.DEFAULT_SCREEN))
            //get current browser
            val name = AppPreferences.getProvider()!!.readSearchPackageBrowserName()
            //get search engine
            val engineId = AppPreferences.getProvider()!!.readSearchEngine()
            var engine: String? = null
            when (engineId) {
                0 -> engine = "https://www.google.com/#q="
                1 -> engine = "https://yandex.ru/?q="
                2 -> engine = "https://search.yahoo.com/?q="
                3 -> engine = "https://duckduckgo.com/?q="
                4 -> engine = "https://www.bing.com/?q="
            }
            //creating and start activity browser with query

            val intent = mServiceContext.packageManager.getLaunchIntentForPackage(name)
            if (intent != null && engine != null) {
                if (name == "com.duckduckgo.mobile.android") {
                    val ddgo = Intent()
                    ddgo.data = Uri.parse(engine + q)
                    ddgo.setPackage("com.duckduckgo.mobile.android")
                    mServiceContext.startActivity(ddgo)
                } else {
                    val uri = Uri.parse(engine + q)
                    intent.action = Intent.ACTION_WEB_SEARCH
                    intent.putExtra(SearchManager.QUERY, q)
                    intent.data = uri
                    mServiceContext.startActivity(intent)
                }
            }
        }
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
            binding.spinnerSearchChoice.setOnTouchListener(onUnturnedTouchListener)
            binding.btSearchSwap.setOnTouchListener(onUnturnedTouchListener)
            binding.etSearch.setOnTouchListener(onUnturnedTouchListener)
        } catch (e: Exception) {
            Logger.log(7,"search screen exception",e)
        }
    }

    override fun initScreensSpinner() {
        val arrayList = ArrayList<ScreensSpinnerItem>()
        arrayList.add(ScreensSpinnerItem(R.drawable.img_screens_search))
        arrayList.add(ScreensSpinnerItem(R.drawable.img_screens_translate))
        arrayList.add(ScreensSpinnerItem(R.drawable.img_screens_diary))
        mScreensSpinnerAdapter = ScreensSpinnerAdapter(mServiceContext, arrayList)
    }
}