package com.example.famreen.application.items

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class ScreenSpinnerTranslateItem {
    @PrimaryKey(autoGenerate = true)
    var mId = 0
    var mLangUI: String? = null
    var mLangName: String? = null

    companion object {
        fun createItem(name: String?, ui: String): ScreenSpinnerTranslateItem {
            val item = ScreenSpinnerTranslateItem()
            item.mLangName = name
            item.mLangUI = ui
            return item
        }
    }
}