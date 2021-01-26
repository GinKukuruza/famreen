package com.example.famreen.firebase

import com.example.famreen.application.interfaces.DiaryRoomRepository
import com.example.famreen.application.interfaces.TranslateRoomRepository
import com.example.famreen.application.interfaces.UserRoomRepository
import com.example.famreen.application.room.DBProviderImpl
import com.example.famreen.application.room.repositories.DiaryRoomRepositoryImpl
import com.example.famreen.application.room.repositories.TranslateRoomRepositoryImpl
import com.example.famreen.application.room.repositories.UserRoomRepositoryImpl
import com.example.famreen.firebase.FirebaseConnection.firebase
import com.example.famreen.firebase.FirebaseConnection.firebaseAuth
import com.example.famreen.firebase.db.EmptyUser
import com.example.famreen.firebase.db.UninitializedUser
import com.example.famreen.firebase.repositories.DiaryRepositoryImpl
import com.example.famreen.firebase.repositories.TranslateRepositoryImpl

object FirebaseProvider {
    val mTag = FirebaseProvider::class.java.name
    private val mTranslateRoomRepository: TranslateRoomRepository = TranslateRoomRepositoryImpl(TranslateRepositoryImpl())
    private val mDiaryRoomRepository: DiaryRoomRepository = DiaryRoomRepositoryImpl(DiaryRepositoryImpl())
    private val mUserRoomRepository: UserRoomRepository = UserRoomRepositoryImpl()

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
        DBProviderImpl.deleteAll(mTranslateRoomRepository,mDiaryRoomRepository,mUserRoomRepository)
    }

    fun exit(): Boolean{
        val currentUser = firebaseAuth!!.currentUser ?: return false
        if (currentUser.isEmailVerified) {
            mUserRoomRepository.deleteUserById(FirebaseConnection.CURRENT_USER)
        }
        mTranslateRoomRepository.deleteAllTranslates()
        mDiaryRoomRepository.deleteAllNotes()
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
                return if(FirebaseConnection.getUser() == null) UninitializedUser() else
                    FirebaseConnection.getUser()
            }else firebaseUser
        } else EmptyUser()
    }
}