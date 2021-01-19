package com.example.famreen.application.preferences

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.famreen.R
import com.example.famreen.application.App
import com.example.famreen.application.screens.Screens

class AppPreferences {
    private var preferences: SharedPreferences? = null
    private val context: Context = App.getAppContext()

    //App values
    private val APP = "APP"

    //Запущено ли приложение в 1 раз
    private val APP_FIRST_RUN = "APP_FIRST_RUN"
    private val APP_VERSION_CODE = "APP_VERSION_CODE"

    //Последний запущенный экран из screens
    private val APP_LAST_SCREEN = "APP_LAST_SCREEN"

    //Если пользователь открывает 1 раз screen из turned, экран который откровется - translate
    private val DEFAULT_APP_LAST_SCREEN = Screens.DIARY_SCREEN

    //Мини-кнопка, из которой открываются разные screens, это ее X и Y координаты по экрану
    private val X_TURNED_SCREEN_LOCATION = "X_TURNED_SCREEN_LOCATION"
    private val Y_TURNED_SCREEN_LOCATION = "Y_TURNED_SCREEN_LOCATION"

    //Размер текста приложения
    private val APP_TEXT_SIZE = "TEXT_SIZE"

    //Цвет фона screens
    private val SCREENS_COLOR = "SCREENS_COLOR"

    //Цвет текста screens
    private val SCREENS_TEXT_COLOR = "SCREENS_TEXT_COLOR"

    //Шрифт текста приложения
    private val APP_TEXT_FONT = "TRANSLATE_TEXT_FONT"

    //using pt (8pt) as text size value
    private val DEFAULT_TEXT_SIZE = 8

    //Translate values
    private val TRANSLATE = "TRANSLATE"

    private val TRANSLATE_REQ = "TRANSLATE_REQ"
    private val TRANSLATE_RESP = "TRANSLATE_RESP"
    private val TRANSLATE_LANGUAGE_FROM = "TRANSLATE_LANGUAGE_FROM"
    private val TRANSLATE_LANGUAGE_TO = "TRANSLATE_LANGUAGE_TO"
    private val DEFAULT_TRANSLATE_LANGUAGE_FROM = "ru"
    private val DEFAULT_TRANSLATE_LANGUAGE_TO = "en"
    private val TRANSLATE_SORT_TEXT_SIZE = "TRANSLATE_SORT_TEXT_SIZE"
    private val TRANSLATE_SORT_LANG_FROM = "TRANSLATE_SORT_LANG_FROM"
    private val TRANSLATE_SORT_LANG_TO = "TRANSLATE_SORT_LANG_TO"
    private val TRANSLATE_SORT_LANG_DESCRIPTION = "TRANSLATE_SORT_LANG_DESCRIPTION"
    private val TRANSLATE_SORT_TEXT_COLOR = "TRANSLATE_SORT_TEXT_COLOR"
    private val TRANSLATE_SORT_TEXT_COLOR_NIGHT = "TRANSLATE_SORT_TEXT_COLOR_NIGHT"
    private val TRANSLATE_TEXT_FONT = "TRANSLATE_TEXT_FONT"

    //Note values
    private val NOTE_SORT = "NOTE_SORT"

    private val NOTE_SORT_IS_IMPORTANT = "NOTE_SORT_IS_IMPORTANT"
    private val NOTE_SORT_TYPE = "NOTE_SORT_TYPE"
    private val NOTE_SORT_BY_TITLE = "NOTE_SORT_BY_TITLE"
    private val NOTE_SORT_BY_TAG = "NOTE_SORT_BY_TAG"
    private val NOTE_SORT_TEXT_SIZE = "NOTE_SORT_TEXT_SIZE"
    private val NOTE_SORT_TEXT_COLOR = "NOTE_SORT_TEXT_COLOR"
    private val NOTE_SORT_TEXT_COLOR_NIGHT = "NOTE_SORT_TEXT_COLOR_NIGHT"
    private val NOTE_SORT_TEXT_FONT = "NOTE_SORT_TEXT_FONT"

