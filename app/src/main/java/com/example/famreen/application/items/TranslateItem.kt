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
    var mFrom_translate: String? = null
    var mTo_translate: String? = null
    var mFrom_lang: String? = null
    var mTo_lang: String? = null

    constructor()
    protected constructor(parcel: Parcel) {
        id = parcel.readInt()
        mFrom_translate = parcel.readString()
        mTo_translate = parcel.readString()
        mFrom_lang = parcel.readString()
        mTo_lang = parcel.readString()
    }

    @Exclude
    fun toMap(): Map<String, Any?> {
        val result: MutableMap<String, Any?> = HashMap()
        result["id"] = id
        result["from_lang"] = mFrom_lang
        result["from_translate"] = mFrom_translate
        result["to_lang"] = mTo_lang
        result["to_translate"] = mTo_translate
        return result
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(id)
        dest.writeString(mFrom_translate)
        dest.writeString(mTo_translate)
        dest.writeString(mFrom_lang)
        dest.writeString(mTo_lang)
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