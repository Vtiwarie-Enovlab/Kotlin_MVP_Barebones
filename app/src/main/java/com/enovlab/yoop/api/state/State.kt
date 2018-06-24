package com.enovlab.yoop.api.state

sealed class State {
    object Success : State()
    data class Loading(val type: LoadingType) : State()
    data class Error(val type: ErrorType, val message: String? = null,
                     val action: (() -> Unit)? = null) : State()
}