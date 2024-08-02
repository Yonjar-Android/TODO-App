package com.example.todoapp.tasks.data.repositories.authRepository

import com.example.todoapp.R
import com.example.todoapp.tasks.data.models.UserModel
import com.example.todoapp.tasks.domain.models.UserM
import com.example.todoapp.tasks.domain.repositories.UserAuthRepository
import com.example.todoapp.tasks.utils.ResourceProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume


class AuthRepositoryImp @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val resourceProvider: ResourceProvider
) : UserAuthRepository {

    override suspend fun loginUser(email: String, password: String): CreateUserResult {
        return kotlin.runCatching {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            // Si el inicio de sesión es exitoso, busca el usuario en Firestore
            val userSnapshot = firestore.collection("Usuarios").whereEqualTo("email", email)
                .get()
                .await()
            if (userSnapshot.isEmpty) {
                CreateUserResult.Error(resourceProvider.getString(R.string.login_user_error_user_not_found))
            } else {
                // Itera sobre los documentos y convierte cada uno a UserModel
                val user = userSnapshot.documents[0].toObject(UserModel::class.java)
                if (user != null) {
                    CreateUserResult.Success(user.toUser()) // Devuelve el primer usuario encontrado
                } else {
                    CreateUserResult.Error(resourceProvider.getString(R.string.login_user_error_retrieval_failed))
                }
            }
        }.getOrElse {
            println(it.message)
            CreateUserResult.Error(it.message ?: "")
        }
    }

    override suspend fun logOutUser(): ResetResult {
       return try {
           firebaseAuth.signOut()
           ResetResult.Success(resourceProvider.getString(R.string.logout_success))
       } catch (e: Exception){
           ResetResult.Error(e.message ?: "")
       }
    }

    override suspend fun createUser(
        name: String,
        email: String,
        password: String
    ): CreateUserResult {

        if (checkEmailExists(email)) {
            return CreateUserResult.Error(resourceProvider.getString(R.string.create_user_error_email_registered))
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
                    println("${resourceProvider.getString(R.string.error_verifying_email)} ${it.message}")
                    continuation.resume(CreateUserResult.Error(it.message))
                }
        }
    }

    override suspend fun resetPassword(email: String): ResetResult {
        return try {
            if (checkEmailExists(email)){
                firebaseAuth.sendPasswordResetEmail(email)
                ResetResult.Success(resourceProvider.getString(R.string.reset_password_success))
            } else{
                ResetResult.Error(resourceProvider.getString(R.string.reset_password_error_email_not_exist))
            }
        } catch (e: Exception) {
            ResetResult.Error(e.message ?: "")// Error message with exception details
        }
    }

    override suspend fun getUserByEmail(email: String): UserResult {
        return suspendCancellableCoroutine { continuation ->
            firestore.collection("Usuarios").whereEqualTo("email",email).get()
                .addOnSuccessListener { querySnapShot ->
                    if (querySnapShot.isEmpty){
                        continuation.resume(UserResult.Error(resourceProvider.getString(R.string.get_user_error_user_not_found)))
                    } else{
                        continuation.resume(UserResult.Success(convertToUser(querySnapShot)))
                    }
                }.addOnFailureListener {
                    continuation.resume(UserResult.Error(it.message ?: ""))
                }
        }
    }

    private fun convertToUser(querySnapshot: QuerySnapshot): UserM {
        return querySnapshot.documents[0].toObject(UserModel::class.java)!!.toUser()
    }

    private suspend fun createAuthUser(email: String, password: String): Boolean {
        return runCatching {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            true
        }.getOrElse {
            println("${resourceProvider.getString(R.string.error_authentication)} ${it.message}")
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
                    println("${resourceProvider.getString(R.string.error_verifying_email)} ${it.message}")
                    continuation.resume(false)
                }
        }
    }
}
