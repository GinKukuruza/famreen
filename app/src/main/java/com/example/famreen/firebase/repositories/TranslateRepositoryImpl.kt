package com.example.famreen.firebase.repositories

import com.example.famreen.application.interfaces.TranslateRepository
import com.example.famreen.application.items.TranslateItem
import com.example.famreen.firebase.FirebaseConnection
import java.util.HashMap

class TranslateRepositoryImpl : TranslateRepository{

    @Throws(java.lang.NullPointerException::class)
    override fun addTranslate(item: TranslateItem?) {
        if(item == null) throw NullPointerException("item is null")
        val firebaseUser = FirebaseConnection.firebaseAuth!!.currentUser
            ?: throw NullPointerException("User is null")
        val values = item.toMap()
        val updates: MutableMap<String, Any> = HashMap()
        updates["/users/profile/" + firebaseUser.uid + "/translate/" + item.id] = values
        FirebaseConnection.firebase!!.updateChildren(updates)

    }
    @Throws(java.lang.NullPointerException::class)
    override fun addAllTranslates(items: List<TranslateItem>?) {
        if(items == null) throw NullPointerException("id is null")
        val firebaseUser = FirebaseConnection.firebaseAuth!!.currentUser
            ?: throw NullPointerException("User is null")
        val updates: MutableMap<String, Any> = HashMap()
        for (i in items.indices) {
            updates["/users/profile/" + firebaseUser.uid + "/translate/" + items[i].id] = items[i].toMap()
        }
        FirebaseConnection.firebase!!.updateChildren(updates)
    }
    @Throws(java.lang.NullPointerException::class)
    override fun deleteTranslate(id: Int?) {
        if(id == null) throw NullPointerException("id is null")
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
    @Throws(java.lang.NullPointerException::class)
    override fun deleteAllTranslates() {
        val firebaseUser = FirebaseConnection.firebaseAuth!!.currentUser
            ?: throw NullPointerException("User is null")
        FirebaseConnection.firebase!!.child("users")
            .child("profile")
            .child(firebaseUser.uid)
            .child("translate")
            .removeValue()
    }
}