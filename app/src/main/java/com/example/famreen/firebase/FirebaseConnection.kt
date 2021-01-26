package com.example.famreen.firebase

import com.example.famreen.firebase.db.User
import com.firebase.client.Firebase
import com.google.firebase.auth.FirebaseAuth

object FirebaseConnection {
    const val CURRENT_USER = Int.MAX_VALUE
    private var mAuth: FirebaseAuth? = null
    private var mFirebase: Firebase? = null
    private var mUser: User? = null
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
    fun getUser() = mUser
    fun setUser(user: User){mUser = user}
}