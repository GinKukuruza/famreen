package com.example.famreen.translateApi.gson

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TranslateLangs {
    @SerializedName("dirs")
    @Expose
    var dirs: List<String>? = null

    @SerializedName("langs")
    @Expose
    var langs: TranslateSupportedLangs? = null

}