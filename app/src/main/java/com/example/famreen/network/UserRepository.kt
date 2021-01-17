package com.example.famreen.network

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.example.famreen.application.items.NoteItem
import com.example.famreen.application.items.TranslateItem
import com.example.famreen.application.logging.Logger
import com.example.famreen.application.room.DBConnection
import com.example.famreen.application.room.observers.ItemObserver
import com.example.famreen.application.room.repositories.DiaryRoomRepository
import com.example.famreen.application.room.repositories.TranslateRoomRepository
import com.example.famreen.application.room.repositories.UserRoomRepository
import com.example.famreen.firebase.FirebaseConnection
import com.example.famreen.firebase.FirebaseProvider
import com.example.famreen.firebase.db.User
import com.firebase.client.DataSnapshot
import com.firebase.client.FirebaseError
import com.firebase.client.ValueEventListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import java.io.ByteArrayOutputStream
import java.lang.IllegalArgumentException
import java.util.ArrayList

class UserRepository {

   /* private fun deleteUser() {
        val firebaseUser = FirebaseConnection.firebaseAuth!!.currentUser
            ?: throw NullPointerException("User is null")
        FirebaseConnection.firebase!!.child("users").child("profile").child(firebaseUser.uid).removeValue()
    }

    fun deleteUser(uid: String) {
        FirebaseConnection.firebase!!.child("users").child("profile").child(uid).removeValue()
    }*/

    fun addUser(result: AuthResult, user: User){
        if(result.user == null) throw NullPointerException("User is null")
        val imageUri = Uri.parse(user.image_uri)
        FirebaseConnection.firebase!!.child("users").child("profile").child(result.user!!.uid).child("info").child("name").setValue(user.name)
        FirebaseConnection.firebase!!.child("users").child("profile").child(result.user!!.uid).child("info").child("email").setValue(user.email)
        FirebaseConnection.firebase!!.child("users").child("profile").child(result.user!!.uid).child("info").child("sign_in_method").setValue("email")
        val storageReference = FirebaseStorage.getInstance().reference
        storageReference.child("users").child("profile").child(result.user!!.uid).child("photo").putFile(imageUri)
            .addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot ->
                if (taskSnapshot.task.isSuccessful && taskSnapshot.uploadSessionUri != null) {
                    FirebaseConnection.firebase!!.child("users").child("profile").child(result.user!!.uid).child("info").child("image_uri").setValue(taskSnapshot.uploadSessionUri.toString())
                }
            }
            .addOnFailureListener {
                //TODO
            }
        FirebaseConnection.firebaseAuth!!.signOut()
    }

    fun getUser(userRoomRepository: UserRoomRepository,observer: ItemObserver<Any>){
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
                        userRoomRepository.insertUser(user,observer)
                    }
                }
                override fun onCancelled(firebaseError: FirebaseError) {
                    //TODO CANCELED
                }
            })
    }

    fun saveNewUserData(diaryRepository: DiaryRepository,translateRepository: TranslateRepository) {
        val disposablesNotes = CompositeDisposable()
        val dbConnection = DBConnection.getDbConnection()
        dbConnection!!.diaryDAO.all
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : DisposableSingleObserver<List<NoteItem>?>() {
                override fun onSuccess(items: List<NoteItem>) {
                    diaryRepository.addAllNotes(items)
                    disposablesNotes.clear()
                    disposablesNotes.dispose()
                }

                override fun onError(e: Throwable) {
                    Logger.log(9, "network user exception", e)
                    disposablesNotes.clear()
                    disposablesNotes.dispose()
                }
            })
        val disposablesTranslates = CompositeDisposable()
        dbConnection.translateDAO.all
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : DisposableSingleObserver<List<TranslateItem?>?>() {
                override fun onSuccess(items: List<TranslateItem?>) {
                    translateRepository.addAllTranslates(items)
                    disposablesTranslates.clear()
                    disposablesTranslates.dispose()
                }

                override fun onError(e: Throwable) {
                    Logger.log(9, "network user exception", e)
                    disposablesTranslates.clear()
                    disposablesTranslates.dispose()
                }
            })
    }

    fun createUser(name: String, email: String, imageUri: String?): User {
        return User(name,email,imageUri)
    }

    @SuppressLint("CheckResult")
    @Throws(Exception::class)
    fun addOAuthUser(result: AuthResult) {
        if(result.user == null) throw NullPointerException("User is null")
        if (result.credential == null) throw  IllegalArgumentException("This is not an oauth account, credentials are null")
        val disposables = CompositeDisposable()
        if (result.user!!.displayName != null) {
            FirebaseConnection.firebase!!.child("users").child("profile").child(result.user!!.uid).child("info").child("name").setValue(result.user!!.displayName)
        }
        FirebaseConnection.firebase!!.child("users").child("profile").child(result.user!!.uid).child("info").child("sign_in_method").setValue(result.credential!!.signInMethod)
        if (result.user!!.email != null) {
            FirebaseConnection.firebase!!.child("users").child("profile").child(result.user!!.uid).child("info").child("email").setValue(result.user!!.email)
        }
        if (result.user!!.photoUrl != null) {
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
                        Logger.log(9, "network user exception", e)
                    }
                    override fun onComplete() {}
                })
        }
    }

    fun getAndSetValues(result: AuthResult,translateRoomRepository: TranslateRoomRepository,diaryRoomRepository: DiaryRoomRepository) {
        if (result.user == null) throw java.lang.NullPointerException("User is null")
        FirebaseConnection.firebase!!.child("users").child("profile").child(result.user!!.uid).child("translate").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val list: MutableList<TranslateItem> = ArrayList()
                    for (snapshot in dataSnapshot.children) {
                        val item = snapshot.getValue(TranslateItem::class.java)
                        list.add(item)
                    }
                    Log.d(FirebaseProvider.tag,"translate count - " + list.size)
                    translateRoomRepository.insertAllTranslates(list)
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
                    Log.d(FirebaseProvider.tag,"notes count - " + list.size)
                    diaryRoomRepository.insertAllNotes(list)
                }
            }
            override fun onCancelled(firebaseError: FirebaseError) {}
        })
    }
}