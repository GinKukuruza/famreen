package com.example.famreen.application.interfaces

import androidx.recyclerview.selection.ItemDetailsLookup

interface ViewHolderDetails<T> {
    val itemDetails: ItemDetailsLookup.ItemDetails<T>?
}