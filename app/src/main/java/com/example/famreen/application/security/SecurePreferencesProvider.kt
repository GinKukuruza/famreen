@file:Suppress("PrivatePropertyName", "PrivatePropertyName")

package com.example.famreen.application.security

import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.famreen.application.App
/**
 * CLASS IN PROGRESS
 * **/
class SecurePreferencesProvider private constructor() {
    private val STRING_DEFAULT_VALUE = ""
    private val PRIVATE_KEY = "44t78ayt9y84y9t9ptu4tue0tiwep9te90w[-"

    companion object{
        private lateinit var mSharedPreferences: SharedPreferences
        fun getProvider() : SecurePreferencesProvider{
            val sPFile = "f4wae785648w7ws4j786f5i47f59ew7yf49ewf"
            val context = App.getAppContext()
            val mainKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            mSharedPreferences = EncryptedSharedPreferences.create(
                context,
                sPFile,
                mainKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            return SecurePreferencesProvider()
        }
    }

    fun writeRK(key: String){
        with (mSharedPreferences.edit()) {
            this?.putString(PRIVATE_KEY,key)
            apply()
        }
    }
    fun readRK() : String{
        return mSharedPreferences.getString(PRIVATE_KEY,STRING_DEFAULT_VALUE) as String
    }

}