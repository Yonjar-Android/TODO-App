package com.example.todoapp.tasks.data.models

import com.example.todoapp.tasks.domain.models.User
import com.google.firebase.firestore.PropertyName


data class UserModel(
    @PropertyName("email") val email:String = "",
    @PropertyName("name") val name:String = "",
){
    fun toUser() = User(
        name = name,
        email = email,
    )
}
