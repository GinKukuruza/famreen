package com.example.famreen.application

import android.app.Application
import android.content.Context
import android.widget.Toast
import com.example.famreen.BuildConfig
import com.example.famreen.R
import com.example.famreen.application.di.*
import com.example.famreen.application.items.ScreenSpinnerTranslateItem
import com.example.famreen.application.logging.Logger
import com.example.famreen.application.preferences.AppPreferences
import com.example.famreen.firebase.FirebaseConnection
import com.example.famreen.firebase.FirebaseProvider
import com.example.famreen.translate.TranslateConnection
import com.example.famreen.translate.gson.TranslateLangs
import com.example.famreen.translate.gson.TranslateSupportedLangs
import com.firebase.client.Firebase
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.FirebaseDatabase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import java.util.*

class App : Application() {

    companion object {
        private lateinit var context: Context
        val appComponent: AppComponent =  DaggerAppComponent.create()
        fun getAppContext(): Context{
            return context
        }
    }


    override fun onCreate() {
        super.onCreate()
        init()
    }
    private fun init(){
        context = applicationContext
        initFirebase()
        initCrashlytics()
    }
    private fun initCrashlytics(){
        if(BuildConfig.DEBUG && BuildConfig.BUILD_TYPE == "debug"){
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false)
        }else{
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
            FirebaseCrashlytics.getInstance().setUserId(AdvertisingIdClient.getAdvertisingIdInfo(this).id)
        }
    }
    private fun initFirebase() {
        Firebase.setAndroidContext(this)
        if (FirebaseApp.getApps(this).isNotEmpty()) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        }
       /* FirebaseConnection.initUser()*/
    }
    private val languages: Unit
        get() {
            val disposables = CompositeDisposable()
            //Формат: "en"
            val lang = Locale.getDefault().language
            /*
            В запросе передается язык локализации, что бы пришел список на необходимом пользователю языке
             */disposables.add(TranslateConnection.createConnection()!!.api.getLangs(lang)!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<TranslateLangs?>() {
                    override fun onSuccess(langs: TranslateLangs) {
                        if (langs.langs != null) {
                            initLanguages(langs.langs)
                        }
                        disposables.clear()
                        disposables.dispose()
                    }

                    override fun onError(e: Throwable) {
                        Logger.log(9, "network translate exception", e)
                        Toast.makeText(this@App,"FAILED: " + e.message, Toast.LENGTH_SHORT).show()
                        disposables.clear()
                        disposables.dispose()
                    }
                }))
        }

    /**
     * Инициализация и создание коллекции, удобной для работы с Yandex Translate API
     * @param context context
     * @param langs все поддерживаемые языки Yandex Translate API [.getLanguages]
     */
    private fun initLanguages(langs: TranslateSupportedLangs?) {
        val list: MutableList<ScreenSpinnerTranslateItem?> = ArrayList()
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.af, "af"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.am, "am"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.ar, "ar"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.az, "az"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.ba, "ba"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.be, "be"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.bg, "bg"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.bn, "bn"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.bs, "bs"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.ca, "ca"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.ceb, "ceb"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.cs, "cs"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.cy, "cy"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.da, "da"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.de, "de"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.el, "el"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.en, "en"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.eo, "eo"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.es, "es"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.et, "et"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.eu, "eu"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.fa, "fa"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.fi, "fi"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.fr, "fr"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.ga, "ga"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.gd, "gd"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.gl, "gl"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.gu, "gu"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.he, "he"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.hi, "hi"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.hr, "hr"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.ht, "ht"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.hu, "hu"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.hy, "hy"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.id, "id"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.`is`, "is"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.it, "it"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.ja, "ja"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.jv, "jv"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.ka, "ka"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.kk, "kk"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.km, "km"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.kn, "kn"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.ko, "ko"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.ky, "ky"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.la, "la"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.lb, "lb"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.lo, "lo"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.lt, "lt"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.lv, "af"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.mg, "lv"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.mhr, "mhr"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.mi, "mi"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.mk, "mk"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.ml, "ml"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.mn, "mn"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.mr, "mr"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.mrj, "mrj"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.ms, "ma"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.mt, "mt"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.my, "my"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.ne, "ne"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.nl, "nl"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.no, "no"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.pa, "pa"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.pap, "pap"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.pl, "pl"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.pt, "pt"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.ro, "ro"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.ru, "ru"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.si, "si"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.sk, "sk"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.sl, "sl"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.sq, "sq"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.sr, "sr"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.su, "su"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.sv, "sv"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.sw, "sw"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.ta, "ta"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.te, "te"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.tg, "tg"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.th, "th"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.tl, "tl"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.tr, "tr"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.tt, "tt"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.udm, "udm"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.uk, "uk"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.ur, "ur"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.uz, "uz"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.vi, "vi"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.xh, "xh"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.yi, "yi"))
        list.add(ScreenSpinnerTranslateItem.createItem(langs?.zh, "zh"))
        /*Collections.sort(list, TranslateSpinnerComparator())*/

    }
}