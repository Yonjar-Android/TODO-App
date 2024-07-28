package com.example.todoapp.tasks.ui.taskScreen.taskDetail

import com.example.todoapp.tasks.domain.models.TaskDom

sealed class TaskDetailState {

    data object Initial:TaskDetailState()

    data object Loading:TaskDetailState()

    data class Error(val error:String):TaskDetailState()

    data class Success(val message:String, val task:TaskDom):TaskDetailState()

}