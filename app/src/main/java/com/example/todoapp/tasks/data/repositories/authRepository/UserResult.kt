package com.example.todoapp.tasks.data.repositories.authRepository

import com.example.todoapp.tasks.domain.models.UserM

sealed class UserResult {
    data class Error(val error:String): UserResult()

    data class Success(val user:UserM): UserResult()
}