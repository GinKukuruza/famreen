package com.example.famreen.translateApi.gson

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TranslateResp {
    @SerializedName("code")
    @Expose
    var mCode = 0

    @SerializedName("lang")
    @Expose
    var mLang: String? = null

    @SerializedName("text")
    @Expose
    var mText: List<String>? = null

}