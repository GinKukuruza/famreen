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
    var mFromTranslate: String? = null
    var mToTranslate: String? = null
    var mFromLang: String? = null
    var mToLang: String? = null

    constructor()
    protected constructor(parcel: Parcel) {
        id = parcel.readInt()
        mFromTranslate = parcel.readString()
        mToTranslate = parcel.readString()
        mFromLang = parcel.readString()
        mToLang = parcel.readString()
    }

    @Exclude
    fun toMap(): Map<String, Any?> {
        val result: MutableMap<String, Any?> = HashMap()
        result["id"] = id
        result["from_lang"] = mFromLang
        result["from_translate"] = mFromTranslate
        result["to_lang"] = mToLang
        result["to_translate"] = mToTranslate
        return result
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(id)
        dest.writeString(mFromTranslate)
        dest.writeString(mToTranslate)
        dest.writeString(mFromLang)
        dest.writeString(mToLang)
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