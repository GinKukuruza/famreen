package com.example.famreen.application.comparators

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.famreen.application.items.NoteItem
import com.example.famreen.application.logging.Logger
import retrofit2.http.Field
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.function.Predicate
import java.util.stream.Collectors

class DiaryComparator : Comparator<NoteItem> {
    private var mState: String = "SORT_BY_DATA_UP"
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
        when (mState) {
            SORT_BY_DATA_UP -> {
                val dataFormat = SimpleDateFormat("EEEE, MMMM d, yyyy 'at' h:mm a", Locale.getDefault())
                try {
                    val date1 = dataFormat.parse(o1.mTime)
                    val date2 = dataFormat.parse(o2.mTime)
                    return date2.compareTo(date1)
                } catch (e: ParseException) {
                    Logger.log(8,"comparator parse data exception",e)
                }
            }
            SORT_BY_DATA_DOWN -> {
                val dataFormat = SimpleDateFormat("EEEE, MMMM d, yyyy 'at' h:mm a", Locale.getDefault())
                try {
                    val date1 = dataFormat.parse(o1.mTime)
                    val date2 = dataFormat.parse(o2.mTime)
                    return date1.compareTo(date2)
                } catch (e: ParseException) {
                    Logger.log(8,"comparator parse data exception",e)
                }
            }
            SORT_BY_TITLE_UP -> return o2.mTitle!!.compareTo(o1.mTitle!!)
            SORT_BY_TITLE_DOWN -> return o1.mTitle!!.compareTo(o2.mTitle!!)
            SORT_BY_TAG_UP -> return o2.mTag!!.compareTo(o1.mTag!!)
            SORT_BY_TAG_DOWN -> return o1.mTag!!.compareTo(o2.mTag!!)
            SORT_BY_IMPORTANT_UP -> return compareValues(o2.mImportant, o1.mImportant)
            SORT_BY_IMPORTANT_DOWN -> return compareValues(o1.mImportant, o2.mImportant)
            SORT_BY_TAG -> {
                if (o1.mTag!!.contains(o2.mTag!!)) {
                    return 0
                }
                if (o1.mTag == null) {
                    return -1
                }
                return if (o2.mTag == null) {
                    1
                } else o1.mTag!!.compareTo(o2.mTag!!)
            }
            SORT_BY_TITLE -> {
                if (o1.mTitle!!.contains(o2.mTitle!!)) {
                    return 0
                }
                if (o1.mTitle == null) {
                    return -1
                }
                return if (o2.mTitle == null) {
                    1
                } else o1.mTitle!!.compareTo(o2.mTitle!!)
            }
        }
        return 0
    }
    private fun changeState(state: String){
        this.mState = state
    }
    /**
     *Сортировка по дате нисходящая
     **/
        fun sortByDataUp(collection: List<NoteItem>) {
            changeState(SORT_BY_DATA_UP)
            Collections.sort(collection, this)
        }
    /**
     *Сортировка по дате восходящая
     **/
        fun sortByDataDown(collection: List<NoteItem>) {
            changeState(SORT_BY_DATA_DOWN)
            Collections.sort(collection, this)
        }
    /**
     *Сортировка по заголовку нисходящая
     **/
        fun sortByTitleUp(collection: List<NoteItem>) {
            changeState(SORT_BY_TITLE_UP)
            Collections.sort(collection, this)
        }
    /**
     *Сортировка по заголовку восходящая
     **/
        fun sortByTitleDown(collection: List<NoteItem>) {
            changeState(SORT_BY_TITLE_DOWN)
            Collections.sort(collection, this)
        }
    /**
     *Сортировка по тегу нисходящая
     **/
        fun sortByTagUp(collection: List<NoteItem>) {
            changeState(SORT_BY_TAG_UP)
            Collections.sort(collection, this)
        }
    /**
     *Сортировка по тегу восходящая
     **/
        fun sortByTagDown(collection: List<NoteItem>) {
            changeState(SORT_BY_TAG_DOWN)
            Collections.sort(collection, this)
        }
    /**
     *Сортировка по важности, сначала важные
     **/
        fun sortByImportantUp(collection: List<NoteItem>) {
            changeState(SORT_BY_IMPORTANT_UP)
            Collections.sort(collection, this)
        }
    /**
     *Сортировка по важности, сначала не важные
     **/
        fun sortByImportantDown(collection: List<NoteItem>) {
            changeState(SORT_BY_IMPORTANT_DOWN)
            Collections.sort(collection, this)
        }
    /**
     *Сортировка по важности, оставляет только важные объекты
     * API >= 24
     **/
        @RequiresApi(api = Build.VERSION_CODES.N)
        fun sortOnlyImportant(collection: List<NoteItem>): List<NoteItem> {
            val byImportant = Predicate { obj: NoteItem -> obj.mImportant }
            return collection.stream().filter(byImportant).collect(Collectors.toList())
        }
    /**
     *Сортировка по важности, оставляет только важные объекты
     * API < 24
     **/
        fun sortOnlyImportantLowerApi24(collection: List<NoteItem>): List<NoteItem> {
            return collection.sortedBy {obj: NoteItem -> obj.mImportant }
       }
    /**
     *Сортировка по важности, оставляет все объекты
     * API >= 24
     **/
        @RequiresApi(api = Build.VERSION_CODES.N)
        fun sortAllImportant(collection: List<NoteItem>): List<NoteItem> {
            val byNotImportant = Predicate { _: NoteItem -> true }
            return collection.stream().filter(byNotImportant).collect(Collectors.toList())
        }
    /**
     *Сортировка по важности, оставляет все объекты
     * API < 24
     **/
         fun sortAllImportantLowerApi24(collection: List<NoteItem>): List<NoteItem> {
            return collection.sortedBy {obj: NoteItem -> obj.mImportant }
         }
    /**
     *Сортировка по тегу, ищет по совпадениям в теге
     **/
        fun sortByTag(collection: List<NoteItem>, tag: String): List<NoteItem> {
            val list: MutableList<NoteItem> = ArrayList()
            if (collection.isEmpty()) return list
            for (i in collection.indices) {
                val length = tag.length
                if (collection[i].mTag!!.length >= length) {
                    if (collection[i].mTag!!.toLowerCase(Locale.getDefault()).contains(tag.toLowerCase(
                            Locale.getDefault()))) {
                        list.add(collection[i])
                    }
                }
            }
            return list
        }

    /**
     *Сортировка по заголовку, ищет по совпадениям в заголовке
     **/
        fun sortByTitle(collection: List<NoteItem>, title: String): List<NoteItem> {
            val list: MutableList<NoteItem> = ArrayList()
            if (collection.isEmpty()) return list
            for (i in collection.indices) {
                val length = title.length
                if (collection[i].mTitle!!.length >= length) if (collection[i].mTitle!!.toLowerCase(
                        Locale.getDefault()).contains(title.toLowerCase(Locale.getDefault()))) {
                    list.add(collection[i])
                }
            }
            return list
        }
}