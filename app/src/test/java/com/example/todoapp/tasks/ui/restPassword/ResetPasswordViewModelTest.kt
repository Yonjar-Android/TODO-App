package com.example.todoapp.tasks.ui.restPassword

import app.cash.turbine.test
import com.example.todoapp.TestCoroutineRule
import com.example.todoapp.tasks.data.repositories.authRepository.AuthRepositoryImp
import com.example.todoapp.tasks.data.repositories.authRepository.ResetResult
import com.example.todoapp.tasks.ui.auth.restPassword.ResetPasswordState
import com.example.todoapp.tasks.ui.auth.restPassword.ResetPasswordViewModel
import com.example.todoapp.tasks.utils.ResourceProvider
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
class ResetPasswordViewModelTest{
    @get:Rule
    val testCoroutine = TestCoroutineRule()

    @Mock
    lateinit var repositoryImp: AuthRepositoryImp

    @Mock
    lateinit var resourceProvider: ResourceProvider

    private lateinit var viewModel: ResetPasswordViewModel

    private val email = "juan132y@gmail.com"

    @Before
    fun setUp(){
        MockitoAnnotations.openMocks(this)
        viewModel = ResetPasswordViewModel(repositoryImp,resourceProvider)
    }

    @Test
    fun `resetPassword with valid email should change state to Success`() = runTest {
        //Given
        Mockito.`when`(repositoryImp.resetPassword(email)).thenReturn(ResetResult.Success("Se le ha enviado un email para restablecer su contraseña"))

        //When
        viewModel.resetPassword(email)

        //Then
        viewModel.state.test {
            assertTrue(awaitItem() is ResetPasswordState.Loading)
            advanceUntilIdle()
            val state = awaitItem()
            assertTrue(state is ResetPasswordState.Success)
            val successState = state as ResetPasswordState.Success
            assertEquals(successState.success, "Se le ha enviado un email para restablecer su contraseña")
        }
    }

    @Test
    fun `resetPassword with ResetResult_Error should change state to Error`() = runTest {
        //Given
        Mockito.`when`(repositoryImp.resetPassword(email)).thenThrow(RuntimeException("An error has occurred"))

        //When
        viewModel.resetPassword(email)

        //Then
        viewModel.state.test {
            assertTrue(awaitItem() is ResetPasswordState.Loading)
            advanceUntilIdle()
            val state = awaitItem()
            assertTrue(state is ResetPasswordState.Error)
            val errorState = state as ResetPasswordState.Error
            assertEquals(errorState.error, "Error: An error has occurred")
        }
    }

    @Test
    fun `resetPassword should change the state to error on ResetResult_Error received`() = runTest {
        //Given
        Mockito.`when`(repositoryImp.resetPassword(email)).thenReturn(ResetResult.Error("An error has occurred"))

        //When
        viewModel.resetPassword(email)

        //Then
        viewModel.state.test {
            assertTrue(awaitItem() is ResetPasswordState.Loading)
            advanceUntilIdle()
            val state = awaitItem()
            assertTrue(state is ResetPasswordState.Error)
            val errorState = state as ResetPasswordState.Error
            assertEquals(errorState.error, "Error: An error has occurred")
        }
    }
}