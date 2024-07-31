package com.example.todoapp.tasks.data.models

import com.example.todoapp.tasks.domain.models.UserM
import com.google.firebase.firestore.PropertyName


data class UserModel(
    @PropertyName("email") val email:String = "",
    @PropertyName("name") val name:String = "",
){
    fun toUser() = UserM(
        name = name,
        email = email,
    )
}
