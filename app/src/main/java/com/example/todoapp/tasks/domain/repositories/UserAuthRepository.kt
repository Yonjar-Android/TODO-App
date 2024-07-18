package com.example.todoapp.tasks.domain.repositories

import com.example.todoapp.tasks.data.repositories.CreateUserResult
import com.example.todoapp.tasks.data.repositories.ResetResult

interface UserAuthRepository {

    suspend fun loginUser(email:String, password:String):CreateUserResult

    suspend fun createUser(name:String, email: String, password: String): CreateUserResult

    suspend fun resetPassword(email: String):ResetResult
}