package com.example.todoapp.tasks.data.repositories.authRepository

sealed class ResetResult {
    data class Success(val message:String): ResetResult()

    data class Error(val error:String): ResetResult()
}