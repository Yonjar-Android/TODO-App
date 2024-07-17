package com.example.todoapp.tasks.data.repositories

import com.example.todoapp.tasks.data.models.UserModel
import com.example.todoapp.tasks.domain.repositories.UserAuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume


class AuthRepositoryImp @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : UserAuthRepository {

    override suspend fun loginUser(email: String, password: String): CreateUserResult{
        return kotlin.runCatching {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            // Si el inicio de sesión es exitoso, busca el usuario en Firestore
            val userSnapshot = firestore.collection("Usuarios").whereEqualTo("email", email)
                .get()
                .await()
            if (userSnapshot.isEmpty) {
                CreateUserResult.Error("Usuario no encontrado en la base de datos.")
            } else {
                // Itera sobre los documentos y convierte cada uno a UserModel
               val user = userSnapshot.documents[0].toObject(UserModel::class.java)
                if (user != null) {
                    CreateUserResult.Success(user.toUser()) // Devuelve el primer usuario encontrado
                } else {
                    CreateUserResult.Error("Error al obtener el usuario.")
                }
            }
        }.getOrElse {
            CreateUserResult.Error("Error al iniciar sesión: ${it.message}")
        }
    }

    override suspend fun createUser(
        name: String,
        email: String,
        password: String
    ): CreateUserResult {

        if (checkEmailExists(email)) {
            return CreateUserResult.Error("El correo ya está registrado")
        }

        val user = UserModel(
            name = name,
            email = email
        )

        return suspendCancellableCoroutine { continuation ->
            // Verifica si el correo ya existe
            firestore.collection("Usuarios")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty) { // El correo no existe, procede con la creación del usuario
                        firestore.collection("Usuarios")
                            .add(user)
                            .addOnSuccessListener {
                                CoroutineScope(Dispatchers.IO).launch {
                                    if (createAuthUser(email, password)) {
                                        continuation.resume(CreateUserResult.Success(user.toUser()))
                                    } else {
                                        firestore.collection("Usuarios").document(it.id).delete()
                                        continuation.resume(CreateUserResult.Error(null))
                                    }
                                }
                            }.addOnFailureListener {
                                println("Error: ${it.message}")
                                continuation.resume(CreateUserResult.Error(it.message))
                            }
                    } else { // El correo ya existe, devuelve null o maneja el error de manera apropiada
                        continuation.resume(CreateUserResult.Error(null))
                    }
                }
                .addOnFailureListener {
                    println("Error al verificar el correo: ${it.message}")
                    continuation.resume(CreateUserResult.Error(it.message))
                }
        }
    }

     private suspend fun createAuthUser(email: String, password: String): Boolean{
        return runCatching {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            true
        }.getOrElse {
            println("Error during authentication: ${it.message}")
            false
        }
    }

     private suspend fun checkEmailExists(email: String): Boolean {
        return suspendCancellableCoroutine { continuation ->
            firestore.collection("Usuarios")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    continuation.resume(!querySnapshot.isEmpty)
                }
                .addOnFailureListener {
                    println("Error al verificar el correo: ${it.message}")
                    continuation.resume(false)
                }
        }
    }
}
