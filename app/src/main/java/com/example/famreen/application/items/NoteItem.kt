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
    var id = 0
    var time: String? = null
    var description: String? = null
    var title: String? = null
    @JsonProperty("important")
    var important: Boolean = false
    var tag: String? = null

    constructor()
    protected constructor(parcel: Parcel) {
        id = parcel.readInt()
        time = parcel.readString()
        description = parcel.readString()
        title = parcel.readString()
        important = parcel.readByte().toInt() != 0
        tag = parcel.readString()
    }

    @Exclude
    fun toMap(): Map<String, Any?> {
        val result: MutableMap<String, Any?> = HashMap()
        result["id"] = id
        result["time"] = time
        result["title"] = title
        result["description"] = description
        result["tag"] = tag
        result["important"] = important
        return result
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(id)
        dest.writeString(time)
        dest.writeString(description)
        dest.writeString(title)
        dest.writeByte((if (important) 1 else 0).toByte())
        dest.writeString(tag)
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