package com.example.todoapp.tasks.data.repositories.authRepository

sealed class UsersResult {

    data class Error(val error:String):UsersResult()

    data class Success(val listUsers:List<String>):UsersResult()

}