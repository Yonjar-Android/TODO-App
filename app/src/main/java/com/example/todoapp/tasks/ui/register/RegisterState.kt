package com.example.todoapp.tasks.ui.register

import com.example.todoapp.tasks.domain.models.User

sealed class RegisterState {

    data object Initial:RegisterState()

    data object Loading:RegisterState()

    data class Error(val error:String?):RegisterState()

    data class Success(val user:User):RegisterState()
}