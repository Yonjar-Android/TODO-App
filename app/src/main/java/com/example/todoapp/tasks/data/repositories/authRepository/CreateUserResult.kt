package com.example.todoapp.tasks.data.repositories.authRepository

import com.example.todoapp.tasks.domain.models.UserM

sealed class CreateUserResult {
    data class Success(val user:UserM): CreateUserResult()

    data class Error(val error:String?): CreateUserResult()
}