    //Search values
    private val SEARCH = "SEARCH"

    //Название пакета браузера
    private val SEARCH_BROWSER_PACKAGE_NAME = "SEARCH_BROWSER_PACKAGE_NAME"

    //Имя браузера
    private val SEARCH_BROWSER_NAME = "SEARCH_BROWSER_NAME"

    //Идентификатор браузера
    private val SEARCH_ENGINE = "SEARCH_ENGINE"

    //theme app
    private val THEME = "THEME"

    //Тема приложения
    private val APP_THEME = "APP_THEME"

    //СТандартные темы
    private val DEFAULT_APP_DARK_THEME = AppCompatDelegate.MODE_NIGHT_YES
    private val DEFAULT_APP_LIGHT_THEME = AppCompatDelegate.MODE_NIGHT_NO

    /**
     * Закрыт от неверного использования класса
     */
    private constructor()

    //TRANSLATE SORTS
    fun writeTranslateReq(req: String?) {
        preferences = context.getSharedPreferences(TRANSLATE, Context.MODE_PRIVATE)
        val editor = preferences!!.edit()
        editor.putString(TRANSLATE_REQ, req) //TODO CHECK
        editor.apply()
    }

    fun writeTranslateResp(resp: String?) {
        preferences = context.getSharedPreferences(TRANSLATE, Context.MODE_PRIVATE)
        val editor = preferences!!.edit()
        editor.putString(TRANSLATE_RESP, resp)
        editor.apply()
    }

    fun writeTranslateLangFrom(from: String?) {
        preferences = context.getSharedPreferences(TRANSLATE, Context.MODE_PRIVATE)
        val editor = preferences!!.edit()
        editor.putString(TRANSLATE_LANGUAGE_FROM, from)
        editor.apply()
    }

    fun writeTranslateLangTo(to: String?) {
        preferences = context.getSharedPreferences(TRANSLATE, Context.MODE_PRIVATE)
        val editor = preferences!!.edit()
        editor.putString(TRANSLATE_LANGUAGE_TO, to)
        editor.apply()
    }

    fun writeSearchPackageBrowserName(name: String?) {
        preferences = context.getSharedPreferences(SEARCH, Context.MODE_PRIVATE)
        val editor = preferences!!.edit()
        editor.putString(SEARCH_BROWSER_PACKAGE_NAME, name)
        editor.apply()
    }

    fun writeSearchBrowserName(name: String?) {
        preferences = context.getSharedPreferences(SEARCH, Context.MODE_PRIVATE)
        val editor = preferences!!.edit()
        editor.putString(SEARCH_BROWSER_NAME, name)
        editor.apply()
    }

    fun writeSearchEngine(name: Int) {
        preferences = context.getSharedPreferences(SEARCH, Context.MODE_PRIVATE)
        val editor = preferences!!.edit()
        editor.putInt(SEARCH_ENGINE, name)
        editor.apply()
    }

    fun writeTheme(theme: Int) {
        preferences = context.getSharedPreferences(THEME, Context.MODE_PRIVATE)
        val editor = preferences!!.edit()
        editor.putInt(APP_THEME, theme)
        editor.apply()
    }

    fun readTheme(): Int {
        preferences = context.getSharedPreferences(THEME, Context.MODE_PRIVATE)
        return preferences!!.getInt(APP_THEME, DEFAULT_APP_LIGHT_THEME)
    }

    fun readTranslateTextFont(): Int {
        preferences = context.getSharedPreferences(TRANSLATE, Context.MODE_PRIVATE)
        return preferences!!.getInt(TRANSLATE_TEXT_FONT, R.font.andika)
    }

    fun writeTranslateTextFont(font: Int) {
        preferences = context.getSharedPreferences(TRANSLATE, Context.MODE_PRIVATE)
        val editor = preferences!!.edit()
        editor.putInt(TRANSLATE_TEXT_FONT, font)
        editor.apply()
    }

