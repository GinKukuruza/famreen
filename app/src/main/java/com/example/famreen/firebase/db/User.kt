package com.example.famreen.firebase.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class User(val name: String,val email:String,val image_uri: String?) {
    @PrimaryKey
    var id = 0
}