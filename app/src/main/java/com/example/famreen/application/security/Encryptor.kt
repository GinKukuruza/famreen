package com.example.famreen.application.security

import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyPairGenerator
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class Encryptor {
    private val TRANSFORMATION = ""
    private val KEY_STORE = ""

    private fun getSecretKey(alias: String) : SecretKey{
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEY_STORE)
        val parameterSpec: KeyGenParameterSpec = KeyGenParameterSpec.Builder(
            alias, KeyProperties.PURPOSE_DECRYPT or KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_SIGN
        ).run {
            setDigests(KeyProperties.DIGEST_SHA256)
            build()
        }
        keyGenerator.init(parameterSpec)

       return keyGenerator.generateKey()
    }
    fun encryptText(){

    }
}