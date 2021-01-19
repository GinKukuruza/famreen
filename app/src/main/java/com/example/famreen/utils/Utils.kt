package com.example.famreen.utils

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Debug
import androidx.navigation.NavOptions
import com.example.famreen.BuildConfig
import com.example.famreen.R
import com.example.famreen.application.App
import com.example.famreen.application.items.ScreenSpinnerTranslateItem
import com.example.famreen.application.logging.Logger
import com.example.famreen.application.preferences.AppPreferences
import com.example.famreen.translateApi.gson.TranslateSupportedLangs
import dalvik.system.DexFile
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.ZipFile
import kotlin.collections.HashSet

object Utils {
    private var failedFindBuildDotPropUsingReflection = false
    private var getPropMethod: Method? = null
    private val tag = Utils::class.java.name

    private val GENY_FILES = arrayOf(
        "/dev/socket/genyd",
        "/dev/socket/baseband_genyd"
    )
    private val PIPES = arrayOf(
        "/dev/socket/qemud",
        "/dev/qemu_pipe"
    )
    private val ANDY_FILES = arrayOf(
        "fstab.andy",
        "ueventd.andy.rc"
    )

    private val NOX_FILES = arrayOf(
        "fstab.nox",
        "init.nox.rc",
        "ueventd.nox.rc"
    )

