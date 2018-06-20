package com.enovlab.yoop.api.state

/**
 * Created by Max Toskhoparan on 2/7/2018.
 */

sealed class State {
    object Success : State()
    data class Loading(val type: LoadingType) : State()
    data class Error(val type: ErrorType, val message: String? = null,
                     val action: (() -> Unit)? = null) : State()
}