package com.example.famreen.firebase.repositories

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import com.example.famreen.application.interfaces.*
import com.example.famreen.application.items.NoteItem
import com.example.famreen.application.items.TranslateItem
import com.example.famreen.application.logging.Logger
import com.example.famreen.application.room.DBConnection
import com.example.famreen.firebase.FirebaseConnection
import com.example.famreen.firebase.db.User
import com.firebase.client.DataSnapshot
import com.firebase.client.FirebaseError
import com.firebase.client.ValueEventListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import java.util.*

class UserRepositoryImpl : UserRepository {

   /* private fun deleteUser() {
        val firebaseUser = FirebaseConnection.firebaseAuth!!.currentUser
            ?: throw NullPointerException("User is null")
        FirebaseConnection.firebase!!.child("users").child("profile").child(firebaseUser.uid).removeValue()
    }

    fun deleteUser(uid: String) {
        FirebaseConnection.firebase!!.child("users").child("profile").child(uid).removeValue()
    }*/
   @Throws(NullPointerException::class)
    override fun addUser(result: AuthResult, user: User){
        if(result.user == null) throw NullPointerException("User is null")
        val imageUri = Uri.parse(user.mImageUri)
        FirebaseConnection.firebase!!.child("users").child("profile").child(result.user!!.uid).child("info").child("name").setValue(user.mName)
        FirebaseConnection.firebase!!.child("users").child("profile").child(result.user!!.uid).child("info").child("email").setValue(user.mEmail)
        FirebaseConnection.firebase!!.child("users").child("profile").child(result.user!!.uid).child("info").child("sign_in_method").setValue("email")
        val storageReference = FirebaseStorage.getInstance().reference
        storageReference.child("users").child("profile").child(result.user!!.uid).child("photo").putFile(imageUri)
            .addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot ->
                if (taskSnapshot.task.isSuccessful && taskSnapshot.uploadSessionUri != null) {
                    FirebaseConnection.firebase!!.child("users").child("profile").child(result.user!!.uid).child("info").child("image_uri").setValue(taskSnapshot.uploadSessionUri.toString())
                }
            }
            .addOnFailureListener {
                Logger.log(Log.ERROR,"network user excaption",it)
            }
        FirebaseConnection.firebaseAuth!!.signOut()
    }
    @Throws(NullPointerException::class,IllegalArgumentException::class)
    override fun getUser(userRoomRepositoryImpl: UserRoomRepository, listener: ItemListener<Any>){
        val firebaseUser = FirebaseConnection.firebaseAuth!!.currentUser ?: throw NullPointerException("User is null")
        if(!firebaseUser.isEmailVerified) throw IllegalArgumentException("user should be verified for getUser()")
        FirebaseConnection.firebase?.child("users")!!.child("profile").child(firebaseUser.uid)
            .child("info").addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val name = dataSnapshot.child("name").value as String
                        val email = dataSnapshot.child("email").value as String
                        val imageUri = dataSnapshot.child("image_uri").value as String
                        val user = User(name,email,imageUri)
                        user.id = FirebaseConnection.CURRENT_USER
                        userRoomRepositoryImpl.insertUser(user,listener)
                    }
                }
                override fun onCancelled(firebaseError: FirebaseError) {
                    listener.onFailure("Запрос был отменен")
                }
            })
    }

    override fun saveNewUserData(diaryRepositoryImpl: DiaryRepository, translateRepositoryImpl: TranslateRepository): List<Disposable> {
        val disposables = ArrayList<Disposable>()
        val dbConnection = DBConnection.getDbConnection()
        val d1 = object : DisposableSingleObserver<List<NoteItem>?>() {
            override fun onSuccess(items: List<NoteItem>) {
                diaryRepositoryImpl.addAllNotes(items)
            }
            override fun onError(e: Throwable) {
                Logger.log(Log.ERROR, "network user exception", e)
            }
        }
        val d2 = object : DisposableSingleObserver<List<TranslateItem>?>() {
            override fun onSuccess(items: List<TranslateItem>) {
                translateRepositoryImpl.addAllTranslates(items)
            }
            override fun onError(e: Throwable) {
                Logger.log(Log.ERROR, "network user exception", e)
            }
        }
        dbConnection!!.diaryDAO.all
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(d1)
        dbConnection.translateDAO.all
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(d2)
        return disposables
    }

    override fun createUser(name: String, email: String, imageUri: String?): User {
        return User(name,email,imageUri)
    }

    @SuppressLint("CheckResult")
    @Throws(NullPointerException::class,IllegalArgumentException::class)
    override fun addOAuthUser(result: AuthResult) {
        if(result.user == null) throw NullPointerException("User is null")
        if (result.credential == null) throw  IllegalArgumentException("This is not an oauth account, credentials are null")
        if (result.user!!.displayName != null) {
            FirebaseConnection.firebase!!.child("users").child("profile").child(result.user!!.uid).child("info").child("name").setValue(result.user!!.displayName)
        }
        FirebaseConnection.firebase!!.child("users").child("profile").child(result.user!!.uid).child("info").child("sign_in_method").setValue(result.credential!!.signInMethod)
        if (result.user!!.email != null) {
            FirebaseConnection.firebase!!.child("users").child("profile").child(result.user!!.uid).child("info").child("email").setValue(result.user!!.email)
        }
        /*if (result.user!!.photoUrl != null) {
            val uri = Uri.parse(result.user!!.photoUrl.toString())
            FirebaseConnection.firebase!!.child("users").child("profile").child(result.user!!.uid).child("info").child("image_uri").setValue(result.user!!.photoUrl.toString())
            Observable
                .just(uri)
                .observeOn(AndroidSchedulers.mainThread())
                .map { Picasso.get().load(uri).get() }
                .subscribeOn(Schedulers.io())
                .subscribeWith(object : Observer<Bitmap> {
                    override fun onSubscribe(d: Disposable) {
                        disposables.add(d)
                    }
                    override fun onNext(bitmap: Bitmap) {
                        val storageReference = FirebaseStorage.getInstance().reference
                        storageReference.child("users").child("profile").child(result.user!!.uid).child("photo")
                        val stream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                        val image = stream.toByteArray()
                        storageReference.putBytes(image)
                            .addOnSuccessListener {

                            }
                            .addOnFailureListener {

                            }
                    }
                    override fun onError(e: Throwable) {
                        Logger.log(Log.ERROR, "network user exception", e)
                    }
                    override fun onComplete() {}
                })
        }*/
    }

    @Throws(NullPointerException::class)
    override fun getAndSetValues(result: AuthResult, translateRoomRepositoryImpl: TranslateRoomRepository, diaryRoomRepositoryImpl: DiaryRoomRepository) {
        if (result.user == null) throw NullPointerException("User is null")
        FirebaseConnection.firebase!!.child("users").child("profile").child(result.user!!.uid).child("translate").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val list: MutableList<TranslateItem> = ArrayList()
                    for (snapshot in dataSnapshot.children) {
                        val item = snapshot.getValue(TranslateItem::class.java)
                        list.add(item)
                    }
                    translateRoomRepositoryImpl.insertAllTranslates(list)
                }
            }
            override fun onCancelled(firebaseError: FirebaseError) {}
        })
        FirebaseConnection.firebase!!.child("users").child("profile").child(result.user!!.uid).child("diary").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val list: MutableList<NoteItem> = ArrayList()
                    for (snapshot in dataSnapshot.children) {
                        val item = snapshot.getValue(NoteItem::class.java)
                        list.add(item)
                    }
                    diaryRoomRepositoryImpl.insertAllNotes(list)
                }
            }
            override fun onCancelled(firebaseError: FirebaseError) {}
        })
    }

    @Throws(java.lang.NullPointerException::class,java.lang.IllegalArgumentException::class)
    override fun changePassword(email: String, oldPassword: String, newPassword: String, listener: SuccessListener) {
        val firebaseUser = FirebaseConnection.firebaseAuth?.currentUser ?: throw NullPointerException("User is null")
        if (!firebaseUser.isEmailVerified) throw java.lang.IllegalArgumentException("Wrong auth type of the user")
        val credential = EmailAuthProvider.getCredential(email, oldPassword)
        firebaseUser.reauthenticate(credential)
            .addOnSuccessListener {
                firebaseUser.updatePassword(newPassword)
                    .addOnSuccessListener {
                        listener.onSuccess()
                    }
                    .addOnFailureListener {
                        listener.onError(it)
                    }
            }
            .addOnFailureListener {
                listener.onError(it)
            }
    }
    override fun signUp(email: String,password: String, name: String,imageUri: String?,listener: SuccessListener) {
        FirebaseConnection.firebaseAuth!!.fetchSignInMethodsForEmail(email)
            .addOnSuccessListener {
                registration(email, password, name,imageUri,listener)
            }
            .addOnFailureListener {
                listener.onError(it)
                Logger.log(Log.ERROR,"network sign up exception",it)
            }
    }
    private fun registration(email: String,password: String, name: String,imageUri: String?,listener: SuccessListener) {
        FirebaseConnection.firebaseAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val user = createUser(name, email, imageUri)
                addUser(it, user)
                sendVerificationEmail(it!!.user,listener)
            }
            .addOnFailureListener {
                listener.onError(it)
            }
    }
    private fun sendVerificationEmail(user: FirebaseUser?, listener: SuccessListener) {
        if(user == null) throw NullPointerException("User is null for email verification !")
        if (!user.isEmailVerified)
            user.sendEmailVerification()
                .addOnSuccessListener {
                    listener.onSuccess()
                }
                .addOnFailureListener {
                    listener.onError(it)
                }
    }

}