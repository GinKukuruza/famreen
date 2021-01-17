package com.example.famreen.application.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.famreen.R
import com.example.famreen.application.App
import com.example.famreen.states.States
import com.example.famreen.application.interfaces.MainUIUpdater
import com.example.famreen.application.items.MainItem
import com.example.famreen.application.logging.Logger
import com.example.famreen.application.preferences.AppPreferences
import com.example.famreen.application.viewmodels.MainActivityViewModel
import com.example.famreen.databinding.ActivityMainBinding
import com.example.famreen.firebase.FirebaseProvider
import com.example.famreen.firebase.db.EmptyUser
import com.example.famreen.firebase.db.UninitializedUser
import com.example.famreen.firebase.db.User
import com.example.famreen.utils.Utils
import com.example.famreen.utils.set
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.gms.ads.identifier.AdvertisingIdClient.getAdvertisingIdInfo
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import javax.inject.Inject

class MainActivity : AppCompatActivity(), MainUIUpdater {
    private val tag = MainActivity::class.java.name
    @Inject lateinit var viewModel: MainActivityViewModel
    private lateinit var mBinding: ActivityMainBinding
    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseCrashlytics.getInstance().log("init ")
        App.appComponent.inject(this@MainActivity)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel.state.observe(this,androidx.lifecycle.Observer {
            when(it){
                is States.DefaultState -> {

                }
                is States.LoadingState -> {

                }
                is States.ErrorState -> {
                    Toast.makeText(this,it.msg, Toast.LENGTH_LONG).show()
                }
                is States.UserState<*> -> {
                    updateUI(it.user)
                }
            }
        })
        setTheme()
        val onClickListener = View.OnClickListener {
            val options = Utils.getDefaultNavigationOptions()
            navController!!.navigate(R.id.fragmentLogin, null, options)
        }
        mBinding.llMainToolbarAccount.setOnClickListener(onClickListener)
        mBinding.ibMainToolbarIcon.setOnClickListener(onClickListener)
        setSupportActionBar(mBinding.toolBar)
        if (supportActionBar != null) supportActionBar!!.setDisplayShowTitleEnabled(false)
    }

    override fun onStart() {
        super.onStart()
        viewModel.state.set(States.UserState(FirebaseProvider.getCurrentUser()))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        navController = Navigation.findNavController(this, R.id.main_fragment_container)
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        if (FirebaseProvider.userIsLogIn()) {
            menu.findItem(R.id.action_sign_out).isVisible = true
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_preferences -> {
                val options = Utils.getDefaultNavigationOptions()
                navController!!.navigate(R.id.preferences, null, options)
            }
            R.id.action_sign_out -> {
                item.isVisible = false
                exit()
            }
        }
        return true
    }

    override fun <T> updateUI(user: T) {
        when(user){
            is FirebaseUser -> {
                mBinding.item = MainItem(user.displayName)
                Picasso.get().load(user.photoUrl).fit().into(mBinding.ibMainToolbarIcon)
            }
            is User -> {
                mBinding.ibMainToolbarIcon.setImageDrawable(null)
                mBinding.item = MainItem(user.name)
            }
            is EmptyUser -> {
                mBinding.ibMainToolbarIcon.setImageDrawable(null)
                mBinding.item = MainItem("")
            }
            is UninitializedUser ->{
               viewModel.prepareUser()
            }
        }
        mBinding.tvMainToolbarName.invalidate()
        invalidateOptionsMenu()
    }
    override fun exit() {
        FirebaseProvider.exit()
        viewModel.state.set(States.UserState(FirebaseProvider.getCurrentUser()))
    }
    fun getObserver(): MainActivityViewModel {
        return viewModel
    }
    private fun setTheme(){
        when(AppPreferences.getProvider()!!.readTheme()){
            AppCompatDelegate.MODE_NIGHT_YES-> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            AppCompatDelegate.MODE_NIGHT_NO-> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
