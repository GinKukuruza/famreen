package com.example.famreen.states

import android.view.View
import android.view.WindowManager

sealed class ScreenStates {
    class CreateState(val view: View,val params: WindowManager.LayoutParams): ScreenStates()
    class UpdateState(val view: View,val params: WindowManager.LayoutParams): ScreenStates()
    class RemoveState(val view: View): ScreenStates()
    class OpenState(val screen: String): ScreenStates()
}