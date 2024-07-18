package com.example.todoapp.tasks.data.repositories

import com.example.todoapp.motherObject.UserMotherObject
import com.example.todoapp.tasks.data.models.UserModel
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class AuthRepositoryImpTest {

    @Mock
    lateinit var firebaseAuth: FirebaseAuth

    @Mock
    lateinit var firestore: FirebaseFirestore

    @Mock
    lateinit var authResult: AuthResult

    @Mock
    lateinit var querySnapshot: QuerySnapshot

    @Mock
    lateinit var collectionReference: CollectionReference

    @Mock
    lateinit var documentSnapshot: DocumentSnapshot

    private lateinit var repositoryImp: AuthRepositoryImp

    private val name = "Juan Centeno"
    private val email = "juancenteno132777@gmail.com"
    private val password = "12345678"

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        repositoryImp = AuthRepositoryImp(firestore, firebaseAuth)
    }

    @Test
    fun `loginUser should return Success when login is successful and user is found`() =
        runBlocking {
            // Given
            Mockito.`when`(firebaseAuth.signInWithEmailAndPassword(email, password))
                .thenReturn(Tasks.forResult(authResult))

            mockFirestoreUserFound()

            // When
            val response = repositoryImp.loginUser(email, password)

            // Then
            assertTrue(response is CreateUserResult.Success)
            val successResult = response as CreateUserResult.Success
            assertEquals(successResult.user.email, email)
            assertEquals(response.user, UserMotherObject.user)
        }

    @Test
    fun `loginUser should return Error when login throws an exception`() = runBlocking {
        // Given
        Mockito.`when`(firebaseAuth.signInWithEmailAndPassword(email, password))
            .thenThrow(RuntimeException("Login error"))

        // When
        val response = repositoryImp.loginUser(email, password)

        //Then

        assertTrue(response is CreateUserResult.Error)
        val errorResult = response as CreateUserResult.Error
        assertEquals(errorResult.error, "Error al iniciar sesión: Login error")
    }

    @Test
    fun `loginUser should return Error when userSnapshot is empty`() = runBlocking {
        // Given
        Mockito.`when`(firebaseAuth.signInWithEmailAndPassword(email, password))
            .thenReturn(Tasks.forResult(authResult))

        mockFirestoreUserFound(booleano = true)

        // When
        val response = repositoryImp.loginUser(email, password)

        //Then

        assertTrue(response is CreateUserResult.Error)
        val errorResult = response as CreateUserResult.Error
        assertEquals(errorResult.error, "Usuario no encontrado en la base de datos.")
    }

    @Test
    fun `loginUser should return Error when user is null`() = runBlocking {
        // Given
        Mockito.`when`(firebaseAuth.signInWithEmailAndPassword(email, password))
            .thenReturn(Tasks.forResult(authResult))

        mockFirestoreUserFound(userModel = null)

        // When
        val response = repositoryImp.loginUser(email, password)

        //Then
        assertTrue(response is CreateUserResult.Error)
        val errorResult = response as CreateUserResult.Error
        assertEquals(errorResult.error, "Error al obtener el usuario.")
    }

    private fun mockFirestoreUserFound(
        userModel: UserModel? = UserMotherObject.userModel,
        booleano: Boolean = false
    ) {
        Mockito.`when`(querySnapshot.isEmpty).thenReturn(booleano)
        Mockito.`when`(querySnapshot.documents).thenReturn(listOf(documentSnapshot))
        Mockito.`when`(documentSnapshot.toObject(UserModel::class.java)).thenReturn(userModel)

        Mockito.`when`(firestore.collection("Usuarios")).thenReturn(collectionReference)
        Mockito.`when`(collectionReference.whereEqualTo("email", email))
            .thenReturn(collectionReference)
        Mockito.`when`(collectionReference.get()).thenReturn(Tasks.forResult(querySnapshot))
    }
}
