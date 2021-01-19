package com.example.famreen.translateApi.gson

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TranslateResp {
    @SerializedName("code")
    @Expose
    var code = 0

    @SerializedName("lang")
    @Expose
    var lang: String? = null

    @SerializedName("text")
    @Expose
    var text: List<String>? = null

}