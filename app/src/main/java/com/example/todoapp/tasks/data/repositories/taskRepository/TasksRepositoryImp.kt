package com.example.todoapp.tasks.data.repositories.taskRepository

import com.example.todoapp.R
import com.example.todoapp.tasks.data.models.CategoryModel
import com.example.todoapp.tasks.data.models.TaskModel
import com.example.todoapp.tasks.domain.models.Category
import com.example.todoapp.tasks.domain.models.TaskDom
import com.example.todoapp.tasks.domain.repositories.TasksRepository
import com.example.todoapp.tasks.utils.ResourceProvider
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class TasksRepositoryImp @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val resourceProvider: ResourceProvider
) : TasksRepository {

    override suspend fun getAllCategories(): CategoryResult {

        return suspendCancellableCoroutine { continuation ->
            firestore.collection("Categorias").get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty) {
                        continuation.resume(CategoryResult.Error(resourceProvider.getString(R.string.categories_not_found)))
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
                        continuation.resume(TaskResult.Error(resourceProvider.getString(R.string.category_not_found)))
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
        category: DocumentReference,
        creationDate: String
    ): TaskResult {

        val task = TaskModel(
            name = name,
            description = description ?: "",
            date = date,
            check = check,
            users = users,
            deliverablesDesc = deliverablesDescription ?: "",
            deliverables = deliverables,
            category = category,
            creationDate = creationDate
        )

        return suspendCancellableCoroutine { continuation ->


            val documentReference = firestore.collection("Tareas").document()

                documentReference.set((task.copy(taskId = documentReference.id)))
                .addOnSuccessListener {
                    continuation.resume(TaskResult.Success(resourceProvider.getString(R.string.task_created_success)))
                }.addOnFailureListener {
                    println("Error: ${it.message}")
                    continuation.resume(TaskResult.Error(it.message ?: ""))
                }
        }

    }

    override suspend fun updateTask(
        taskId: String,
        name: String,
        description: String?,
        date: String,
        check: Boolean,
        deliverablesDescription: String?,
        deliverables: List<String>,
        users: List<String>,
        category: DocumentReference
    ): TaskResult {
        return suspendCancellableCoroutine { continuation ->

            // Referencia al documento de la tarea en la colección "Tareas"
            val taskDocumentRef = firestore.collection("Tareas").document(taskId)

            val updates = hashMapOf<String, Any?>(
                "name" to name,
                "description" to description,
                "date" to date,
                "check" to check,
                "deliverablesDescription" to deliverablesDescription,
                "deliverables" to deliverables,
                "users" to users,
                "category" to category
            )

            taskDocumentRef.update(updates)
                .addOnSuccessListener {
                    continuation.resume(TaskResult.Success(resourceProvider.getString(R.string.task_updated_success)))
                }
                .addOnFailureListener { e ->
                    continuation.resume(TaskResult.Error(e.message ?: ""))
                }

        }
    }

    private fun convertCategories(documents: MutableList<DocumentSnapshot>): List<Category> {
        return documents.mapNotNull { document ->
            document.toObject(CategoryModel::class.java)?.toCategory()
        }
    }

    override suspend fun getAllTasks(): TaskResult {
        return suspendCancellableCoroutine { continuation ->
            firestore.collection("Tareas").get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty) {
                        continuation.resume(TaskResult.Error(""))

                    } else {
                        continuation.resume(
                            TaskResult.Success(
                                "",
                                tasks = convertTasks(querySnapshot.documents)
                            )
                        )
                    }
                }.addOnFailureListener {
                    println("Error: ${it.message}")
                    continuation.resume(TaskResult.Error(it.message ?: ""))
                }
        }
    }

    override suspend fun getTaskById(taskId: String): TaskDetailResult {
        return suspendCancellableCoroutine { continuation ->

            firestore.collection("Tareas")
                .whereEqualTo("taskId", taskId).get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty) {
                        continuation.resume(TaskDetailResult.Error(resourceProvider.getString(R.string.task_not_found)))
                    } else {
                        val taskModel = querySnapshot.documents[0].toObject(TaskModel::class.java)
                        if (taskModel != null) {
                            val categoryReference = taskModel.category
                            categoryReference?.get()
                                ?.addOnSuccessListener { categoryDocument ->
                                    if (categoryDocument.exists()) {
                                        val categoryName = categoryDocument.getString("name")
                                        continuation.resume(TaskDetailResult.Success(taskModel.toTask(), categoryName ?: "Categoría desconocida"))
                                    } else {
                                        continuation.resume(TaskDetailResult.Error(resourceProvider.getString(R.string.category_not_found)))
                                    }
                                }?.addOnFailureListener {
                                    continuation.resume(TaskDetailResult.Error(it.message ?: resourceProvider.getString(R.string.error_getting_category)))
                                }
                        } else {
                            continuation.resume(TaskDetailResult.Error(resourceProvider.getString(R.string.error_converting_task)))
                        }
                    }
                }.addOnFailureListener {
                    continuation.resume(TaskDetailResult.Error(it.message ?: resourceProvider.getString(R.string.error_getting_task)))
                }
        }
    }

    override suspend fun deleteTask(taskId: String): TaskResult {
        return suspendCancellableCoroutine { continuation ->
            firestore.collection("Tareas").document(taskId).delete()
                .addOnSuccessListener {
                    continuation.resume(TaskResult.Success(resourceProvider.getString(R.string.task_deleted_success)))
                }
                .addOnFailureListener { exception ->
                    continuation.resume(TaskResult.Error(exception.message ?: ""))
                }
        }
    }

    override suspend fun onCheckChange(taskId: String, check: Boolean): TaskResult {
        return suspendCancellableCoroutine { continuation ->
            firestore.collection("Tareas").document(taskId)
                .update("check", check)
                .addOnSuccessListener {
                    continuation.resume(TaskResult.Success("Se actualizo"))
                }
                .addOnFailureListener { e ->
                    continuation.resume(TaskResult.Error(e.message ?: resourceProvider.getString(R.string.update_failed)))
                }
        }
    }


    private fun convertTasks(documents: MutableList<DocumentSnapshot>): List<TaskDom> {
        return documents.mapNotNull { document ->
            document.toObject(TaskModel::class.java)?.toTask()
        }
    }

}