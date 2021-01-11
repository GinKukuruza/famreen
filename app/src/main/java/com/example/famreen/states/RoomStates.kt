package com.example.famreen.states

sealed class RoomStates{
    class InsertState(val isSuccess: Boolean): RoomStates()
    class DeleteState(val isDeleted: Boolean): RoomStates()
}