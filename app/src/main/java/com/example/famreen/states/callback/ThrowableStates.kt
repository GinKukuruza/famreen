package com.example.famreen.states.callback

import com.firebase.client.FirebaseError

sealed class ThrowableStates: CallbackStates(){
    class FailureStates(val msg: String, val ex: Throwable): ThrowableStates()
    class ErrorStates(val msg: String, val ex: Throwable): ThrowableStates()
    class CancelledStates(val msg: String, val err: FirebaseError): ThrowableStates()
}