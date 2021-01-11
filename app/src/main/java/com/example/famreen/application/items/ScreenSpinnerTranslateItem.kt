package com.example.famreen.application.items

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
open class ScreenSpinnerTranslateItem {
    @PrimaryKey(autoGenerate = true)
    var id = 0
    var langUI: String? = null
    var langName: String? = null

    companion object {
        fun createItem(name: String?, ui: String): ScreenSpinnerTranslateItem {
            val item = ScreenSpinnerTranslateItem()
            item.langName = name
            item.langUI = ui
            return item
        }
    }
}