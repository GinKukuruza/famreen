package com.example.famreen.application.preferences

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.famreen.R
import com.example.famreen.application.App
import com.example.famreen.application.screens.Screens

class AppPreferences private constructor() {
    private var mPreferences: SharedPreferences? = null
    private val mContext: Context = App.getAppContext()

    companion object {
        //App values
        private const val APP = "APP"

        //Запущено ли приложение в 1 раз
        private const val APP_FIRST_RUN = "APP_FIRST_RUN"
        private const val APP_VERSION_CODE = "APP_VERSION_CODE"

        //Последний запущенный экран из screens
        private const val APP_LAST_SCREEN = "APP_LAST_SCREEN"

        //Если пользователь открывает 1 раз screen из turned, экран который откровется - translate
        private const val DEFAULT_APP_LAST_SCREEN = Screens.DIARY_SCREEN

        //Мини-кнопка, из которой открываются разные screens, это ее X и Y координаты по экрану
        private const val X_TURNED_SCREEN_LOCATION = "X_TURNED_SCREEN_LOCATION"
        private const val Y_TURNED_SCREEN_LOCATION = "Y_TURNED_SCREEN_LOCATION"

        //Размер текста приложения
        private const val APP_TEXT_SIZE = "TEXT_SIZE"

        //Цвет фона screens
        private const val SCREENS_COLOR = "SCREENS_COLOR"

        //Цвет текста screens
        private const val SCREENS_TEXT_COLOR = "SCREENS_TEXT_COLOR"

        //Шрифт текста приложения
        private const val APP_TEXT_FONT = "TRANSLATE_TEXT_FONT"

        //using pt (8pt) as text size value
        private const val DEFAULT_TEXT_SIZE = 8

        //Translate values
        private const val TRANSLATE = "TRANSLATE"

        private const val TRANSLATE_REQ = "TRANSLATE_REQ"
        private const val TRANSLATE_RESP = "TRANSLATE_RESP"
        private const val TRANSLATE_LANGUAGE_FROM = "TRANSLATE_LANGUAGE_FROM"
        private const val TRANSLATE_LANGUAGE_TO = "TRANSLATE_LANGUAGE_TO"
        private const val DEFAULT_TRANSLATE_LANGUAGE_FROM = "ru"
        private const val DEFAULT_TRANSLATE_LANGUAGE_TO = "en"
        private const val TRANSLATE_SORT_TEXT_SIZE = "TRANSLATE_SORT_TEXT_SIZE"
        private const val TRANSLATE_SORT_LANG_FROM = "TRANSLATE_SORT_LANG_FROM"
        private const val TRANSLATE_SORT_LANG_TO = "TRANSLATE_SORT_LANG_TO"
        private const val TRANSLATE_SORT_LANG_DESCRIPTION = "TRANSLATE_SORT_LANG_DESCRIPTION"
        private const val TRANSLATE_SORT_TEXT_COLOR = "TRANSLATE_SORT_TEXT_COLOR"
        private const val TRANSLATE_SORT_TEXT_COLOR_NIGHT = "TRANSLATE_SORT_TEXT_COLOR_NIGHT"
        private const val TRANSLATE_TEXT_FONT = "TRANSLATE_TEXT_FONT"

        //Note values
        private const val NOTE_SORT = "NOTE_SORT"

        private const val NOTE_SORT_IS_IMPORTANT = "NOTE_SORT_IS_IMPORTANT"
        private const val NOTE_SORT_TYPE = "NOTE_SORT_TYPE"
        private const val NOTE_SORT_BY_TITLE = "NOTE_SORT_BY_TITLE"
        private const val NOTE_SORT_BY_TAG = "NOTE_SORT_BY_TAG"
        private const val NOTE_SORT_TEXT_SIZE = "NOTE_SORT_TEXT_SIZE"
        private const val NOTE_SORT_TEXT_COLOR = "NOTE_SORT_TEXT_COLOR"
        private const val NOTE_SORT_TEXT_COLOR_NIGHT = "NOTE_SORT_TEXT_COLOR_NIGHT"
        private const val NOTE_SORT_TEXT_FONT = "NOTE_SORT_TEXT_FONT"

        //Search values
        private const val SEARCH = "SEARCH"

        //Название пакета браузера
        private const val SEARCH_BROWSER_PACKAGE_NAME = "SEARCH_BROWSER_PACKAGE_NAME"

        //Имя браузера
        private const val SEARCH_BROWSER_NAME = "SEARCH_BROWSER_NAME"

        //Идентификатор браузера
        private const val SEARCH_ENGINE = "SEARCH_ENGINE"

        //theme app
        private const val THEME = "THEME"

        //Тема приложения
        private const val APP_THEME = "APP_THEME"

        private const val DEFAULT_APP_LIGHT_THEME = AppCompatDelegate.MODE_NIGHT_NO
        //TRANSLATE TEMP PREFS
        private var appPreferences: AppPreferences? = null
        const val STRING_DEFAULT_VALUE = ""
        const val INT_DEFAULT_VALUE = -1

        fun getProvider(): AppPreferences? {
            if (appPreferences == null) synchronized(AppPreferences::class.java) {
                appPreferences = AppPreferences()
                return appPreferences
            }
            return appPreferences
        }
    }
    //TRANSLATE SORTS
    fun writeTranslateReq(req: String?) {
        mPreferences = mContext.getSharedPreferences(TRANSLATE, Context.MODE_PRIVATE)
        val editor = mPreferences!!.edit()
        editor.putString(TRANSLATE_REQ, req)
        editor.apply()
    }

