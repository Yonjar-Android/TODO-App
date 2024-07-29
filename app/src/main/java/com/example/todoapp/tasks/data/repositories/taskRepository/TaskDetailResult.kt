package com.example.todoapp.tasks.data.repositories.taskRepository

import com.example.todoapp.tasks.domain.models.TaskDom

sealed class TaskDetailResult {

    data class Error(val error:String): TaskDetailResult()

    data class Success(val task:TaskDom, val category:String): TaskDetailResult()
}