package com.example.famreen.application.custom.predicate

import androidx.recyclerview.selection.SelectionTracker
import com.example.famreen.application.items.NoteItem

class RVPredicate : SelectionTracker.SelectionPredicate<NoteItem>(){
    override fun canSetStateForKey(key: NoteItem, nextState: Boolean): Boolean {
        return true
    }

    override fun canSetStateAtPosition(position: Int, nextState: Boolean): Boolean {
        return true
    }

    override fun canSelectMultiple(): Boolean {
        return true
    }

}