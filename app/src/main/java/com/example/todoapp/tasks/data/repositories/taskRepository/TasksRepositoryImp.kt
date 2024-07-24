package com.example.todoapp.tasks.data.repositories.taskRepository

import com.example.todoapp.tasks.data.models.CategoryModel
import com.example.todoapp.tasks.data.models.TaskModel
import com.example.todoapp.tasks.domain.models.Category
import com.example.todoapp.tasks.domain.repositories.TasksRepository
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class TasksRepositoryImp @Inject constructor(
    private val firestore: FirebaseFirestore
) : TasksRepository {

    override suspend fun getAllCategories(): CategoryResult {

        return suspendCancellableCoroutine { continuation ->
            firestore.collection("Categorias").get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty) {
                        continuation.resume(CategoryResult.Error("No se encontraron categorías"))
                    } else {
                        println(convertCategories(querySnapshot.documents))
                        continuation.resume(CategoryResult.Success(convertCategories(querySnapshot.documents)))
                    }
                }.addOnFailureListener {
                    continuation.resume(CategoryResult.Error(it.message ?: ""))
                }
        }
    }

    override suspend fun getCategoryReference(name: String): TaskResult {
        return suspendCancellableCoroutine { continuation ->
            firestore.collection("Categorias")
                .whereEqualTo("name", name)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty) {
                        continuation.resume(TaskResult.Error("Categoria no encontrada"))
                    } else {
                        continuation.resume(
                            TaskResult.Success(
                                "",
                                querySnapshot.documents[0].reference
                            )
                        )
                    }

                }.addOnFailureListener {
                    continuation.resume(TaskResult.Error(it.message ?: ""))
                }
        }
    }

    override suspend fun createTask(
        name: String,
        description: String?,
        date: String,
        check: Boolean,
        deliverablesDescription: String?,
        deliverables: List<String>,
        users: List<String>,
        category: DocumentReference
    ): TaskResult {

        val task = TaskModel(
            name = name,
            description = description ?: "",
            date = date,
            check = check,
            users = users,
            deliverablesDesc = deliverablesDescription ?: "",
            deliverables = deliverables,
            category = category
        )

        return suspendCancellableCoroutine { continuation ->


            firestore.collection("Tareas").add(task)
                .addOnSuccessListener {
                    continuation.resume(TaskResult.Success("Se ha creado la tarea con éxito"))
                }.addOnFailureListener {
                    println("Error: ${it.message}")
                    continuation.resume(TaskResult.Error(it.message ?: ""))
                }
        }

    }

    private fun convertCategories(documents: MutableList<DocumentSnapshot>): List<Category> {
        return documents.mapNotNull { document ->
            document.toObject(CategoryModel::class.java)?.toCategory()
        }
    }
}