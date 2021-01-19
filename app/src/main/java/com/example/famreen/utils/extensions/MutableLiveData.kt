package com.example.famreen.utils.extensions

import androidx.lifecycle.MutableLiveData

fun <T : Any?> MutableLiveData<T>.default(initialValue: T) = apply{setValue(initialValue)}

fun <T> MutableLiveData<T>.set(newValue: T) = apply { setValue(newValue) }