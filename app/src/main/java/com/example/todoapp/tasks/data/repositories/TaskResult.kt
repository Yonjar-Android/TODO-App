package com.example.todoapp.tasks.data.repositories

sealed class TaskResult {

    data class Error(val error:String):TaskResult()

    data class Success(val message:String):TaskResult()

}