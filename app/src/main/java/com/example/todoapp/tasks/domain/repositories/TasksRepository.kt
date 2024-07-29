package com.example.todoapp.tasks.domain.repositories

import com.example.todoapp.tasks.data.repositories.taskRepository.CategoryResult
import com.example.todoapp.tasks.data.repositories.taskRepository.TaskDetailResult
import com.example.todoapp.tasks.data.repositories.taskRepository.TaskResult
import com.google.firebase.firestore.DocumentReference

interface TasksRepository {
    suspend fun getAllCategories(): CategoryResult

    suspend fun getCategoryReference(name:String):TaskResult

    suspend fun createTask(
        name: String,
        description: String?,
        date: String,
        check: Boolean = false,
        deliverablesDescription:String?,
        deliverables:List<String> = listOf(),
        users:List<String> = listOf(),
        category:DocumentReference
    ): TaskResult

    suspend fun getAllTasks(): TaskResult

    suspend fun getTaskById(taskId:String): TaskDetailResult

    suspend fun onCheckChange(taskId:String, check:Boolean):TaskResult

}