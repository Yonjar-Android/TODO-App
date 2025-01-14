package com.example.todoapp.tasks.data.models

import com.example.todoapp.tasks.domain.models.TaskDom
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.PropertyName

data class TaskModel(
    @PropertyName("taskId") val taskId: String = "",
    @PropertyName("name") val name: String = "",
    @PropertyName("description") val description: String = "",
    @PropertyName("date") val date: String = "",
    @PropertyName("check") val check: Boolean = false,
    @PropertyName("deliverablesDescription") val deliverablesDesc: String = "",
    @PropertyName("deliverables") val deliverables: List<String> = listOf(),
    @PropertyName("users") val users: List<String> = listOf(),
    @PropertyName("category") val category: DocumentReference? = null,
    @PropertyName("categoryId") val categoryId:String = "",
    @PropertyName("creationDate") val creationDate: String = "" // Nuevo atributo
) {
    fun toTask(): TaskDom {
        return TaskDom(
            taskId = taskId,
            name = name,
            description = description,
            check = check,
            date = date,
            deliverablesDesc = deliverablesDesc,
            deliverables = deliverables,
            users = users,
            category = category,
            categoryId = categoryId,
            creationDate = creationDate // Asegúrate de pasar el nuevo atributo
        )
    }
}