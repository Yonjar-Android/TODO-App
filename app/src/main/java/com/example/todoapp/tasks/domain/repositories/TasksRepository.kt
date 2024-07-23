package com.example.todoapp.tasks.domain.repositories

import com.example.todoapp.tasks.data.repositories.taskRepository.CategoryResult
import com.example.todoapp.tasks.data.repositories.taskRepository.TaskResult

interface TasksRepository {
    suspend fun getAllCategories(): CategoryResult

    suspend fun createTask(
        name: String,
        description: String?,
        date: String,
        check: Boolean = false,
        deliverablesDescription:String?,
        deliverables:List<String> = listOf(),
        users:List<String> = listOf()
    ): TaskResult
}