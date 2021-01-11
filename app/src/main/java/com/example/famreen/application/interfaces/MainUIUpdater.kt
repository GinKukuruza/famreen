package com.example.famreen.application.interfaces

interface MainUIUpdater {
    fun <T>updateUI(user: T)
    fun exit()
}