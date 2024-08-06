package com.example.todoapp.motherObject

import com.example.todoapp.tasks.data.models.UserModel
import com.example.todoapp.tasks.data.repositories.authRepository.CreateUserResult
import com.example.todoapp.tasks.data.repositories.authRepository.ResetResult
import com.example.todoapp.tasks.data.repositories.authRepository.UserResult
import com.example.todoapp.tasks.data.repositories.authRepository.UsersResult

object UserMotherObject {
    val userModel = UserModel(
        name = "Juan Centeno",
        email = "juancenteno132777@gmail.com"
    )

    val user = userModel.toUser()
    private val error = "An error has occurred"

    //CreateUserResults
     val createUserResult = CreateUserResult.Success(user)
     val createErrorNullUserResult = CreateUserResult.Error(null)
     val createErrorUserResult = CreateUserResult.Error(error)

    //UserResults
    val userResultSuccess = UserResult.Success(user)
    val userResultError = UserResult.Error(error)

    //ResetResults
    val resetResultSuccess = ResetResult.Success("Success message")
    val resetResultError = ResetResult.Error(error)

    val usersResultError = UsersResult.Error(error)
}