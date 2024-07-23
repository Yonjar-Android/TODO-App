package com.example.todoapp.tasks.domain.repositories

import com.example.todoapp.tasks.data.repositories.CategoryResult

interface TasksRepository {
    suspend fun getAllCategories():CategoryResult
}