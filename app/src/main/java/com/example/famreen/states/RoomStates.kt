package com.example.famreen.states

sealed class RoomStates{
    class InsertItem(val isSuccess: Boolean): RoomStates()
    class InsertState(val isSuccess: Boolean): RoomStates()
    class DeleteState(val isDeleted: Boolean): RoomStates()
}