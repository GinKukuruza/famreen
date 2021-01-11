package com.example.famreen.application.items

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.Exclude
import java.util.HashMap

@Entity
open class TranslateItem : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id = 0
    var from_translate: String? = null
    var to_translate: String? = null
    var from_lang: String? = null
    var to_lang: String? = null

    constructor()
    protected constructor(`in`: Parcel) {
        id = `in`.readInt()
        from_translate = `in`.readString()
        to_translate = `in`.readString()
        from_lang = `in`.readString()
        to_lang = `in`.readString()
    }

    @Exclude
    fun toMap(): Map<String, Any?> {
        val result: MutableMap<String, Any?> = HashMap()
        result["id"] = id
        result["from_lang"] = from_lang
        result["from_translate"] = from_translate
        result["to_lang"] = to_lang
        result["to_translate"] = to_translate
        return result
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(id)
        dest.writeString(from_translate)
        dest.writeString(to_translate)
        dest.writeString(from_lang)
        dest.writeString(to_lang)
    }

    companion object CREATOR : Parcelable.Creator<TranslateItem> {
        override fun createFromParcel(parcel: Parcel): TranslateItem {
            return TranslateItem(parcel)
        }

        override fun newArray(size: Int): Array<TranslateItem?> {
            return arrayOfNulls(size)
        }
    }
}