    fun getNoteTime(): String {
        val calendar = Calendar.getInstance()
        val datePattern = getStringFromResourcesByName(
            App.getAppContext(),
            R.string.diary_time_pattern
        )
        val format = SimpleDateFormat(datePattern, Locale.getDefault())
        return format.format(calendar.time)
    }
    fun getStringFromResourcesByName(context: Context, strId: Int): String {
        return context.resources.getString(strId)
    }
    fun getDefaultNavigationOptions(): NavOptions{
        return NavOptions.Builder()
            .setEnterAnim(R.anim.fragment_swipe_to_right_from_l)
            .setExitAnim(R.anim.fragment_swipe_to_left_from_l)
            .setPopEnterAnim(R.anim.fragment_swipe_to_right_from_l)
            .setPopExitAnim(R.anim.fragment_swipe_to_left_from_l)
            .setLaunchSingleTop(true)
            .build()
    }
    fun initLanguages(languages: TranslateSupportedLangs?) : List<ScreenSpinnerTranslateItem> {
        val list: MutableList<ScreenSpinnerTranslateItem> = ArrayList()
        languages?.let {
            list.add(ScreenSpinnerTranslateItem.createItem(it.af, "af"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.am, "am"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.ar, "ar"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.az, "az"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.ba, "ba"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.be, "be"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.bg, "bg"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.bn, "bn"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.bs, "bs"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.ca, "ca"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.ceb, "ceb"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.cs, "cs"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.cy, "cy"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.da, "da"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.de, "de"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.el, "el"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.en, "en"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.eo, "eo"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.es, "es"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.et, "et"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.eu, "eu"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.fa, "fa"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.fi, "fi"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.fr, "fr"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.ga, "ga"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.gd, "gd"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.gl, "gl"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.gu, "gu"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.he, "he"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.hi, "hi"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.hr, "hr"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.ht, "ht"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.hu, "hu"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.hy, "hy"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.id, "id"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.`is`, "is"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.it, "it"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.ja, "ja"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.jv, "jv"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.ka, "ka"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.kk, "kk"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.km, "km"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.kn, "kn"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.ko, "ko"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.ky, "ky"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.la, "la"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.lb, "lb"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.lo, "lo"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.lt, "lt"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.lv, "af"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.mg, "lv"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.mhr, "mhr"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.mi, "mi"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.mk, "mk"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.ml, "ml"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.mn, "mn"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.mr, "mr"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.mrj, "mrj"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.ms, "ma"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.mt, "mt"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.my, "my"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.ne, "ne"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.nl, "nl"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.no, "no"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.pa, "pa"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.pap, "pap"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.pl, "pl"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.pt, "pt"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.ro, "ro"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.ru, "ru"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.si, "si"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.sk, "sk"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.sl, "sl"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.sq, "sq"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.sr, "sr"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.su, "su"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.sv, "sv"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.sw, "sw"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.ta, "ta"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.te, "te"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.tg, "tg"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.th, "th"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.tl, "tl"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.tr, "tr"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.tt, "tt"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.udm, "udm"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.uk, "uk"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.ur, "ur"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.uz, "uz"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.vi, "vi"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.xh, "xh"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.yi, "yi"))
            list.add(ScreenSpinnerTranslateItem.createItem(it.zh, "zh"))
            /*Collections.sort(list, TranslateSpinnerComparator())*/
            return list
        }
        return emptyList()
    }
    fun checkRoot() : Boolean{
        @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        for(path in System.getenv("PATH").split(":")){
            if(File(path, "su").exists()) {
                return Logger.wtf("device is rooted")
            }
        }
        return Logger.wtf()
    }
    fun checkRunningProcesses(manager: ActivityManager) : Boolean{
        val list = manager.runningAppProcesses
        var tmp = ""
        for(process in list){
            tmp = process.processName
            if(tmp.contains("supersu") || tmp.contains("superuser")){
                return Logger.wtf("device is rooted")
            }
        }
        return Logger.wtf()
    }
    fun checkInstalledPackages() : Boolean{
        val packages = App.getAppContext().packageManager.getInstalledPackages(PackageManager.GET_PROVIDERS)
        val malwarePackages = arrayOf(
            "com.thirdparty.superuser",
            "eu.chainfire.supersu",
            "com.noshufou.android.su",
            "com.koushikdutta.superuser",
            "com.zachspong.temprootremovejb",
            "com.ramdroid.appquarantine",
            "com.topjohnwu.magisk"
        )
        for(pack in packages){
            if (malwarePackages.contains(pack.packageName))return Logger.wtf("device is rooted")
        }
        return Logger.wtf()
    }
    fun checkInstalledApplications() : Boolean{
        val packages = App.getAppContext().packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        for(appInfo in packages){
            when(appInfo.packageName){
                "de.robv.android.xposed.installer" -> return Logger.wtf("Xposed detected")
                "com.saurik.substrate" -> return Logger.wtf("Substrate detected")
            }
        }
        return Logger.wtf()
    }
    fun isTestKeyBuild() : Boolean {
       val tags = Build.TAGS
        return if(tags != null && tags.contains("test-keys")) Logger.wtf("is test build") else Logger.wtf()
    }
    fun isDebuggable() : Boolean{
        return if(App.getAppContext().applicationInfo.flags.and(ApplicationInfo.FLAG_DEBUGGABLE) != 0) Logger.wtf(
            "device is debuggable"
        ) else Logger.wtf()
    }
    fun isDebuggerConnected() : Boolean{
        return if(Debug.isDebuggerConnected()) return Logger.wtf("debugger is connected") else Logger.wtf()
    }
    fun detectThreadCpuTimeNanos() : Boolean{
        val start = Debug.threadCpuTimeNanos()
        for(i in 0..1000000) continue
        val stop = Debug.threadCpuTimeNanos()
        return if(stop - start >= 10000000) return Logger.wtf("start time is out") else Logger.wtf()
    }
    fun testCRC() : Boolean{//TODO set dex string
        val long = App.getAppContext().getString(R.string.dex_crc).toLong()
        val zf = ZipFile(App.getAppContext().packageCodePath)
        val ze = zf.getEntry("classes.dex")
        return if(ze.crc != long) return Logger.wtf("crc string is wrong") else Logger.wtf()
    }
    fun secCheck(){
        try {
        throw Exception("check method$504i5459ds4848553453")
        }catch (e: Exception){
            var callCount = 0
            for(elem in e.stackTrace){
                if(elem.className == "com.android.internal.os.ZygoteInit") {
                    callCount++;
                    if(callCount == 2) {
                        Logger.wtf("Substrate is active")
                    }
                }
                if(elem.className == "com.saurik.substrate.MS$2" && elem.methodName == "invoked") {
                    Logger.wtf("A method has been hooked using Substrate.")
                }
                if(elem.className == "com.cigital.freak.Freak$1$1" && elem.methodName == "invoked") {
                    Logger.wtf("A method has been hooked using Substrate.")
                }
                if(elem.className == "de.robv.android.xposed.XposedBridge" && elem.methodName == "main") {
                    Logger.wtf("Xposed is active")
                }
                if(elem.className == "de.robv.android.xposed.XposedBridge" && elem.methodName == "handleHookedMethod") {
                    Logger.wtf("A method has been hooked using Xposed.")
                }
                if(elem.className == "de.robv.android.xposed.XposedBridge" && elem.methodName == "invokeOriginalMethodNative") {
                    Logger.wtf("An original method has been hooked using Xposed.")
                }
            }
        }
    }
    //работает только на среде выполнения Davlik, на ART нет необходимости переводить методы в нейтив при хуке
    //WARN: если в проекте есть native методы, то нужно сделать для них исключения в виде вставки
    fun checkHookedMethodCallback(){
        val packages = App.getAppContext().packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        for(appInfo in packages){
            if (appInfo.processName == "com.example.famreen") {
                val classes = HashSet<String>()
                var dex: DexFile
                try {
                    dex =  DexFile(appInfo.sourceDir)
                    val entries = dex.entries()
                    while(entries.hasMoreElements()) {
                        val entry = entries.nextElement()
                        classes.add(entry)
                    }
                    dex.close();
                }catch (e: IOException){
                    Logger.e(tag, e.message.toString(), null)
                }
                for(name in classes){
                    if(name.startsWith("com.example.famreen")){
                        try {
                            val clazz = Class.forName(name)
                            for(method in clazz.declaredMethods){
                                if(Modifier.isNative(method.modifiers)){
                                    Logger.wtf("found native method, could be hooked")
                                }
                            }
                        }catch (e: ClassNotFoundException){
                            Logger.wtf("Class not found")
                        }
                    }
                }
            }
        }
    }
    fun pidCheck(){
        try{
            val libs = HashSet<String>()
            val mapsFileName = "/proc/" + android.os.Process.myPid() + "/maps"
            File(mapsFileName).useLines {
                val line = it.toString()
                if (line.endsWith(".so") || line.endsWith(".jar")) {
                    val n = line.lastIndexOf(" ")
                    libs.add(line.substring(n + 1))
                }
            }
            for(lib in libs){
                if(lib.contains("com.saurik.substrate")) {
                    Logger.wtf("Substrate shared object found: " + lib)
                }
                if(lib.contains("XposedBridge.jar")) {
                    Logger.wtf("Xposed jar found: " + lib)
                }
            }
        }catch (e: Exception){
            Logger.wtf(e.message.toString())
        }
    }
    fun isEmulator() : Boolean{
        val filesCheck = checkFiles(PIPES)
                || checkFiles(GENY_FILES)
                || checkFiles(ANDY_FILES)
                || checkFiles(NOX_FILES)
        return (Build.FINGERPRINT.startsWith("google/sdk_gphone_")
                && Build.FINGERPRINT.endsWith(":user/release-keys")
                && Build.MANUFACTURER == "Google" && Build.PRODUCT.startsWith("sdk_gphone_") && Build.BRAND == "google"
                && Build.MODEL.startsWith("sdk_gphone_"))
                || "QC_Reference_Phone" == Build.BOARD && !"Xiaomi".equals(Build.MANUFACTURER, ignoreCase = true)
                || Build.HOST.startsWith("Build")
                ||(Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.PRODUCT.contains("sdk_google")
                || Build.PRODUCT.contains("google_sdk")
                || Build.PRODUCT.contains("sdk")
                || Build.PRODUCT.contains("sdk_x86")
                || Build.PRODUCT.contains("vbox86p")
                || Build.PRODUCT.contains("emulator")
                || Build.PRODUCT.contains("simulator")
                // another Android SDK emulator check
                || getProp("ro.kernel.qemu") == "1"
                || filesCheck
    }
    private fun checkFiles(filesPaths: Array<String>): Boolean {
        for (pipe in filesPaths) {
            val file = File(pipe)
            if (file.exists()) {
                return Logger.wtf("file exists, device running on emulator")
            }
        }
        return Logger.wtf()
    }

    @SuppressLint("PrivateApi")
    private fun getProp(propName: String, defaultResult: String = ""): String {
        if (!failedFindBuildDotPropUsingReflection)
            try {
            if (getPropMethod == null) {
                val clazz = Class.forName("android.os.SystemProperties")
                getPropMethod = clazz.getMethod("get", String::class.java, String::class.java)
            }
            return getPropMethod!!.invoke(null, propName, defaultResult) as String? ?: defaultResult
        } catch (e: Exception) {
                Logger.e(tag, "io exception for build.prop via reflection", e.message.toString())
        }
        var process: Process? = null
        try {
            process = Runtime.getRuntime().exec("getprop \"$propName\" \"$defaultResult\"")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            return reader.readLine()
        } catch (e: IOException) {
            Logger.e(tag, "io exception for build.prop via file reader", e.message.toString())
        } finally {
            process?.destroy()
        }
        return defaultResult
    }

}