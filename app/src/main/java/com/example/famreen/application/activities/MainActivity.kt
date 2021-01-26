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
import com.example.famreen.application.preferences.AppPreferences
import com.example.famreen.application.security.Encryptor
import com.example.famreen.application.viewmodels.MainActivityViewModel
import com.example.famreen.databinding.ActivityMainBinding
import com.example.famreen.firebase.FirebaseProvider
import com.example.famreen.firebase.db.EmptyUser
import com.example.famreen.firebase.db.UninitializedUser
import com.example.famreen.firebase.db.User
import com.example.famreen.utils.Utils
import com.example.famreen.utils.extensions.set
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.squareup.picasso.Picasso
import javax.inject.Inject

class MainActivity : AppCompatActivity(), MainUIUpdater {
    private val mTag = MainActivity::class.java.name
    @Inject lateinit var mViewModel: MainActivityViewModel
    @Inject lateinit var mFirebaseProvider: FirebaseProvider
    private lateinit var mBinding: ActivityMainBinding
    private var mNavController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.appComponent.inject(this@MainActivity)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mViewModel.getState().observe(this,androidx.lifecycle.Observer {
            when(it){
                is States.DefaultState -> { }
                is States.LoadingState -> { }
                is States.ErrorState -> {
                    Toast.makeText(this,it.msg, Toast.LENGTH_LONG).show()
                }
                is States.UserState<*> -> {
                    updateUI(it.user) }
            }
        })
        setTheme()
        val onClickListener = View.OnClickListener {
            val options = Utils.getDefaultNavigationOptions()
            mNavController!!.navigate(R.id.fragmentLogin, null, options)
        }
        mBinding.llMainToolbarAccount.setOnClickListener(onClickListener)
        mBinding.ibMainToolbarIcon.setOnClickListener(onClickListener)
        setSupportActionBar(mBinding.toolBar)
        if (supportActionBar != null) supportActionBar!!.setDisplayShowTitleEnabled(false)
    }

    override fun onStart() {
        super.onStart()
        mViewModel.getState().set(States.UserState(mFirebaseProvider.getCurrentUser()))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        mNavController = Navigation.findNavController(this, R.id.main_fragment_container)
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        if (mFirebaseProvider.userIsLogIn()) {
            menu.findItem(R.id.action_sign_out).isVisible = true
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_preferences -> {
                val options = Utils.getDefaultNavigationOptions()
                mNavController!!.navigate(R.id.preferences, null, options)
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
                mBinding.item = MainItem(user.mName)
            }
            is EmptyUser -> {
                mBinding.ibMainToolbarIcon.setImageDrawable(null)
                mBinding.item = MainItem("")
            }
            is UninitializedUser ->{
               mViewModel.prepareUser()
            }
        }
        mBinding.tvMainToolbarName.invalidate()
        invalidateOptionsMenu()
    }
    override fun exit() {
        mFirebaseProvider.exit()
        mViewModel.getState().set(States.UserState(mFirebaseProvider.getCurrentUser()))
    }
    fun getObserver(): MainActivityViewModel {
        return mViewModel
    }
    private fun setTheme(){
        when(AppPreferences.getProvider()!!.readTheme()){
            AppCompatDelegate.MODE_NIGHT_YES-> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            AppCompatDelegate.MODE_NIGHT_NO-> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
