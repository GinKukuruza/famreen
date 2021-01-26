package com.example.famreen.application.comparators

import com.example.famreen.application.items.TranslateItem
import java.util.*

class TranslateComparator {
    /**
     *Сортировка по языку из которого был сделан перевод, ищет по совпадениям
     **/
        fun sortByLangFrom(collection: List<TranslateItem>, lang_from: String): List<TranslateItem> {
            if (collection.isEmpty() || lang_from == "") return collection
            val list: MutableList<TranslateItem> = ArrayList()
            for (i in collection.indices) {
                val length = lang_from.length
                if (collection[i].mFrom_lang?.length!! >= length) {
                    if (collection[i].mFrom_lang?.toLowerCase(Locale.getDefault())!!.contains(lang_from.toLowerCase(Locale.getDefault()))) {
                        list.add(collection[i])
                    }
                }
            }
            return list
        }
    /**
     *Сортировка по языку в который был сделан перевод, ищет по совпадениям
     **/
        fun sortByLangTo(collection: List<TranslateItem>, lang_to: String): List<TranslateItem> {
            if (collection.isEmpty() || lang_to == "") return collection
            val list: MutableList<TranslateItem> = ArrayList()
            for (i in collection.indices) {
                val length = lang_to.length
                if (collection[i].mTo_lang?.length!! >= length) {
                    if (collection[i].mTo_lang?.toLowerCase(Locale.getDefault())!!.contains(lang_to.toLowerCase(Locale.getDefault()))) {
                        list.add(collection[i])
                    }
                }
            }
            return list
        }
    /**
     *Сортировка по переводу, ищет по совпадениям в переводе
     **/
        fun sortByDescription(collection: List<TranslateItem>, desc: String): List<TranslateItem> {
            if (collection.isEmpty() || desc == "") return collection
            val list: MutableList<TranslateItem> = ArrayList()
            for (i in collection.indices) {
                if (collection[i].mFrom_translate?.length!! >= desc.length) {
                    if (collection[i].mFrom_translate?.toLowerCase(Locale.getDefault())!!.contains(desc.toLowerCase(
                            Locale.getDefault()))) {
                        list.add(collection[i])
                    }
                }
                if (collection[i].mTo_translate?.length!! >= desc.length) {
                    if (collection[i].mTo_translate?.toLowerCase(Locale.getDefault())!!.contains(desc.toLowerCase(Locale.getDefault()))) {
                        if (!list.contains(collection[i])) {
                            list.add(collection[i])
                        }
                    }
                }
            }
            return list
        }
}