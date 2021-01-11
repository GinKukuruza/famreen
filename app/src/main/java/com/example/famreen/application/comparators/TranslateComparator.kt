package com.example.famreen.application.comparators

import com.example.famreen.application.items.TranslateItem
import java.util.*

class TranslateComparator {

        fun sortByLangFrom(collection: List<TranslateItem>, lang_from: String): List<TranslateItem> {
            if (collection.isEmpty() || lang_from == "") return collection
            val list: MutableList<TranslateItem> = ArrayList()
            for (i in collection.indices) {
                val length = lang_from.length
                if (collection[i].from_lang?.length!! >= length) {
                    if (collection[i].from_lang?.toLowerCase(Locale.getDefault())!!.contains(lang_from.toLowerCase(
                            Locale.getDefault()))) {
                        list.add(collection[i])
                    }
                }
            }
            return list
        }

        fun sortByLangTo(collection: List<TranslateItem>, lang_to: String): List<TranslateItem> {
            if (collection.isEmpty() || lang_to == "") return collection
            val list: MutableList<TranslateItem> = ArrayList()
            for (i in collection.indices) {
                val length = lang_to.length
                if (collection[i].to_lang?.length!! >= length) {
                    if (collection[i].to_lang?.toLowerCase(Locale.getDefault())!!.contains(lang_to.toLowerCase(
                            Locale.getDefault()))) {
                        list.add(collection[i])
                    }
                }
            }
            return list
        }

        fun sortByDescription(collection: List<TranslateItem>, desc: String): List<TranslateItem> {
            if (collection.isEmpty() || desc == "") return collection
            val list: MutableList<TranslateItem> = ArrayList()
            for (i in collection.indices) {
                if (collection[i].from_translate?.length!! >= desc.length) {
                    if (collection[i].from_translate?.toLowerCase(Locale.getDefault())!!.contains(desc.toLowerCase(
                            Locale.getDefault()))) {
                        list.add(collection[i])
                    }
                }
                if (collection[i].to_translate?.length!! >= desc.length) {
                    if (collection[i].to_translate?.toLowerCase(Locale.getDefault())!!.contains(desc.toLowerCase(
                            Locale.getDefault()))) {
                        if (!list.contains(collection[i])) {
                            list.add(collection[i])
                        }
                    }
                }
            }
            return list
        }
}