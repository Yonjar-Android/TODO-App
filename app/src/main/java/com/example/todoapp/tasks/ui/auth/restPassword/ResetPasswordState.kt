package com.example.todoapp.tasks.ui.auth.restPassword

sealed class ResetPasswordState {
    data object Initial: ResetPasswordState()

    data object Loading: ResetPasswordState()

    data class Error(val error:String?): ResetPasswordState()

    data class Success(val success:String): ResetPasswordState()
}