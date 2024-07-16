package com.example.todoapp.tasks.domain.repositories

import com.example.todoapp.tasks.data.repositories.CreateUserResult
import com.example.todoapp.tasks.domain.models.User

interface UserAuthRepository {

    suspend fun loginUser(email:String, password:String):CreateUserResult

    suspend fun createUser(name:String, email: String, password: String): CreateUserResult

    suspend  fun createAuthUser(email: String, password: String): Boolean

}