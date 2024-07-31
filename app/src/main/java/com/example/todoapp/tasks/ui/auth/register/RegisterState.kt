package com.example.todoapp.tasks.ui.auth.register

import com.example.todoapp.tasks.domain.models.UserM

sealed class RegisterState {

    data object Initial: RegisterState()

    data object Loading: RegisterState()

    data class Error(val error:String?): RegisterState()

    data class Success(val user:UserM): RegisterState()
}