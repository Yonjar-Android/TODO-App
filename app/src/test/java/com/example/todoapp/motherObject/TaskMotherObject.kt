package com.example.todoapp.motherObject

import com.example.todoapp.tasks.data.repositories.taskRepository.CategoryResult
import com.example.todoapp.tasks.data.repositories.taskRepository.TaskResult
import com.example.todoapp.tasks.domain.models.Category

object TaskMotherObject {

  private val errorMessage = "An error has occurred"

    private val categoryList = listOf(
        Category(
            "Documents"
        ),
        Category(
            "Tasks"
        ),
        Category(
            "Images"
        )
    )


    val categoryResult = CategoryResult.Success(
        categories = categoryList
    )

    val categoryResultError = CategoryResult.Error(errorMessage)

    val TaskResultSuccess = TaskResult.Success("Success Message")
    val TaskResultError = TaskResult.Error(errorMessage)

}