package com.example.todoapp.tasks.data.models

import com.example.todoapp.tasks.domain.models.User
import com.google.firebase.firestore.PropertyName

data class UserModel(
    @PropertyName("Correo") val email:String,
    @PropertyName("Nombre") val name:String,
){
    fun toUser() = User(
        name = name,
        email = email,
    )
}
