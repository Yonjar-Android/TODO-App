package com.example.todoapp.tasks.data.models

import com.example.todoapp.tasks.domain.models.Category
import com.google.firebase.firestore.PropertyName

data class CategoryModel(
    @PropertyName("id") val id:String = "",
    @PropertyName("name") val name:String = ""
){
    fun toCategory():Category{
        return Category(
            id = id,
            name = name
        )
    }
}