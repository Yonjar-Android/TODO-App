package com.example.todoapp.motherObject

import com.example.todoapp.tasks.data.models.UserModel
import com.example.todoapp.tasks.data.repositories.CreateUserResult

object UserMotherObject {
    val userModel = UserModel(
        name = "Juan Centeno",
        email = "juancenteno132777@gmail.com"
    )

    val user = userModel.toUser()

     val createUserResult = CreateUserResult.Success(UserMotherObject.user)
     val createErrorNullUserResult = CreateUserResult.Error(null)
     val createErrorUserResult = CreateUserResult.Error("An error has occurred")
}