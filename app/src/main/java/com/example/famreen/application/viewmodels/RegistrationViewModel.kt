package com.example.famreen.application.viewmodels

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.example.famreen.states.States
import com.example.famreen.application.exceptions.RegistrationException
import com.example.famreen.application.logging.Logger
import com.example.famreen.firebase.FirebaseConnection
import com.example.famreen.firebase.FirebaseProvider
import com.example.famreen.firebase.db.User
import com.example.famreen.firebase.repositories.UserRepository
import com.example.famreen.utils.extensions.default
import com.example.famreen.utils.extensions.set
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import java.lang.NullPointerException

class RegistrationViewModel(private val userRepository: UserRepository) {
    val state = MutableLiveData<States>().default(initialValue = States.DefaultState())

    fun signUp(email: String?,password: String?, name: String?,imageUri: String?) {
        if (!FirebaseProvider.exit()) return
        if(!checkFields(email,password, name)) return
        state.set(States.LoadingState())
        FirebaseConnection.firebaseAuth?.fetchSignInMethodsForEmail(email as String)
            ?.addOnSuccessListener {
                registration(email, password as String, name as String,imageUri)
            }
            ?.addOnFailureListener {
                state.set(States.ErrorState("network sign up exception"))
                Logger.log(9,"network sign up exception",it)
            }
    }
    private fun registration(email: String,password: String, name: String,imageUri: String?) {
        FirebaseConnection.firebaseAuth?.createUserWithEmailAndPassword(email, password)
            ?.addOnSuccessListener {
                state.set(States.DefaultState())
                sendVerificationEmail(it!!.user)
                val user = userRepository.createUser(name, email, imageUri)
                initData(it, user)
            }
            ?.addOnFailureListener {
                state.set(States.DefaultState())
                catchException(it)
            }
    }

    private fun initData(result: AuthResult, user: User) {
        userRepository.addUser(result, user)
        state.set(States.UserState(user))
    }
    private fun sendVerificationEmail(user: FirebaseUser?) {
        if(user == null) throw NullPointerException("User is null for email verification !")
        if (!user.isEmailVerified)
            user.sendEmailVerification()
                .addOnSuccessListener {
                    state.set(States.ErrorState("На почту было отправлено письмо"))
                }
    }

    private fun checkFields(email: String?, password: String?,name: String?): Boolean {
        return if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(name)) {
            state.set(States.ErrorState("Пустые поля"))
            //TODO добавить проверок
            false
        } else true
    }
    private fun catchException(ex: Exception?) {
        Logger.log(2,"comparator parse data exception",ex)
        val msg = RegistrationException(ex).message
        state.set(States.ErrorState(msg))
    }
}