package com.example.famreen.application.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.google.firebase.installations.FirebaseInstallations
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import kotlin.random.Random
/**
 * CLASS IN PROGRESS
 * **/
class Decryptor {
    private val mSecKey = getSecretKey("ilrgd48oguseu98gulsis4u8gtslu")
    private fun getSecretKey(alias: String) : PublicKey {
        val keyPairGenerator = KeyPairGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore"
        )
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            alias,
            KeyProperties.PURPOSE_DECRYPT
        )
            .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
            .build()
        keyPairGenerator.initialize(keyGenParameterSpec)
        val keyPair = keyPairGenerator.generateKeyPair()
        val cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding")
        cipher.init(Cipher.DECRYPT_MODE, keyPair.private)

        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        val privateKey = keyStore.getKey(alias, null) as PrivateKey
        val publicKey = keyStore.getCertificate(alias).publicKey
        //encrypt
        val privateKeyInString = encryptPK(privateKey, publicKey)
        //write in sp
        val provider = SecurePreferencesProvider.getProvider()
        provider.writeRK(privateKeyInString)
        return publicKey
    }
    @Throws(NullPointerException::class)
    private fun encryptPK(privateKey: PrivateKey, publicKey: PublicKey) : String{
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val nonce = ByteArray(256)
        val random = Random.nextBytes(nonce)
        val spec = GCMParameterSpec(32 * 8, random)
        cipher.init(Cipher.ENCRYPT_MODE,publicKey,spec)
        val id = FirebaseInstallations.getInstance().id.result
            ?: throw NullPointerException("user id is null")
        val bytes = id.toByteArray()
        cipher.update(bytes)
        cipher.init(Cipher.ENCRYPT_MODE,publicKey)
        val encodedPrivateKey = cipher.doFinal(privateKey.encoded)
        return String(encodedPrivateKey)
    }
    fun decryptText(text: String) : String{
        val provider = SecurePreferencesProvider.getProvider()
        val encodedPK = provider.readRK()
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val nonce = ByteArray(256)
        val random = Random.nextBytes(nonce)
        val spec = GCMParameterSpec(32 * 8, random)
        cipher.init(Cipher.DECRYPT_MODE,mSecKey,spec)
        val bytes = encodedPK.toByteArray()
        cipher.update(bytes)
        cipher.init(Cipher.DECRYPT_MODE,mSecKey)
        val encodedString = cipher.doFinal(text.toByteArray())
        return String(encodedString)
    }
}