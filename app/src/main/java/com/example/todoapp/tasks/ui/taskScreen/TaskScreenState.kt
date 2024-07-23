package com.example.todoapp.tasks.ui.taskScreen

import com.example.todoapp.tasks.domain.models.Category

sealed class TaskScreenState {
    data object Loading:TaskScreenState()

    data object Initial:TaskScreenState()

    data class Error(val error:String):TaskScreenState()

    data class Success(val message:String, val categories:List<Category>?): TaskScreenState()
}