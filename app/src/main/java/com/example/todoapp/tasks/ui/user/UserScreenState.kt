package com.example.todoapp.tasks.ui.user

import com.example.todoapp.tasks.domain.models.UserM

sealed class UserScreenState {

    data object Loading:UserScreenState()

    data object Initial: UserScreenState()

    data class Error(val error:String):UserScreenState()

    data class Success(val user:UserM):UserScreenState()

}