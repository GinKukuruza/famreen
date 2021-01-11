package com.example.famreen.application.comparators

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.famreen.application.items.NoteItem
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.function.Predicate
import java.util.stream.Collectors


class NoteFilterComparator : Comparator<NoteItem> {
    private var STATE = ""

    private constructor() {}
    private constructor(state: String) {
        STATE = state
    }

    override fun compare(o1: NoteItem, o2: NoteItem): Int {
        when (STATE) {
            SORT_BY_DATA_UP -> {
                val sdf = SimpleDateFormat("EEEE, MMMM d, yyyy 'at' h:mm a", Locale.getDefault())
                try {
                    val date1 = sdf.parse(o1.time)
                    val date2 = sdf.parse(o2.time)
                    return date2.compareTo(date1)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }
            SORT_BY_DATA_DOWN -> {
                val sdfd = SimpleDateFormat("EEEE, MMMM d, yyyy 'at' h:mm a", Locale.getDefault())
                try {
                    val date1 = sdfd.parse(o1.time)
                    val date2 = sdfd.parse(o2.time)
                    return date1.compareTo(date2)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }
            SORT_BY_TITLE_UP -> return o2.title!!.compareTo(o1.title!!)
            SORT_BY_TITLE_DOWN -> return o1.title!!.compareTo(o2.title!!)
            SORT_BY_TAG_UP -> return o2.tag!!.compareTo(o1.tag!!)
            SORT_BY_TAG_DOWN -> return o1.tag!!.compareTo(o2.tag!!)
            SORT_BY_IMPORTANT_UP -> return compareValues(o2.important, o1.important)
            SORT_BY_IMPORTANT_DOWN -> return compareValues(o1.important, o2.important)
            SORT_BY_TAG -> {
                if (o1.tag!!.contains(o2.tag!!)) {
                    return 0
                }
                if (o1.tag == null) {
                    return -1
                }
                return if (o2.tag == null) {
                    1
                } else o1.tag!!.compareTo(o2.tag!!)
            }
            SORT_BY_TITLE -> {
                if (o1.title!!.contains(o2.title!!)) {
                    return 0
                }
                if (o1.title == null) {
                    return -1
                }
                return if (o2.title == null) {
                    1
                } else o1.title!!.compareTo(o2.title!!)
            }
        }
        return 0
    }

    companion object {
        private const val SORT_BY_DATA_UP = "SORT_BY_DATA_UP"
        private const val SORT_BY_DATA_DOWN = "SORT_BY_DATA_DOWN"
        private const val SORT_BY_TITLE_UP = "SORT_BY_TITLE_UP"
        private const val SORT_BY_TITLE_DOWN = "SORT_BY_TITLE_DOWN"
        private const val SORT_BY_TAG_UP = "SORT_BY_TAG_UP"
        private const val SORT_BY_TAG_DOWN = "SORT_BY_TAG_DOWN"
        private const val SORT_BY_IMPORTANT_UP = "SORT_BY_IMPORTANT_UP"
        private const val SORT_BY_IMPORTANT_DOWN = "SORT_BY_IMPORTANT_DOWN"
        private const val SORT_BY_TITLE = "SORT_BY_TITLE"
        private const val SORT_BY_TAG = "SORT_BY_TAG"
        private fun createInstance(state: String): NoteFilterComparator {
            return NoteFilterComparator(state)
        }

        @JvmStatic
        fun sortByDataUp(collection: List<NoteItem?>?) {
            Collections.sort(collection, createInstance(SORT_BY_DATA_UP) as Comparator<in NoteItem?>)
        }

        @JvmStatic
        fun sortByDataDown(collection: List<NoteItem?>?) {
            Collections.sort(collection, createInstance(SORT_BY_DATA_DOWN) as Comparator<in NoteItem?>)
        }

        @JvmStatic
        fun sortByTitleUp(collection: List<NoteItem?>?) {
            Collections.sort(collection, createInstance(SORT_BY_TITLE_UP) as Comparator<in NoteItem?>)
        }

        @JvmStatic
        fun sortByTitleDown(collection: List<NoteItem?>?) {
            Collections.sort(collection, createInstance(SORT_BY_TITLE_DOWN) as Comparator<in NoteItem?>)
        }

        @JvmStatic
        fun sortByTagUp(collection: List<NoteItem?>?) {
            Collections.sort(collection, createInstance(SORT_BY_TAG_UP) as Comparator<in NoteItem?>)
        }

        @JvmStatic
        fun sortByTagDown(collection: List<NoteItem?>?) {
            Collections.sort(collection, createInstance(SORT_BY_TAG_DOWN) as Comparator<in NoteItem?>)
        }

        @JvmStatic
        fun sortByImportantUp(collection: List<NoteItem?>?) {
            Collections.sort(collection, createInstance(SORT_BY_IMPORTANT_UP) as Comparator<in NoteItem?>)
        }

        @JvmStatic
        fun sortByImportantDown(collection: List<NoteItem?>?) {
            Collections.sort(collection, createInstance(SORT_BY_IMPORTANT_DOWN) as Comparator<in NoteItem?>)
        }

        @JvmStatic
        @RequiresApi(api = Build.VERSION_CODES.N)
        fun sortOnlyImportant(collection: List<NoteItem>): List<NoteItem> {
            val byImportant = Predicate { obj: NoteItem -> obj.important }
            return collection.stream().filter(byImportant).collect(Collectors.toList())
        }

        @JvmStatic
        @RequiresApi(api = Build.VERSION_CODES.N)
        fun sortAllImportant(collection: List<NoteItem>): List<NoteItem> {
            val byNotImportant = Predicate { _: NoteItem? -> true }
            return collection.stream().filter(byNotImportant).collect(Collectors.toList())
        }

        @JvmStatic
        fun sortByTag(collection: List<NoteItem>, tag: String): List<NoteItem> {
            val list: MutableList<NoteItem> = ArrayList()
            if (collection.isEmpty()) return list
            for (i in collection.indices) {
                val length = tag.length
                if (collection[i].tag!!.length >= length) {
                    if (collection[i].tag!!.toLowerCase(Locale.getDefault()).contains(tag.toLowerCase(
                            Locale.getDefault()))) {
                        list.add(collection[i])
                    }
                }
            }
            return list
        }

        @JvmStatic
        @Throws(NullPointerException::class)
        fun sortByTitle(collection: List<NoteItem>, title: String): List<NoteItem> {
            val list: MutableList<NoteItem> = ArrayList()
            if (collection.isEmpty()) return list
            for (i in collection.indices) {
                val length = title.length
                if (collection[i].title!!.length >= length) if (collection[i].title!!.toLowerCase(
                        Locale.getDefault()).contains(title.toLowerCase(Locale.getDefault()))) {
                    list.add(collection[i])
                }
            }
            return list
        }
    }
}