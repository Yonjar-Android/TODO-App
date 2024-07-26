package com.example.todoapp.tasks.ui.taskScreen.tasks

sealed class TaskScreenState {
    data object Loading : TaskScreenState()

    data object Initial : TaskScreenState()

    data class Error(val error: String) : TaskScreenState()

    data class Success(val message: String) : TaskScreenState()
}