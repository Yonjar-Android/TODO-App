package com.example.todoapp.tasks.data.repositories

import com.example.todoapp.tasks.domain.models.User

sealed class CreateUserResult {
    data class Success(val user:User):CreateUserResult()

    data class Error(val error:String?):CreateUserResult()
}