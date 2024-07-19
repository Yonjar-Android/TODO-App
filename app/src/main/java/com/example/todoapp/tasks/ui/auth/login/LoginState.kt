package com.example.todoapp.tasks.ui.auth.login

import com.example.todoapp.tasks.domain.models.User

sealed class LoginState {

    data object Initial: LoginState()

    data object Loading: LoginState()

    data class Error(val error:String?): LoginState()

    data class Success(val user:User): LoginState()

}