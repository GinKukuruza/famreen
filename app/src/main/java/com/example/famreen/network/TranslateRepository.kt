package com.example.famreen.network

import com.example.famreen.application.items.TranslateItem
import com.example.famreen.firebase.FirebaseConnection
import java.util.HashMap

class TranslateRepository {

    fun addTranslate(item: TranslateItem) {
        val firebaseUser = FirebaseConnection.firebaseAuth!!.currentUser
            ?: throw NullPointerException("User is null")
        val values = item.toMap()
        val updates: MutableMap<String, Any> = HashMap()
        updates["/users/profile/" + firebaseUser.uid + "/translate/" + item.id] = values
        FirebaseConnection.firebase!!.updateChildren(updates)

    }

    fun addAllTranslates(items: List<TranslateItem?>) {
        val firebaseUser = FirebaseConnection.firebaseAuth!!.currentUser
            ?: throw NullPointerException("User is null")
        val updates: MutableMap<String, Any> = HashMap()
        for (i in items.indices) {
            updates["/users/profile/" + firebaseUser.uid + "/translate/" + items[i]!!.id] = items[i]!!.toMap()
        }
        FirebaseConnection.firebase!!.updateChildren(updates)
    }

    fun deleteTranslate(id: Int) {
        val firebaseUser = FirebaseConnection.firebaseAuth!!.currentUser
            ?: throw NullPointerException("User is null")
        val stringId = id.toString()
        FirebaseConnection.firebase!!.child("users")
            .child("profile")
            .child(firebaseUser.uid)
            .child("translate")
            .child(stringId)
            .removeValue()
    }

    fun deleteAllTranslates() {
        val firebaseUser = FirebaseConnection.firebaseAuth!!.currentUser
            ?: throw NullPointerException("User is null")
        FirebaseConnection.firebase!!.child("users")
            .child("profile")
            .child(firebaseUser.uid)
            .child("translate")
            .removeValue()
    }
}