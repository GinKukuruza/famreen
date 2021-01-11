package com.example.famreen.firebase

import com.example.famreen.application.room.DBProvider
import com.example.famreen.application.room.repositories.DiaryRoomRepository
import com.example.famreen.application.room.repositories.TranslateRoomRepository
import com.example.famreen.application.room.repositories.UserRoomRepository
import com.example.famreen.firebase.FirebaseConnection.firebase
import com.example.famreen.firebase.FirebaseConnection.firebaseAuth
import com.example.famreen.firebase.db.EmptyUser
import com.example.famreen.firebase.db.UninitializedUser
import com.example.famreen.network.DiaryRepository
import com.example.famreen.network.TranslateRepository

object FirebaseProvider {
    private val translateRoomRepository = TranslateRoomRepository(TranslateRepository())
    private val diaryRoomRepository = DiaryRoomRepository(DiaryRepository())
    private val userRoomRepository = UserRoomRepository()
    val tag = "FirebaseProvider"

    private fun deleteUser() {
        val user = firebaseAuth!!.currentUser
        if (user != null) {
            firebase!!.child("users").child("profile").child(user.uid).removeValue()
        } else {
            throw NullPointerException("User is null")
        }
    }

    fun deleteUser(uid: String) {
        firebase!!.child("users").child("profile").child(uid).removeValue()
        DBProvider.deleteAll(translateRoomRepository,diaryRoomRepository,userRoomRepository)
    }

    fun exit(): Boolean{
        val currentUser = firebaseAuth!!.currentUser ?: return false
        if (currentUser.isEmailVerified) {
            userRoomRepository.deleteUserById(FirebaseConnection.CURRENT_USER)
        }
        translateRoomRepository.deleteAllTranslates()
        diaryRoomRepository.deleteAllNotes()
        firebaseAuth!!.signOut()
        return true
    }

    fun userIsLogIn(): Boolean {
        return firebaseAuth!!.currentUser != null
    }
    fun getCurrentUser(): Any?{
        val firebaseUser = firebaseAuth?.currentUser
        return if(firebaseUser != null){
            if(firebaseUser.isEmailVerified){
                return if(FirebaseConnection.user == null) UninitializedUser() else
                    FirebaseConnection.user
            }else firebaseUser
        } else EmptyUser()
    }
}