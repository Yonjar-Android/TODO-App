package com.example.todoapp.tasks.data.repositories

import com.example.todoapp.tasks.data.models.CategoryModel
import com.example.todoapp.tasks.domain.models.Category
import com.example.todoapp.tasks.domain.repositories.TasksRepository
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

    private fun convertCategories(documents: MutableList<DocumentSnapshot>): List<Category> {
        return documents.mapNotNull { document ->
            document.toObject(CategoryModel::class.java)?.toCategory()
        }
    }
}