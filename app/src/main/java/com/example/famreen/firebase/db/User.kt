package com.example.famreen.firebase.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(val mName: String,val mEmail:String,val mImageUri: String?) {
    @PrimaryKey
    var id = 0
}