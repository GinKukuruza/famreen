package com.example.famreen.application.viewmodels

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.famreen.application.App
import com.example.famreen.application.exceptions.LoginException
import com.example.famreen.application.interfaces.*
import com.example.famreen.application.logging.Logger
import com.example.famreen.firebase.FirebaseConnection
import com.example.famreen.firebase.FirebaseProvider
import com.example.famreen.firebase.db.EmptyUser
import com.example.famreen.firebase.db.UninitializedUser
import com.example.famreen.firebase.db.User
import com.example.famreen.firebase.repositories.DiaryRepositoryImpl
import com.example.famreen.firebase.repositories.TranslateRepositoryImpl
import com.example.famreen.states.States
import com.example.famreen.utils.extensions.default
import com.example.famreen.utils.extensions.set
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.GoogleAuthProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.*

class LoginViewModel(private val mUserRepositoryImpl: UserRepository,
                     private val mUserRoomRepositoryImpl: UserRoomRepository,
                     private val mTranslateRoomRepositoryImpl: TranslateRoomRepository,
                     private val mDiaryRoomRepositoryImpl: DiaryRoomRepository) {
    private val mState = MutableLiveData<States>().default(initialValue = States.DefaultState())
    private var mIsDelete = false
    private val mDisposables = CompositeDisposable()

    init {
        App.appComponent.inject(this@LoginViewModel)
    }
    private fun checkFields(email: String, password: String): Boolean {
        return if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            mState.set(States.ErrorState("Пустые поля"))
            false
        } else true
    }

    private fun signInWithEmail(email: String, password: String) {
        mState.set(States.LoadingState())
        FirebaseConnection.firebaseAuth?.signInWithEmailAndPassword(email, password)
            ?.addOnSuccessListener {
                mState.set(States.DefaultState())
                if (FirebaseConnection.firebaseAuth!!.currentUser!!.isEmailVerified) {
                   successAuth(it)
                } else {
                    FirebaseConnection.firebaseAuth?.signOut()
                    mState.set(States.ErrorState("Проверьте свою почту для верификации"))
                }
            }
            ?.addOnFailureListener {
                mState.set(States.DefaultState())
                catchException(it)
            }
    }

    private fun reAuth(result: AuthResult, email: String?, password: String?) {
        val user = result.user ?: throw NullPointerException("User is null for reauth")
        if (user.isEmailVerified) {
            val credential = EmailAuthProvider.getCredential(email!!, password!!)
            user.reauthenticate(credential)
                .addOnCompleteListener { mIsDelete = false }
                .addOnFailureListener {catchException(it)}
        }
        if (result.credential != null) {
            val credential = result.credential
            user.reauthenticate(credential!!)
                .addOnCompleteListener { mIsDelete = false }
                .addOnFailureListener {catchException(it)}
        }
    }
    @Throws(java.lang.NullPointerException::class)
    private fun initData(result: AuthResult) {
        if (result.user == null) throw NullPointerException("User is null")
        if (result.additionalUserInfo!!.isNewUser) {
            mUserRepositoryImpl.saveNewUserData(DiaryRepositoryImpl(), TranslateRepositoryImpl())
        } else {
            mUserRepositoryImpl.getAndSetValues(result,mTranslateRoomRepositoryImpl,mDiaryRoomRepositoryImpl)
        }
        if (result.credential != null) {
            mUserRepositoryImpl.addOAuthUser(result)
            mState.set(States.UserState(result.user))
        }
        if (result.user!!.isEmailVerified) {
            prepareUser()
        }
    }

    private fun errorDeleteAccount(e: Exception) {
        when (e) {
            is FirebaseAuthRecentLoginRequiredException -> {
                mState.set(States.ErrorState("Пожалуйста, войдите заново для подтверждения аккаунта"))
                mIsDelete = true
                FirebaseProvider.exit()
                mState.set(States.UserState(EmptyUser()))

            }
            else -> {
                catchException(e)
            }
        }
    }
    private fun prepareUser(){
        mUserRepositoryImpl.getUser(mUserRoomRepositoryImpl,object : ItemListener<Any> {
            override fun getItem(item: Any) {
                when(item){
                    is java.lang.Exception ->{
                        catchException(item)
                    }
                    is Boolean ->{
                        if(item){
                            mState.set(States.UserState(UninitializedUser()))
                        }
                    }
                }
            }

            override fun onFailure(msg: String) {
                mState.set(States.ErrorState(msg))
            }

        })
    }
    /**
     * Вызывается для получения текущего пользователя и его данных, отсылает его в mState
     * **/
    fun getUser(){
        val d = mUserRoomRepositoryImpl.getUser(object : ItemListener<User> {
            override fun getItem(item: User) {
                FirebaseConnection.setUser(item)
                mState.set(States.UserState(item))
            }

            override fun onFailure(msg: String) {
                mState.set(States.ErrorState(msg))
            }
        })
        d?.let {
            addDisposable(it)
        }
    }
    /**
     * Вызывается для удаления пользователя
     * **/
    @Throws(java.lang.NullPointerException::class)
    fun deleteAccount(){
        mState.set(States.LoadingState())
        val user = FirebaseConnection.firebaseAuth?.currentUser ?: throw java.lang.NullPointerException("User is null")
        val uid = user.uid
        user.delete()
            .addOnSuccessListener {
                FirebaseProvider.deleteUser(uid)
                mState.set(States.UserState(EmptyUser()))
            }
            .addOnFailureListener {
                errorDeleteAccount(it)
            }
    }
    /**
     * Вход через Google
     * **/
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
    /**
     * Ловит основные исключения связанные со входом
     * **/
    fun catchException(e: Exception?) {
        val ex = LoginException(e)
        Logger.log(Log.ERROR,"network login exception",e)
        mState.set(States.ErrorState(ex.mMessage))
    }
    /**
     * Вызывается для передачи результата аутентификации при удачном выполнении входа
     * **/
    fun successAuth(authResult: AuthResult?){
        mState.set(States.DefaultState())
        if (authResult != null) {
            if (authResult.user != null) {
                if (mIsDelete) {
                    reAuth(authResult,null,null)
                }
                initData(authResult)
            }
        }
    }
    /**
     * Вход с помощью email и password
     * **/
    fun customLogin(email: String, password: String) {
        if (!checkFields(email, password)) return
        if (FirebaseConnection.firebaseAuth?.currentUser != null) FirebaseProvider.exit()
        signInWithEmail(email, password)
    }
    /**
     * **/
    fun getState() = mState
    /**
     * Окончательно высвобождает ресурсы при полном завершении работы фрагмента, вызывается в onDestroy()
     * **/
    fun release(){
        mDisposables.dispose()
    }
    /**
     * Очищает временные ресурсы, вызывается в onDestroyView()
     * **/
    fun clear(){
        mDisposables.clear()
    }
    /**
     * **/
    fun addDisposable(disposable: Disposable){
        mDisposables.add(disposable)
    }
}
