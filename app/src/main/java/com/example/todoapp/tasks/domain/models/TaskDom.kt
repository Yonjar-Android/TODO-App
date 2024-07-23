package com.example.todoapp.tasks.domain.models

data class TaskDom(
    val name:String,
    val description:String?,
    val check:Boolean,
    val date:String,
    val users:List<String>,
    val deliverables:List<String>,
    val deliverablesDesc:String
)
