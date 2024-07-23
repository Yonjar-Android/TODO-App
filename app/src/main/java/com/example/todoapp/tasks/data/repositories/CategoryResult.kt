package com.example.todoapp.tasks.data.repositories

import com.example.todoapp.tasks.domain.models.Category

sealed class CategoryResult {

    data class Success(val categories:List<Category>?):CategoryResult()

    data class Error(val error:String):CategoryResult()

}