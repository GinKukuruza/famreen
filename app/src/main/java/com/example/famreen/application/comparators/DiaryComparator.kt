package com.example.famreen.application.comparators

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.famreen.application.items.NoteItem
import retrofit2.http.Field
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.function.Predicate
import java.util.stream.Collectors


class DiaryComparator : Comparator<NoteItem> {
    private var state: String = "SORT_BY_DATA_UP"
    private val SORT_BY_DATA_UP = "SORT_BY_DATA_UP"
    private val SORT_BY_DATA_DOWN = "SORT_BY_DATA_DOWN"
    private val SORT_BY_TITLE_UP = "SORT_BY_TITLE_UP"
    private val SORT_BY_TITLE_DOWN = "SORT_BY_TITLE_DOWN"
    private val SORT_BY_TAG_UP = "SORT_BY_TAG_UP"
    private val SORT_BY_TAG_DOWN = "SORT_BY_TAG_DOWN"
    private val SORT_BY_IMPORTANT_UP = "SORT_BY_IMPORTANT_UP"
    private val SORT_BY_IMPORTANT_DOWN = "SORT_BY_IMPORTANT_DOWN"
    private val SORT_BY_TITLE = "SORT_BY_TITLE"
    private val SORT_BY_TAG = "SORT_BY_TAG"
    override fun compare(o1: NoteItem, o2: NoteItem): Int {
        when (state) {
            SORT_BY_DATA_UP -> {
                val dataFormat = SimpleDateFormat("EEEE, MMMM d, yyyy 'at' h:mm a", Locale.getDefault())
                try {
                    val date1 = dataFormat.parse(o1.time)
                    val date2 = dataFormat.parse(o2.time)
                    return date2.compareTo(date1)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }
            SORT_BY_DATA_DOWN -> {
                val dataFormat = SimpleDateFormat("EEEE, MMMM d, yyyy 'at' h:mm a", Locale.getDefault())
                try {
                    val date1 = dataFormat.parse(o1.time)
                    val date2 = dataFormat.parse(o2.time)
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

        fun sortByDataUp(collection: List<NoteItem>?) {
            changeState(SORT_BY_DATA_UP)
            Collections.sort(collection, this)
        }

        fun sortByDataDown(collection: List<NoteItem>?) {
            changeState(SORT_BY_DATA_DOWN)
            Collections.sort(collection, this)
        }

        fun sortByTitleUp(collection: List<NoteItem>?) {
            changeState(SORT_BY_TITLE_UP)
            Collections.sort(collection, this)
        }

        fun sortByTitleDown(collection: List<NoteItem>?) {
            changeState(SORT_BY_TITLE_DOWN)
            Collections.sort(collection, this)
        }

        fun sortByTagUp(collection: List<NoteItem>?) {
            changeState(SORT_BY_TAG_UP)
            Collections.sort(collection, this)
        }

        fun sortByTagDown(collection: List<NoteItem>?) {
            changeState(SORT_BY_TAG_DOWN)
            Collections.sort(collection, this)
        }

        fun sortByImportantUp(collection: List<NoteItem>?) {
            changeState(SORT_BY_IMPORTANT_UP)
            Collections.sort(collection, this)
        }

        fun sortByImportantDown(collection: List<NoteItem>?) {
            changeState(SORT_BY_IMPORTANT_DOWN)
            Collections.sort(collection, this)
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        fun sortOnlyImportant(collection: List<NoteItem>): List<NoteItem> {
            val byImportant = Predicate { obj: NoteItem -> obj.important }
            return collection.stream().filter(byImportant).collect(Collectors.toList())
        }

        fun sortOnlyImportantLowerApi24(collection: List<NoteItem>): List<NoteItem> {
            return collection.sortedBy {obj: NoteItem -> obj.important }
       }

        @RequiresApi(api = Build.VERSION_CODES.N)
        fun sortAllImportant(collection: List<NoteItem>): List<NoteItem> {
            val byNotImportant = Predicate { _: NoteItem -> true }
            return collection.stream().filter(byNotImportant).collect(Collectors.toList())
        }

         fun sortAllImportantLowerApi24(collection: List<NoteItem>): List<NoteItem> {
            return collection.sortedBy {obj: NoteItem -> obj.important }
         }


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
    private fun changeState(state: String){
        this.state = state
    }
}