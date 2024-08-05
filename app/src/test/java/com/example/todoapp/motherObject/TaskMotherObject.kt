package com.example.todoapp.motherObject

import com.example.todoapp.tasks.data.repositories.taskRepository.CategoryResult
import com.example.todoapp.tasks.data.repositories.taskRepository.TaskResult
import com.example.todoapp.tasks.domain.models.Category

object TaskMotherObject {

    private val errorMessage = "An error has occurred"
    private val categoryId = "category1"

    private val categoryList = listOf(
        Category(
            id = "category1",
            "Documents"
        ),
        Category(
            id = "category2",
            "Tasks"
        ),
        Category(
            id = "category3",
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