    fun writeTranslateResp(resp: String?) {
        mPreferences = mContext.getSharedPreferences(TRANSLATE, Context.MODE_PRIVATE)
        val editor = mPreferences!!.edit()
        editor.putString(TRANSLATE_RESP, resp)
        editor.apply()
    }

    fun writeTranslateLangFrom(from: String?) {
        mPreferences = mContext.getSharedPreferences(TRANSLATE, Context.MODE_PRIVATE)
        val editor = mPreferences!!.edit()
        editor.putString(TRANSLATE_LANGUAGE_FROM, from)
        editor.apply()
    }

    fun writeTranslateLangTo(to: String?) {
        mPreferences = mContext.getSharedPreferences(TRANSLATE, Context.MODE_PRIVATE)
        val editor = mPreferences!!.edit()
        editor.putString(TRANSLATE_LANGUAGE_TO, to)
        editor.apply()
    }

    fun writeSearchPackageBrowserName(name: String?) {
        mPreferences = mContext.getSharedPreferences(SEARCH, Context.MODE_PRIVATE)
        val editor = mPreferences!!.edit()
        editor.putString(SEARCH_BROWSER_PACKAGE_NAME, name)
        editor.apply()
    }

    fun writeSearchBrowserName(name: String?) {
        mPreferences = mContext.getSharedPreferences(SEARCH, Context.MODE_PRIVATE)
        val editor = mPreferences!!.edit()
        editor.putString(SEARCH_BROWSER_NAME, name)
        editor.apply()
    }

    fun writeSearchEngine(name: Int) {
        mPreferences = mContext.getSharedPreferences(SEARCH, Context.MODE_PRIVATE)
        val editor = mPreferences!!.edit()
        editor.putInt(SEARCH_ENGINE, name)
        editor.apply()
    }

    fun writeTheme(theme: Int) {
        mPreferences = mContext.getSharedPreferences(THEME, Context.MODE_PRIVATE)
        val editor = mPreferences!!.edit()
        editor.putInt(APP_THEME, theme)
        editor.apply()
    }

    fun readTheme(): Int {
        mPreferences = mContext.getSharedPreferences(THEME, Context.MODE_PRIVATE)
        return mPreferences!!.getInt(APP_THEME, DEFAULT_APP_LIGHT_THEME)
    }

    fun readTranslateTextFont(): Int {
        mPreferences = mContext.getSharedPreferences(TRANSLATE, Context.MODE_PRIVATE)
        return mPreferences!!.getInt(TRANSLATE_TEXT_FONT, R.font.andika)
    }

    fun writeTranslateTextFont(font: Int) {
        mPreferences = mContext.getSharedPreferences(TRANSLATE, Context.MODE_PRIVATE)
        val editor = mPreferences!!.edit()
        editor.putInt(TRANSLATE_TEXT_FONT, font)
        editor.apply()
    }

    fun readAppTextFont(): Int {
        mPreferences = mContext.getSharedPreferences(APP, Context.MODE_PRIVATE)
        return mPreferences!!.getInt(APP_TEXT_FONT, R.font.andika)
    }

    fun writeAppTextFont(font: Int) {
        mPreferences = mContext.getSharedPreferences(APP, Context.MODE_PRIVATE)
        val editor = mPreferences!!.edit()
        editor.putInt(APP_TEXT_FONT, font)
        editor.apply()
    }

    fun readNoteTextFont(): Int {
        mPreferences = mContext.getSharedPreferences(NOTE_SORT, Context.MODE_PRIVATE)
        return mPreferences!!.getInt(NOTE_SORT_TEXT_FONT, R.font.andika)
    }

