package com.example.famreen.translateApi.gson

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TranslateWhatIsLang {
    @SerializedName("code")
    @Expose
    var mCode = 0

    @SerializedName("lang")
    @Expose
    var mLang: String? = null

}