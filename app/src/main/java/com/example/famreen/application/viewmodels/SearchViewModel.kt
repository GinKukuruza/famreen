package com.example.famreen.application.viewmodels

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.example.famreen.application.App
import com.example.famreen.states.States
import com.example.famreen.application.items.SearchItem
import com.example.famreen.utils.extensions.default

class SearchViewModel {
    private val mState = MutableLiveData<States>().default(initialValue = States.DefaultState())
    /**
     * Вызывается для получения списка с основной информацией о браузерах
     * **/
    fun getSearchList(): MutableList<SearchItem>{
        val list: MutableList<SearchItem> = ArrayList()
        val packageManager = App.getAppContext().packageManager
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://"))
        val apps = packageManager.queryIntentActivities(intent, 0)

        for (app in apps) {
            if (packageManager.getLaunchIntentForPackage(app.resolvePackageName) != null) {
                val item = SearchItem()
                item.mImage = app.loadIcon(packageManager)
                item.mName = app.loadLabel(packageManager).toString()
                item.mPackageName = app.activityInfo.packageName
                list.add(item)
            }
        }
        return list
    }
    /**
     * **/
    fun getState() = mState
}