    fun readAppTextFont(): Int {
        preferences = context.getSharedPreferences(APP, Context.MODE_PRIVATE)
        return preferences!!.getInt(APP_TEXT_FONT, R.font.andika)
    }

    fun writeAppTextFont(font: Int) {
        preferences = context.getSharedPreferences(APP, Context.MODE_PRIVATE)
        val editor = preferences!!.edit()
        editor.putInt(APP_TEXT_FONT, font)
        editor.apply()
    }

    fun readNoteTextFont(): Int {
        preferences = context.getSharedPreferences(NOTE_SORT, Context.MODE_PRIVATE)
        return preferences!!.getInt(NOTE_SORT_TEXT_FONT, R.font.andika)
    }

    fun writeNoteTextFont(font: Int) {
        preferences = context.getSharedPreferences(NOTE_SORT, Context.MODE_PRIVATE)
        val editor = preferences!!.edit()
        editor.putInt(NOTE_SORT_TEXT_FONT, font)
        editor.apply()
    }

    fun writeNoteTextSize(size: Int) {
        preferences = context.getSharedPreferences(NOTE_SORT, Context.MODE_PRIVATE)
        val editor = preferences!!.edit()
        editor.putInt(NOTE_SORT_TEXT_SIZE, size)
        editor.apply()
    }

    fun readNoteTextSize(): Int {
        preferences = context.getSharedPreferences(NOTE_SORT, Context.MODE_PRIVATE)
        return preferences!!.getInt(NOTE_SORT_TEXT_SIZE, 5)
    }

