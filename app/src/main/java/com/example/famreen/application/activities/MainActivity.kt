package com.example.famreen.application.activities

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.famreen.R
import com.example.famreen.application.App
import com.example.famreen.application.interfaces.MainUIUpdater
import com.example.famreen.application.items.MainItem
import com.example.famreen.application.preferences.AppPreferences
import com.example.famreen.application.viewmodels.MainActivityViewModel
import com.example.famreen.databinding.ActivityMainBinding
import com.example.famreen.firebase.FirebaseProvider
import com.example.famreen.firebase.db.EmptyUser
import com.example.famreen.firebase.db.UninitializedUser
import com.example.famreen.firebase.db.User
import com.example.famreen.states.States
import com.example.famreen.utils.Utils
import com.example.famreen.utils.extensions.set
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso
import javax.inject.Inject


class MainActivity : AppCompatActivity(), MainUIUpdater {
    private val PERMISSIONS_CODE = 515
    private val mTag = MainActivity::class.java.name
    @Inject lateinit var mViewModel: MainActivityViewModel
    private lateinit var mBinding: ActivityMainBinding
    private var mNavController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.appComponent.inject(this@MainActivity)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        checkPermissions()
        mViewModel.getState().observe(this, {
            when (it) {
                is States.DefaultState -> {
                }
                is States.LoadingState -> {
                }
                is States.ErrorState -> {
                    Toast.makeText(this, it.msg, Toast.LENGTH_LONG).show()
                }
                is States.UserState<*> -> {
                    updateUI(it.user)
                }
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
        mViewModel.startService()
    }

    override fun onStart() {
        super.onStart()
        mViewModel.getState().set(States.UserState(FirebaseProvider.getCurrentUser()))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        mNavController = Navigation.findNavController(this, R.id.main_fragment_container)
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
                mNavController!!.navigate(R.id.preferences, null, options)
            }
            R.id.action_sign_out -> {
                item.isVisible = false
                exit()
            }
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PERMISSIONS_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this,"Примите разрешение о наложении поверх окон",Toast.LENGTH_LONG).show()
                this@MainActivity.checkPermissions()
            } else {
                return
            }

        }
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
            is UninitializedUser -> {
                mViewModel.prepareUser()
            }
        }
        mBinding.tvMainToolbarName.invalidate()
        invalidateOptionsMenu()
    }
    override fun exit() {
        FirebaseProvider.exit()
        mViewModel.getState().set(States.UserState(FirebaseProvider.getCurrentUser()))
    }
    fun getObserver(): MainActivityViewModel {
        return mViewModel
    }
    private fun setTheme(){
        when(AppPreferences.getProvider()!!.readTheme()){
            AppCompatDelegate.MODE_NIGHT_YES -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_YES
            )
            AppCompatDelegate.MODE_NIGHT_NO -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }
    fun checkPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                Toast.makeText(this,"Примите разрешение о наложении поверх окон",Toast.LENGTH_LONG).show()
                startActivityForResult(intent, PERMISSIONS_CODE)
            }
        }
        /*val alertWindowPerm = ContextCompat.checkSelfPermission(this,android.Manifest.permission.SYSTEM_ALERT_WINDOW)
        Logger.d(mTag,"number - " + alertWindowPerm, "test")
        if (alertWindowPerm == PackageManager.PERMISSION_GRANTED ){
            return
        }else{
            requestPermissions(this,
                arrayOf(android.Manifest.permission.SYSTEM_ALERT_WINDOW),
                PERMISSIONS_CODE)
        }*/
    }
}
