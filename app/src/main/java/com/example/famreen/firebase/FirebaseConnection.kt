package com.example.famreen.firebase

import com.example.famreen.application.room.DBProvider
import com.example.famreen.application.room.observers.ItemObserver
import com.example.famreen.application.room.repositories.UserRoomRepository
import com.example.famreen.firebase.db.User
import com.example.famreen.network.UserRepository
import com.firebase.client.Firebase
import com.google.firebase.auth.FirebaseAuth

object FirebaseConnection {
    const val CURRENT_USER = Int.MAX_VALUE
    private var mAuth: FirebaseAuth? = null
    private var mFirebase: Firebase? = null
    var user: User? = null
    @JvmStatic
    val firebaseAuth: FirebaseAuth?
        get() {
            if (mAuth == null) {
                mAuth = FirebaseAuth.getInstance()
            }
            return mAuth
        }

    fun signOut() {
        if (mAuth == null) return
        mAuth!!.signOut()
    }

    @JvmStatic
    val firebase: Firebase?
        get() {
            mFirebase = Firebase("https://fasthelperapp-f0d86.firebaseio.com/")
            return mFirebase
        }
}