    /**
     * Игнорируются значения других тем, так как не используется в приложении [.APP_THEME]
     * @param size размер шрифта
     */
    @SuppressLint("SwitchIntDef")
    fun writeNoteTextColor(size: Int) {
        preferences = context.getSharedPreferences(NOTE_SORT, Context.MODE_PRIVATE)
        val editor = preferences!!.edit()
        when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_YES -> editor.putInt(NOTE_SORT_TEXT_COLOR_NIGHT, size)
            AppCompatDelegate.MODE_NIGHT_NO -> editor.putInt(NOTE_SORT_TEXT_COLOR, size)
        }
        editor.apply()
    }

    /**
     * Игнорируются значения других тем, так как не используется в приложении [.APP_THEME]
     */
    @SuppressLint("SwitchIntDef")
    fun readNoteTextColor(): Int {
        preferences = context.getSharedPreferences(NOTE_SORT, Context.MODE_PRIVATE)
        when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_YES -> return preferences!!.getInt(NOTE_SORT_TEXT_COLOR_NIGHT, R.color.colorLight)
            AppCompatDelegate.MODE_NIGHT_NO -> return preferences!!.getInt(NOTE_SORT_TEXT_COLOR, R.color.colorDark)
        }
        return R.color.colorText
    }

    /**
     * Игнорируются значения других тем, так как не используется в приложении [.APP_THEME]
     */
    @SuppressLint("SwitchIntDef")
    fun writeTranslateTextColor(size: Int) {
        preferences = context.getSharedPreferences(TRANSLATE, Context.MODE_PRIVATE)
        val editor = preferences!!.edit()
        when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_YES -> editor.putInt(TRANSLATE_SORT_TEXT_COLOR_NIGHT, size)
            AppCompatDelegate.MODE_NIGHT_NO -> editor.putInt(TRANSLATE_SORT_TEXT_COLOR, size)
        }
        editor.apply()
    }

    /**
     * Игнорируются значения других тем, так как не используется в приложении [.APP_THEME]
     */
    @SuppressLint("SwitchIntDef")
    fun readTranslateTextColor(): Int {
        preferences = context.getSharedPreferences(TRANSLATE, Context.MODE_PRIVATE)
        when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_YES -> return preferences!!.getInt(TRANSLATE_SORT_TEXT_COLOR_NIGHT, R.color.colorLight)
            AppCompatDelegate.MODE_NIGHT_NO -> return preferences!!.getInt(TRANSLATE_SORT_TEXT_COLOR, R.color.colorDark)
        }
        return R.color.colorText
    }

    fun writeAppTextSize(size: Int) {
        preferences = context.getSharedPreferences(APP, Context.MODE_PRIVATE)
        val editor = preferences!!.edit()
        editor.putInt(APP_TEXT_SIZE, size)
        editor.apply()
    }

    fun readAppTextSize(): Int {
        preferences = context.getSharedPreferences(APP, Context.MODE_PRIVATE)
        return preferences!!.getInt(APP_TEXT_SIZE, DEFAULT_TEXT_SIZE)
    }

    fun writeTranslateTextSize(size: Int) {
        preferences = context.getSharedPreferences(TRANSLATE, Context.MODE_PRIVATE)
        val editor = preferences!!.edit()
        editor.putInt(TRANSLATE_SORT_TEXT_SIZE, size)
        editor.apply()
    }

    fun readTranslateTextSize(): Int {
        preferences = context.getSharedPreferences(TRANSLATE, Context.MODE_PRIVATE)
        return preferences!!.getInt(TRANSLATE_SORT_TEXT_SIZE, DEFAULT_TEXT_SIZE)
    }

    fun writeScreensColor(color: Int) {
        preferences = context.getSharedPreferences(APP, Context.MODE_PRIVATE)
        val editor = preferences!!.edit()
        editor.putInt(SCREENS_COLOR, color)
        editor.apply()
    }

    fun readScreensColor(): Int {
        preferences = context.getSharedPreferences(APP, Context.MODE_PRIVATE)
        return preferences!!.getInt(SCREENS_COLOR, R.color.colorPopUpBackground)
    }

    fun writeScreensTextColor(color: Int) {
        preferences = context.getSharedPreferences(APP, Context.MODE_PRIVATE)
        val editor = preferences!!.edit()
        editor.putInt(SCREENS_TEXT_COLOR, color)
        editor.apply()
    }

    fun readScreensTextColor(): Int {
        preferences = context.getSharedPreferences(APP, Context.MODE_PRIVATE)
        return preferences!!.getInt(SCREENS_TEXT_COLOR, R.color.colorText)
    }

    fun writeXTurnedScreenLocation(x: Int) {
        preferences = context.getSharedPreferences(APP, Context.MODE_PRIVATE)
        val editor = preferences!!.edit()
        editor.putInt(X_TURNED_SCREEN_LOCATION, x)
        editor.apply()
    }

    fun readXTurnedScreenLocation(): Int {
        preferences = context.getSharedPreferences(APP, Context.MODE_PRIVATE)
        return preferences!!.getInt(X_TURNED_SCREEN_LOCATION, 1000)
    }

    fun writeYTurnedScreenLocation(x: Int) {
        preferences = context.getSharedPreferences(APP, Context.MODE_PRIVATE)
        val editor = preferences!!.edit()
        editor.putInt(Y_TURNED_SCREEN_LOCATION, x)
        editor.apply()
    }

    fun readYTurnedScreenLocation(): Int {
        preferences = context.getSharedPreferences(APP, Context.MODE_PRIVATE)
        return preferences!!.getInt(Y_TURNED_SCREEN_LOCATION, -1000)
    }

    fun readTranslateReq(): String? {
        preferences = context.getSharedPreferences(TRANSLATE, Context.MODE_PRIVATE)
        return preferences!!.getString(TRANSLATE_REQ, "")
    }

    fun readSearchPackageBrowserName(): String {
        preferences = context.getSharedPreferences(SEARCH, Context.MODE_PRIVATE)
        return preferences!!.getString(SEARCH_BROWSER_PACKAGE_NAME, "com.duckduckgo.mobile.android")!!
    }

    fun readSearchBrowserName(): String? {
        preferences = context.getSharedPreferences(SEARCH, Context.MODE_PRIVATE)
        return preferences!!.getString(SEARCH_BROWSER_NAME, "Search DuckDuckGo")
    }

    fun readSearchEngine(): Int {
        preferences = context.getSharedPreferences(SEARCH, Context.MODE_PRIVATE)
        return preferences!!.getInt(SEARCH_ENGINE, 0)
    }

    fun readTranslateResp(): String? {
        preferences = context.getSharedPreferences(TRANSLATE, Context.MODE_PRIVATE)
        return preferences!!.getString(TRANSLATE_RESP, STRING_DEFAULT_VALUE)
    }

    fun readTranslateLangFrom(): String? {
        preferences = context.getSharedPreferences(TRANSLATE, Context.MODE_PRIVATE)
        return preferences!!.getString(TRANSLATE_LANGUAGE_FROM, DEFAULT_TRANSLATE_LANGUAGE_FROM)
    }

    fun readTranslateLangTo(): String? {
        preferences = context.getSharedPreferences(TRANSLATE, Context.MODE_PRIVATE)
        return preferences!!.getString(TRANSLATE_LANGUAGE_TO, DEFAULT_TRANSLATE_LANGUAGE_TO)
    }

    //NOTE SORTS
    fun writeNoteSortType(type: Int) {
        preferences = context.getSharedPreferences(NOTE_SORT, Context.MODE_PRIVATE)
        val editor = preferences!!.edit()
        editor.putInt(NOTE_SORT_TYPE, type) //TODO CHECK
        editor.apply()
    }

    fun writeNoteSortIsImportant(isImportant: Boolean) {
        preferences = context.getSharedPreferences(NOTE_SORT, Context.MODE_PRIVATE)
        val editor = preferences!!.edit()
        editor.putBoolean(NOTE_SORT_IS_IMPORTANT, isImportant)
        editor.apply()
    }

    fun writeNoteSortTitle(title: String?) {
        preferences = context.getSharedPreferences(NOTE_SORT, Context.MODE_PRIVATE)
        val editor = preferences!!.edit()
        editor.putString(NOTE_SORT_BY_TITLE, title)
        editor.apply()
    }

    fun writeNoteSortTag(tag: String?) {
        preferences = context.getSharedPreferences(NOTE_SORT, Context.MODE_PRIVATE)
        val editor = preferences!!.edit()
        editor.putString(NOTE_SORT_BY_TAG, tag)
        editor.apply()
    }

    fun readNoteSortType(): Int {
        preferences = context.getSharedPreferences(NOTE_SORT, Context.MODE_PRIVATE)
        return preferences!!.getInt(NOTE_SORT_TYPE, 0)
    }

    fun readNoteSortIsImportant(): Boolean {
        preferences = context.getSharedPreferences(NOTE_SORT, Context.MODE_PRIVATE)
        return preferences!!.getBoolean(NOTE_SORT_IS_IMPORTANT, false)
    }

    fun readNoteSortTitle(): String {
        preferences = context.getSharedPreferences(NOTE_SORT, Context.MODE_PRIVATE)
        return preferences!!.getString(NOTE_SORT_BY_TITLE, STRING_DEFAULT_VALUE)!!
    }

    fun readNoteSortTag(): String {
        preferences = context.getSharedPreferences(NOTE_SORT, Context.MODE_PRIVATE)
        return preferences!!.getString(NOTE_SORT_BY_TAG, STRING_DEFAULT_VALUE)!!
    }
    //TRANSLATE SORT
    fun writeTranslateSortFromLang(from: String) {
        preferences = context.getSharedPreferences(NOTE_SORT, Context.MODE_PRIVATE)
        val editor = preferences!!.edit()
        editor.putString(TRANSLATE_SORT_LANG_FROM, from)
        editor.apply()
    }

    fun writeTranslateSortToLang(to: String) {
        preferences = context.getSharedPreferences(NOTE_SORT, Context.MODE_PRIVATE)
        val editor = preferences!!.edit()
        editor.putString(TRANSLATE_SORT_LANG_TO, to)
        editor.apply()
    }
    fun writeTranslateSortDescription(description: String) {
        preferences = context.getSharedPreferences(NOTE_SORT, Context.MODE_PRIVATE)
        val editor = preferences!!.edit()
        editor.putString(TRANSLATE_SORT_LANG_DESCRIPTION, description)
        editor.apply()
    }
    fun readTranslateSortFromLang(): String {
        preferences = context.getSharedPreferences(NOTE_SORT, Context.MODE_PRIVATE)
        return preferences!!.getString(TRANSLATE_SORT_LANG_FROM, STRING_DEFAULT_VALUE)!!
    }

    fun reaTranslateSortToLang(): String {
        preferences = context.getSharedPreferences(NOTE_SORT, Context.MODE_PRIVATE)
        return preferences!!.getString(TRANSLATE_SORT_LANG_TO, STRING_DEFAULT_VALUE)!!
    }
    fun readTranslateSortDescription(): String {
        preferences = context.getSharedPreferences(NOTE_SORT, Context.MODE_PRIVATE)
        return preferences!!.getString(TRANSLATE_SORT_LANG_DESCRIPTION, STRING_DEFAULT_VALUE)!!
    }
    //APP
    fun writeLastScreen(type: String?) {
        preferences = context.getSharedPreferences(APP, Context.MODE_PRIVATE)
        val editor = preferences!!.edit()
        editor.putString(APP_LAST_SCREEN, type)
        editor.apply()
    }

    fun readLastScreen(): String {
        preferences = context.getSharedPreferences(APP, Context.MODE_PRIVATE)
        return preferences!!.getString(APP_LAST_SCREEN, DEFAULT_APP_LAST_SCREEN)!!
    }

    fun writeFirstRun() {
        preferences = context.getSharedPreferences(APP, Context.MODE_PRIVATE)
        val editor = preferences!!.edit()
        editor.putBoolean(APP_FIRST_RUN, false)
        editor.apply()
    }

    fun readFirstRun(): Boolean {
        preferences = context.getSharedPreferences(APP, Context.MODE_PRIVATE)
        return preferences!!.getBoolean(APP_FIRST_RUN, true)
    }
    fun writeVersionCode(versionCode: Int) {
        preferences = context.getSharedPreferences(APP, Context.MODE_PRIVATE)
        val editor = preferences!!.edit()
        editor.putInt(APP_VERSION_CODE, versionCode)
        editor.apply()
    }

    fun readVersionCode(): Int {
        preferences = context.getSharedPreferences(APP, Context.MODE_PRIVATE)
        return preferences!!.getInt(APP_VERSION_CODE, INT_DEFAULT_VALUE)
    }

    fun deleteAll() {
        writeSearchBrowserName(null)
        writeSearchPackageBrowserName(null)
        writeLastScreen(DEFAULT_APP_LAST_SCREEN)
        writeNoteSortIsImportant(false)
        writeNoteSortTag("")
        writeNoteSortTitle("")
        writeNoteSortType(0)
        writeSearchEngine(0)
        writeTranslateLangFrom(DEFAULT_TRANSLATE_LANGUAGE_FROM)
        writeTranslateLangTo(DEFAULT_TRANSLATE_LANGUAGE_TO)
        writeTranslateReq("")
        writeTranslateResp("")
        //TODO PREPARE DELETE SYSTEM BY PREFS FOR DELETE
    }

    companion object {
        //TRANSLATE TEMP PREFS
        private var appPreferences: AppPreferences? = null
        val STRING_DEFAULT_VALUE = ""
        val INT_DEFAULT_VALUE = -1

        fun getProvider(): AppPreferences? {
            if (appPreferences == null) synchronized(AppPreferences::class.java) {
                appPreferences = AppPreferences()
                return appPreferences
            }
            return appPreferences
        }
    }
}