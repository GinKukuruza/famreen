package com.example.famreen.application.interfaces

interface ItemHelper {
    fun onItemMove(fromPosition: Int, toPosition: Int): Boolean
    fun onItemDismiss(position: Int)
}