package com.example.famreen.network

import com.example.famreen.application.items.NoteItem
import com.example.famreen.firebase.FirebaseConnection
import java.util.HashMap

class DiaryRepository {
    @Throws(NullPointerException::class)
    fun deleteNotes(list: List<Int>?) {
        if(list == null) throw java.lang.NullPointerException("List is null")
        val firebaseUser = FirebaseConnection.firebaseAuth!!.currentUser
            ?: throw NullPointerException("User is null")
        var id: String
        for (item in list) {
            id = item.toString()
            FirebaseConnection.firebase!!.child("users")
                .child("profile")
                .child(firebaseUser.uid)
                .child("diary")
                .child(id)
                .removeValue()
        }
    }

    fun deleteAllNotes() {
        val firebaseUser = FirebaseConnection.firebaseAuth!!.currentUser
            ?: throw NullPointerException("User is null")
        FirebaseConnection.firebase!!
            .child("users")
            .child("profile")
            .child(firebaseUser.uid)
            .child("diary")
            .removeValue()
    }
    @Throws(java.lang.NullPointerException::class)
    fun addNote(item: NoteItem?) {
        if(item == null) throw NullPointerException("Item is null")
        val firebaseUser = FirebaseConnection.firebaseAuth!!.currentUser ?: throw NullPointerException("User is null")
        val values = item.toMap()
        val updates: MutableMap<String, Any> = HashMap()
        updates["/users/profile/" + firebaseUser.uid + "/diary/" + item.id] = values
        FirebaseConnection.firebase!!.updateChildren(updates)
    }
    @Throws(NullPointerException::class)
    fun addAllNotes(list: List<NoteItem>?) {
        if(list == null) throw java.lang.NullPointerException("List is null")
        val firebaseUser = FirebaseConnection.firebaseAuth!!.currentUser
            ?: throw NullPointerException("User is null")
        val updates: MutableMap<String, Any> = HashMap()
        for (i in list.indices) {
            updates["/users/profile/" + firebaseUser.uid + "/diary/" + list[i].id] = list[i].toMap()
        }
        FirebaseConnection.firebase!!.updateChildren(updates)

    }
    @Throws(NullPointerException::class)
    fun deleteNote(id: Int?) {
        if(id == null) throw NullPointerException("id is null")
        val firebaseUser = FirebaseConnection.firebaseAuth!!.currentUser
            ?: throw NullPointerException("User is null")
        val stringId = id.toString()
        FirebaseConnection.firebase!!.child("users")
            .child("profile")
            .child(firebaseUser.uid)
            .child("diary")
            .child(stringId)
            .removeValue()
    }
}