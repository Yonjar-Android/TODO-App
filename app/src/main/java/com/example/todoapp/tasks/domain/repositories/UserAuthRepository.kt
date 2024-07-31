package com.example.todoapp.tasks.domain.repositories

import com.example.todoapp.tasks.data.repositories.authRepository.CreateUserResult
import com.example.todoapp.tasks.data.repositories.authRepository.ResetResult
import com.example.todoapp.tasks.data.repositories.authRepository.UserResult

interface UserAuthRepository {

    suspend fun loginUser(email:String, password:String): CreateUserResult

    suspend fun createUser(name:String, email: String, password: String): CreateUserResult

    suspend fun resetPassword(email: String): ResetResult

    suspend fun getUserByEmail(email:String): UserResult
}