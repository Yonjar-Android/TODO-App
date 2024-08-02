package com.example.todoapp.tasks.domain.models

import com.google.firebase.firestore.DocumentReference

data class TaskDom(
    val taskId:String,
    val name:String,
    val description:String?,
    val check:Boolean,
    val date:String,
    val users:List<String>,
    val deliverables:List<String>,
    val deliverablesDesc:String,
    val category:DocumentReference?,
    val creationDate:String
)
