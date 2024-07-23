package com.example.todoapp.tasks.ui.login

import app.cash.turbine.test
import com.example.todoapp.TestCoroutineRule
import com.example.todoapp.motherObject.UserMotherObject
import com.example.todoapp.tasks.data.repositories.authRepository.AuthRepositoryImp
import com.example.todoapp.tasks.ui.auth.login.LoginState
import com.example.todoapp.tasks.ui.auth.login.LoginViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    @get: Rule
    val testCoroutine = TestCoroutineRule()

    @Mock
    lateinit var repositoryImpTest: AuthRepositoryImp

    private lateinit var viewModel: LoginViewModel

    private val email: String = "juancenteno132777@gmail.com"
    private val password = "12345678"

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        viewModel = LoginViewModel(repositoryImpTest)
    }

    @Test
    fun `loginUser should emit success state on successful login`() = runTest {
        //Given
        Mockito.`when`(repositoryImpTest.loginUser(email, password))
            .thenReturn(UserMotherObject.createUserResult)

        //When
        viewModel.loginUser(email, password)

        //Then
        viewModel.state.test {
            assertTrue(awaitItem() is LoginState.Loading)
            advanceUntilIdle()
            val state = awaitItem()
            assertTrue(state is LoginState.Success)
            val successState = state as LoginState.Success
            assertEquals(successState.user, UserMotherObject.createUserResult.user)
        }
    }

    @Test
    fun `loginUser should emit Error state on null user creation response`() = runTest {
        //Given
        Mockito.`when`(repositoryImpTest.loginUser(email, password))
            .thenReturn(UserMotherObject.createErrorNullUserResult)

        //When

        viewModel.loginUser(email, password)

        //Then
        viewModel.state.test {
            assertTrue(awaitItem() is LoginState.Loading)
            advanceUntilIdle()
            val state = awaitItem()
            assertTrue(state is LoginState.Error)
            val errorState = state as LoginState.Error
            assertEquals(errorState.error, "Error: null")
        }
    }

    @Test
    fun `createUser should emit Error state on user creation error response`() =
        runTest {
            //Given
            Mockito.`when`(repositoryImpTest.loginUser(email, password))
                .thenReturn(UserMotherObject.createErrorUserResult)

            //When
            viewModel.loginUser(email, password)

            //Then
            viewModel.state.test {
                assertTrue(awaitItem() is LoginState.Loading)
                advanceUntilIdle()
                val state = awaitItem()
                assertTrue(state is LoginState.Error)
                val errorState = state as LoginState.Error
                assertEquals(errorState.error, "Error: An error has occurred")
            }
        }

    @Test
    fun `loginUser should emit Error state if an exception occurs during login`() =
        runTest {
            //Given
            Mockito.`when`(repositoryImpTest.loginUser(email, password))
                .thenThrow(RuntimeException("Exception happened"))

            //When
            viewModel.loginUser(email, password)

            //Then
            viewModel.state.test {
                assertTrue(awaitItem() is LoginState.Loading)
                advanceUntilIdle()
                val state = awaitItem()
                assertTrue(state is LoginState.Error)
                val errorState = state as LoginState.Error
                assertEquals(errorState.error, "Error: Exception happened")
            }

        }
}