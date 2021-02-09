package com.example.famreen.utils

import android.content.Context
import androidx.navigation.NavOptions
import com.example.famreen.R
import com.example.famreen.application.App
import com.example.famreen.application.items.ScreenSpinnerTranslateItem
import com.example.famreen.translateApi.gson.TranslateSupportedLangs
import java.text.SimpleDateFormat
import java.util.*


object Utils {
    const val STRING_ARR = ""
    fun getNoteTime(): String {
        val calendar = Calendar.getInstance()
        val datePattern = getStringFromResourcesByName(
            App.getAppContext(),
            R.string.diary_time_pattern
        )
        val format = SimpleDateFormat(datePattern, Locale.getDefault())
        return format.format(calendar.time)
    }
    private fun getStringFromResourcesByName(context: Context, strId: Int): String {
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


}