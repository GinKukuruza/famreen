package com.example.famreen.application.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.example.colorpickerlib.lib.ColorPickerDialog
import com.example.famreen.R
import com.example.famreen.application.App
import com.example.famreen.application.activities.MainActivity
import com.example.famreen.application.interfaces.CallbackListener
import com.example.famreen.application.preferences.AppPreferences.Companion.getProvider
import com.example.famreen.application.viewmodels.PreferencesViewModel
import com.example.famreen.firebase.FirebaseProvider
import com.example.famreen.states.States
import com.example.famreen.states.callback.ItemStates
import com.example.famreen.states.callback.ThrowableStates
import com.example.famreen.utils.Utils
import com.example.famreen.utils.extensions.set
import javax.inject.Inject

class PreferencesFragment : PreferenceFragmentCompat() {
    //ui
    @Inject lateinit var mViewModel: PreferencesViewModel
    private lateinit var mNavController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.appComponent.inject(this@PreferencesFragment)
    }
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        //set prefs resources
        setPreferencesFromResource(R.xml.preferences, rootKey)
        //init prefs
        //Изменение цвета background'a всех screens
        val prefPalette = findPreference<Preference>("preferencePalette")
        //Изменение темы приложения
        val prefTheme = findPreference<SwitchPreferenceCompat>("preferenceTheme")
        //Поделиться с друзьями
        val prefShare = findPreference<Preference>("preferenceShare")
        //Связь с разработчиком
        val prefDevConnection = findPreference<Preference>("preferenceDevConnection")
        //О приложении
        val prefAboutApp = findPreference<Preference>("preferenceAboutApp")
        //Изменение размера шрифта приложения
        val prefTextSize = findPreference<Preference>("preferenceTextSize")
        //Изменение цвета текста у всех screens
        val prefScreensTextColor = findPreference<Preference>("preferencePaletteTextColor")
        //Изменение шрифта текста у приложения , включая screens
        val prefScreensTextStyle = findPreference<Preference>("preferenceTextStyle")
        //set listeners
        if (prefScreensTextColor != null) prefScreensTextColor.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val colorPickerDialog = ColorPickerDialog.createColorPickerDialog()
            val savedColor = getProvider()!!.readScreensTextColor()
            colorPickerDialog.setInitialColor(savedColor)
            colorPickerDialog.setOnColorPickedListener { color: Int, _: String? -> getProvider()!!.writeScreensTextColor(color) }
            colorPickerDialog.show(requireActivity().supportFragmentManager,"colorpickerdialog")
            true
        }
        if (prefScreensTextStyle != null) prefScreensTextStyle.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val dialog = DialogTextFontFragment(getProvider()!!.readAppTextFont(),object : CallbackListener<Int> {
                override fun onItem(s: ItemStates.ItemState<Int>) {
                    getProvider()!!.writeAppTextFont(s.item)
                }
                override fun onFailure(state: ThrowableStates) {}
            })
            dialog.show(requireActivity().supportFragmentManager, "dialogTextFont")
            true
        }
        if (prefPalette != null) prefPalette.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val colorPickerDialog = ColorPickerDialog.createColorPickerDialog()
            val savedColor = getProvider()!!.readScreensColor()
            colorPickerDialog.setInitialColor(savedColor)
            colorPickerDialog.setOnColorPickedListener { color: Int, _: String? -> getProvider()!!.writeScreensColor(color) }
            colorPickerDialog.show(requireActivity().supportFragmentManager,"colorpickerdialog")
            true
        }
        if (prefTextSize != null) prefTextSize.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val size = getProvider()!!.readAppTextSize()
            val dialogTextSizeFragment = DialogTextSizeFragment(size,object : CallbackListener<Int> {
                override fun onItem(s: ItemStates.ItemState<Int>) {
                    getProvider()!!.writeAppTextSize(s.item)
                }
                override fun onFailure(state: ThrowableStates) {}
            })
            dialogTextSizeFragment.show(requireActivity().supportFragmentManager, "dialogTextSize")
            true
        }
        if (prefAboutApp != null) prefAboutApp.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val options = Utils.getDefaultNavigationOptions()
            mNavController.navigate(R.id.action_preferences_to_aboutAppFragment, null, options)
            true
        }
        if (prefShare != null) prefShare.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val intent = mViewModel.createShareIntent()
            startActivity(Intent.createChooser(intent, "Share using"))
            true
        }
        if (prefDevConnection != null) prefDevConnection.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val options = Utils.getDefaultNavigationOptions()
            mNavController.navigate(R.id.action_preferences_to_devConnectionFragment, null, options)
            true
        }
        if (prefTheme != null) {
            prefTheme.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _: Preference?, _: Any? ->
                if (prefTheme.isChecked) {
                    prefTheme.isChecked = false
                    getProvider()!!.writeTheme(AppCompatDelegate.MODE_NIGHT_NO)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                } else {
                    prefTheme.isChecked = true
                    getProvider()!!.writeTheme(AppCompatDelegate.MODE_NIGHT_YES)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
                false
            }
            when (getProvider()!!.readTheme()) {
                AppCompatDelegate.MODE_NIGHT_NO -> prefTheme.isChecked = false
                AppCompatDelegate.MODE_NIGHT_YES -> prefTheme.isChecked = true
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorBackground, null))
        mNavController = Navigation.findNavController(view)
        mViewModel.getState().observe(viewLifecycleOwner, {
            when(it){
                is States.UserState<*> ->{
                    updateUI(it.user)
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        mViewModel.getState().set(States.UserState(FirebaseProvider.getCurrentUser()))
    }

    private fun <T>updateUI(user: T){
        (requireActivity() as MainActivity).getObserver().getState().set(States.UserState(user))
    }
}