    fun writeNoteTextFont(font: Int) {
        mPreferences = mContext.getSharedPreferences(NOTE_SORT, Context.MODE_PRIVATE)
        val editor = mPreferences!!.edit()
        editor.putInt(NOTE_SORT_TEXT_FONT, font)
        editor.apply()
    }

    fun writeNoteTextSize(size: Int) {
        mPreferences = mContext.getSharedPreferences(NOTE_SORT, Context.MODE_PRIVATE)
        val editor = mPreferences!!.edit()
        editor.putInt(NOTE_SORT_TEXT_SIZE, size)
        editor.apply()
    }

    fun readNoteTextSize(): Int {
        mPreferences = mContext.getSharedPreferences(NOTE_SORT, Context.MODE_PRIVATE)
        return mPreferences!!.getInt(NOTE_SORT_TEXT_SIZE, 5)
    }

    /**
     * Игнорируются значения других тем, так как не используется в приложении [.APP_THEME]
     * @param size размер шрифта
     */
    @SuppressLint("SwitchIntDef")
    fun writeNoteTextColor(size: Int) {
        mPreferences = mContext.getSharedPreferences(NOTE_SORT, Context.MODE_PRIVATE)
        val editor = mPreferences!!.edit()
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
        mPreferences = mContext.getSharedPreferences(NOTE_SORT, Context.MODE_PRIVATE)
        when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_YES -> return mPreferences!!.getInt(NOTE_SORT_TEXT_COLOR_NIGHT, R.color.colorLight)
            AppCompatDelegate.MODE_NIGHT_NO -> return mPreferences!!.getInt(NOTE_SORT_TEXT_COLOR, R.color.colorDark)
        }
        return R.color.colorText
    }

    /**
     * Игнорируются значения других тем, так как не используется в приложении [.APP_THEME]
     */
    @SuppressLint("SwitchIntDef")
    fun writeTranslateTextColor(size: Int) {
        mPreferences = mContext.getSharedPreferences(TRANSLATE, Context.MODE_PRIVATE)
        val editor = mPreferences!!.edit()
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
        mPreferences = mContext.getSharedPreferences(TRANSLATE, Context.MODE_PRIVATE)
        when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_YES -> return mPreferences!!.getInt(TRANSLATE_SORT_TEXT_COLOR_NIGHT, R.color.colorLight)
            AppCompatDelegate.MODE_NIGHT_NO -> return mPreferences!!.getInt(TRANSLATE_SORT_TEXT_COLOR, R.color.colorDark)
        }
        return R.color.colorText
    }

    fun writeAppTextSize(size: Int) {
        mPreferences = mContext.getSharedPreferences(APP, Context.MODE_PRIVATE)
        val editor = mPreferences!!.edit()
        editor.putInt(APP_TEXT_SIZE, size)
        editor.apply()
    }

    fun readAppTextSize(): Int {
        mPreferences = mContext.getSharedPreferences(APP, Context.MODE_PRIVATE)
        return mPreferences!!.getInt(APP_TEXT_SIZE, DEFAULT_TEXT_SIZE)
    }

    fun writeTranslateTextSize(size: Int) {
        mPreferences = mContext.getSharedPreferences(TRANSLATE, Context.MODE_PRIVATE)
        val editor = mPreferences!!.edit()
        editor.putInt(TRANSLATE_SORT_TEXT_SIZE, size)
        editor.apply()
    }

    fun readTranslateTextSize(): Int {
        mPreferences = mContext.getSharedPreferences(TRANSLATE, Context.MODE_PRIVATE)
        return mPreferences!!.getInt(TRANSLATE_SORT_TEXT_SIZE, DEFAULT_TEXT_SIZE)
    }

    fun writeScreensColor(color: Int) {
        mPreferences = mContext.getSharedPreferences(APP, Context.MODE_PRIVATE)
        val editor = mPreferences!!.edit()
        editor.putInt(SCREENS_COLOR, color)
        editor.apply()
    }

    fun readScreensColor(): Int {
        mPreferences = mContext.getSharedPreferences(APP, Context.MODE_PRIVATE)
        return mPreferences!!.getInt(SCREENS_COLOR, R.color.colorPopUpBackground)
    }

    fun writeScreensTextColor(color: Int) {
        mPreferences = mContext.getSharedPreferences(APP, Context.MODE_PRIVATE)
        val editor = mPreferences!!.edit()
        editor.putInt(SCREENS_TEXT_COLOR, color)
        editor.apply()
    }

    fun readScreensTextColor(): Int {
        mPreferences = mContext.getSharedPreferences(APP, Context.MODE_PRIVATE)
        return mPreferences!!.getInt(SCREENS_TEXT_COLOR, R.color.colorText)
    }

    fun writeXTurnedScreenLocation(x: Int) {
        mPreferences = mContext.getSharedPreferences(APP, Context.MODE_PRIVATE)
        val editor = mPreferences!!.edit()
        editor.putInt(X_TURNED_SCREEN_LOCATION, x)
        editor.apply()
    }

    fun readXTurnedScreenLocation(): Int {
        mPreferences = mContext.getSharedPreferences(APP, Context.MODE_PRIVATE)
        return mPreferences!!.getInt(X_TURNED_SCREEN_LOCATION, 1000)
    }

    fun writeYTurnedScreenLocation(x: Int) {
        mPreferences = mContext.getSharedPreferences(APP, Context.MODE_PRIVATE)
        val editor = mPreferences!!.edit()
        editor.putInt(Y_TURNED_SCREEN_LOCATION, x)
        editor.apply()
    }

    fun readYTurnedScreenLocation(): Int {
        mPreferences = mContext.getSharedPreferences(APP, Context.MODE_PRIVATE)
        return mPreferences!!.getInt(Y_TURNED_SCREEN_LOCATION, -1000)
    }

    fun readTranslateReq(): String? {
        mPreferences = mContext.getSharedPreferences(TRANSLATE, Context.MODE_PRIVATE)
        return mPreferences!!.getString(TRANSLATE_REQ, "")
    }

    fun readSearchPackageBrowserName(): String {
        mPreferences = mContext.getSharedPreferences(SEARCH, Context.MODE_PRIVATE)
        return mPreferences!!.getString(SEARCH_BROWSER_PACKAGE_NAME, STRING_DEFAULT_VALUE)!!
    }

    fun readSearchBrowserName(): String? {
        mPreferences = mContext.getSharedPreferences(SEARCH, Context.MODE_PRIVATE)
        return mPreferences!!.getString(SEARCH_BROWSER_NAME, "-")
    }

    fun readSearchEngine(): Int {
        mPreferences = mContext.getSharedPreferences(SEARCH, Context.MODE_PRIVATE)
        return mPreferences!!.getInt(SEARCH_ENGINE, 0)
    }

    fun readTranslateResp(): String? {
        mPreferences = mContext.getSharedPreferences(TRANSLATE, Context.MODE_PRIVATE)
        return mPreferences!!.getString(TRANSLATE_RESP, STRING_DEFAULT_VALUE)
    }

    fun readTranslateLangFrom(): String? {
        mPreferences = mContext.getSharedPreferences(TRANSLATE, Context.MODE_PRIVATE)
        return mPreferences!!.getString(TRANSLATE_LANGUAGE_FROM, DEFAULT_TRANSLATE_LANGUAGE_FROM)
    }

    fun readTranslateLangTo(): String? {
        mPreferences = mContext.getSharedPreferences(TRANSLATE, Context.MODE_PRIVATE)
        return mPreferences!!.getString(TRANSLATE_LANGUAGE_TO, DEFAULT_TRANSLATE_LANGUAGE_TO)
    }

    //NOTE SORTS
    fun writeNoteSortType(type: Int) {
        mPreferences = mContext.getSharedPreferences(NOTE_SORT, Context.MODE_PRIVATE)
        val editor = mPreferences!!.edit()
        editor.putInt(NOTE_SORT_TYPE, type)
        editor.apply()
    }

    fun writeNoteSortIsImportant(isImportant: Boolean) {
        mPreferences = mContext.getSharedPreferences(NOTE_SORT, Context.MODE_PRIVATE)
        val editor = mPreferences!!.edit()
        editor.putBoolean(NOTE_SORT_IS_IMPORTANT, isImportant)
        editor.apply()
    }

    fun writeNoteSortTitle(title: String?) {
        mPreferences = mContext.getSharedPreferences(NOTE_SORT, Context.MODE_PRIVATE)
        val editor = mPreferences!!.edit()
        editor.putString(NOTE_SORT_BY_TITLE, title)
        editor.apply()
    }

    fun writeNoteSortTag(tag: String?) {
        mPreferences = mContext.getSharedPreferences(NOTE_SORT, Context.MODE_PRIVATE)
        val editor = mPreferences!!.edit()
        editor.putString(NOTE_SORT_BY_TAG, tag)
        editor.apply()
    }

    fun readNoteSortType(): Int {
        mPreferences = mContext.getSharedPreferences(NOTE_SORT, Context.MODE_PRIVATE)
        return mPreferences!!.getInt(NOTE_SORT_TYPE, 0)
    }

    fun readNoteSortIsImportant(): Boolean {
        mPreferences = mContext.getSharedPreferences(NOTE_SORT, Context.MODE_PRIVATE)
        return mPreferences!!.getBoolean(NOTE_SORT_IS_IMPORTANT, false)
    }

    fun readNoteSortTitle(): String {
        mPreferences = mContext.getSharedPreferences(NOTE_SORT, Context.MODE_PRIVATE)
        return mPreferences!!.getString(NOTE_SORT_BY_TITLE, STRING_DEFAULT_VALUE)!!
    }

    fun readNoteSortTag(): String {
        mPreferences = mContext.getSharedPreferences(NOTE_SORT, Context.MODE_PRIVATE)
        return mPreferences!!.getString(NOTE_SORT_BY_TAG, STRING_DEFAULT_VALUE)!!
    }
    //TRANSLATE SORT
    fun writeTranslateSortFromLang(from: String) {
        mPreferences = mContext.getSharedPreferences(NOTE_SORT, Context.MODE_PRIVATE)
        val editor = mPreferences!!.edit()
        editor.putString(TRANSLATE_SORT_LANG_FROM, from)
        editor.apply()
    }

    fun writeTranslateSortToLang(to: String) {
        mPreferences = mContext.getSharedPreferences(NOTE_SORT, Context.MODE_PRIVATE)
        val editor = mPreferences!!.edit()
        editor.putString(TRANSLATE_SORT_LANG_TO, to)
        editor.apply()
    }
    fun writeTranslateSortDescription(description: String) {
        mPreferences = mContext.getSharedPreferences(NOTE_SORT, Context.MODE_PRIVATE)
        val editor = mPreferences!!.edit()
        editor.putString(TRANSLATE_SORT_LANG_DESCRIPTION, description)
        editor.apply()
    }
    fun readTranslateSortFromLang(): String {
        mPreferences = mContext.getSharedPreferences(NOTE_SORT, Context.MODE_PRIVATE)
        return mPreferences!!.getString(TRANSLATE_SORT_LANG_FROM, STRING_DEFAULT_VALUE)!!
    }

    fun reaTranslateSortToLang(): String {
        mPreferences = mContext.getSharedPreferences(NOTE_SORT, Context.MODE_PRIVATE)
        return mPreferences!!.getString(TRANSLATE_SORT_LANG_TO, STRING_DEFAULT_VALUE)!!
    }
    fun readTranslateSortDescription(): String {
        mPreferences = mContext.getSharedPreferences(NOTE_SORT, Context.MODE_PRIVATE)
        return mPreferences!!.getString(TRANSLATE_SORT_LANG_DESCRIPTION, STRING_DEFAULT_VALUE)!!
    }
    //APP
    fun writeLastScreen(type: String?) {
        mPreferences = mContext.getSharedPreferences(APP, Context.MODE_PRIVATE)
        val editor = mPreferences!!.edit()
        editor.putString(APP_LAST_SCREEN, type)
        editor.apply()
    }

    fun readLastScreen(): String {
        mPreferences = mContext.getSharedPreferences(APP, Context.MODE_PRIVATE)
        return mPreferences!!.getString(APP_LAST_SCREEN, DEFAULT_APP_LAST_SCREEN)!!
    }

    fun writeFirstRun() {
        mPreferences = mContext.getSharedPreferences(APP, Context.MODE_PRIVATE)
        val editor = mPreferences!!.edit()
        editor.putBoolean(APP_FIRST_RUN, false)
        editor.apply()
    }

    fun readFirstRun(): Boolean {
        mPreferences = mContext.getSharedPreferences(APP, Context.MODE_PRIVATE)
        return mPreferences!!.getBoolean(APP_FIRST_RUN, true)
    }
    fun writeVersionCode(versionCode: Int) {
        mPreferences = mContext.getSharedPreferences(APP, Context.MODE_PRIVATE)
        val editor = mPreferences!!.edit()
        editor.putInt(APP_VERSION_CODE, versionCode)
        editor.apply()
    }

    fun readVersionCode(): Int {
        mPreferences = mContext.getSharedPreferences(APP, Context.MODE_PRIVATE)
        return mPreferences!!.getInt(APP_VERSION_CODE, INT_DEFAULT_VALUE)
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
}