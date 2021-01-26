@file:Suppress("CanSealedSubClassBeObject")

package com.example.famreen.states

sealed class States{
    class DefaultState : States()
    class LoadingState : States()
    class ErrorState(val msg: String): States()
    class UserState<T>(val user: T?): States()
    class SuccessState<T>(val list: List<T>?): States()
}