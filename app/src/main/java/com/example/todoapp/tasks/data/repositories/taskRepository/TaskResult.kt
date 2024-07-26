package com.example.todoapp.tasks.data.repositories.taskRepository

import com.example.todoapp.tasks.domain.models.TaskDom
import com.google.firebase.firestore.DocumentReference

sealed class TaskResult {

    data class Error(val error: String) : TaskResult()

    data class Success(
        val message: String,
        val documentReference: DocumentReference? = null,
        val tasks: List<TaskDom> = listOf()
    ) :
        TaskResult()

}