package com.example.famreen.application.items

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.firebase.database.Exclude
import java.util.HashMap

@Entity
open class NoteItem : Parcelable{
    @PrimaryKey(autoGenerate = true)
    var mId = 0
    var mTime: String? = null
    var mDescription: String? = null
    var mTitle: String? = null
    @JsonProperty("important")
    var mImportant: Boolean = false
    var mTag: String? = null

    constructor()
    protected constructor(parcel: Parcel) {
        mId = parcel.readInt()
        mTime = parcel.readString()
        mDescription = parcel.readString()
        mTitle = parcel.readString()
        mImportant = parcel.readByte().toInt() != 0
        mTag = parcel.readString()
    }

    @Exclude
    fun toMap(): Map<String, Any?> {
        val result: MutableMap<String, Any?> = HashMap()
        result["id"] = mId
        result["time"] = mTime
        result["title"] = mTitle
        result["description"] = mDescription
        result["tag"] = mTag
        result["important"] = mImportant
        return result
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(mId)
        dest.writeString(mTime)
        dest.writeString(mDescription)
        dest.writeString(mTitle)
        dest.writeByte((if (mImportant) 1 else 0).toByte())
        dest.writeString(mTag)
    }

    companion object CREATOR : Parcelable.Creator<NoteItem> {
        override fun createFromParcel(parcel: Parcel): NoteItem {
            return NoteItem(parcel)
        }

        override fun newArray(size: Int): Array<NoteItem?> {
            return arrayOfNulls(size)
        }
    }
}