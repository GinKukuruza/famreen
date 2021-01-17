package com.example.famreen.application.viewmodels

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.example.famreen.states.States
import com.example.famreen.application.exceptions.LoginException
import com.example.famreen.application.logging.Logger
import com.example.famreen.application.room.observers.ItemObserver
import com.example.famreen.application.room.repositories.DiaryRoomRepository
import com.example.famreen.application.room.repositories.TranslateRoomRepository
import com.example.famreen.application.room.repositories.UserRoomRepository
import com.example.famreen.firebase.FirebaseConnection
import com.example.famreen.firebase.FirebaseProvider
import com.example.famreen.firebase.db.EmptyUser
import com.example.famreen.firebase.db.UninitializedUser
import com.example.famreen.firebase.db.User
import com.example.famreen.network.DiaryRepository
import com.example.famreen.network.TranslateRepository
import com.example.famreen.network.UserRepository
import com.example.famreen.utils.default
import com.example.famreen.utils.set
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.*
import java.util.*

class LoginViewModel(private val userRepository: UserRepository, private val userRoomRepository: UserRoomRepository, private val translateRoomRepository: TranslateRoomRepository, val diaryRoomRepository: DiaryRoomRepository) {
    private var isDelete = false
    val state = MutableLiveData<States>().default(initialValue = States.DefaultState())

    fun customLogin(email: String, password: String) {
        if (!checkFields(email, password)) return
        if (FirebaseConnection.firebaseAuth?.currentUser != null) FirebaseProvider.exit()
        signInWithEmail(email, password)
    }

    private fun checkFields(email: String, password: String): Boolean {
        return if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            state.set(States.ErrorState("Пустые поля"))
            //TODO добавить проверок
            false
        } else true
    }

    private fun signInWithEmail(email: String, password: String) {
        state.set(States.LoadingState())
        FirebaseConnection.firebaseAuth?.signInWithEmailAndPassword(email, password)
            ?.addOnSuccessListener {
                state.set(States.DefaultState())
                if (FirebaseConnection.firebaseAuth!!.currentUser!!.isEmailVerified) {
                   successAuth(it)
                } else {
                    FirebaseConnection.firebaseAuth?.signOut()
                    state.set(States.ErrorState("Проверьте свою почту для верификации"))
                }
            }
            ?.addOnFailureListener {
                state.set(States.DefaultState())
                catchException(it)
            }
    }

    fun authWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        Objects.requireNonNull(FirebaseConnection.firebaseAuth?.signInWithCredential(credential)
            ?.addOnSuccessListener {
                successAuth(it)
            }
            ?.addOnFailureListener {
                catchException(it)
            })
    }

    fun catchException(e: Exception?) {
        val ex = LoginException(e)
        Logger.log(2,"network login exception",e)
        state.set(States.ErrorState(ex.message))
    }
    fun successAuth(authResult: AuthResult?){
        state.set(States.DefaultState())
        if (authResult != null) {
            if (authResult.user != null) {
                if (isDelete) {
                    reAuth(authResult,null,null)
                }
                initData(authResult)
            }
        }
    }

    private fun reAuth(result: AuthResult, email: String?, password: String?) {
        val user = result.user ?: throw NullPointerException("User is null for reauth")
        if (user.isEmailVerified) {
            val credential = EmailAuthProvider.getCredential(email!!, password!!)
            user.reauthenticate(credential)
                .addOnCompleteListener { isDelete = false }
                .addOnFailureListener {catchException(it)}
        }
        if (result.credential != null) {
            val credential = result.credential
            user.reauthenticate(credential!!)
                .addOnCompleteListener { isDelete = false }
                .addOnFailureListener {catchException(it)}
        }
    }
    @Throws(java.lang.NullPointerException::class)
    private fun initData(result: AuthResult) {
        if (result.user == null) throw NullPointerException("User is null")
        if (result.additionalUserInfo!!.isNewUser) {
                userRepository.saveNewUserData(DiaryRepository(), TranslateRepository())
        } else {
                userRepository.getAndSetValues(result,translateRoomRepository,diaryRoomRepository)
        }
        if (result.credential != null) {
            userRepository.addOAuthUser(result)
            state.set(States.UserState(result.user))
        }
        if (result.user!!.isEmailVerified) {
            prepareUser()
        }
    }
    @Throws(java.lang.NullPointerException::class)
    fun deleteAccount(){
        val user = FirebaseConnection.firebaseAuth?.currentUser ?: throw java.lang.NullPointerException("User is null")
        val uid = user.uid
        user.delete()
            .addOnSuccessListener {
                FirebaseProvider.deleteUser(uid)
                state.set(States.UserState(EmptyUser()))
            }
            .addOnFailureListener {
                errorDeleteAccount(it)
            }
    }

    private fun errorDeleteAccount(e: Exception) {
        when (e) {
            is FirebaseAuthRecentLoginRequiredException -> {
                state.set(States.ErrorState("Пожалуйста, войдите заново для подтверждения аккаунта"))
                isDelete = true
                FirebaseProvider.exit()
                state.set(States.UserState(EmptyUser()))

            }
            else -> {
                catchException(e)
            }
        }
    }
    fun getUser(){
        userRoomRepository.getUser(object : ItemObserver<User> {
            override fun getItem(item: User) {
                FirebaseConnection.user = item
                state.set(States.UserState(item))
            }
        })
    }
    private fun prepareUser(){
        userRepository.getUser(userRoomRepository,object : ItemObserver<Any>{
            override fun getItem(item: Any) {
                when(item){
                    is java.lang.Exception ->{
                        catchException(item)
                    }
                    is Boolean ->{
                        if(item){
                            state.set(States.UserState(UninitializedUser()))
                        }
                    }
                }
            }

        })
    }
}
