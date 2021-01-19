package com.example.famreen.translateApi.gson

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TranslateWhatIsLang {
    @SerializedName("code")
    @Expose
    var code = 0

    @SerializedName("lang")
    @Expose
    var lang: String? = null

}