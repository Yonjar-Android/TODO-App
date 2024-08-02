package com.example.todoapp.tasks.ui.register

import app.cash.turbine.test
import com.example.todoapp.TestCoroutineRule
import com.example.todoapp.motherObject.UserMotherObject
import com.example.todoapp.tasks.data.repositories.authRepository.AuthRepositoryImp
import com.example.todoapp.tasks.ui.auth.register.RegisterState
import com.example.todoapp.tasks.ui.auth.register.RegisterViewModel
import com.example.todoapp.tasks.utils.ResourceProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest {

    @get: Rule
    val dispatcherRule = TestCoroutineRule()

    @Mock
    lateinit var repositoryImpTest: AuthRepositoryImp

    @Mock
    lateinit var resourceProvider: ResourceProvider

    private lateinit var viewModel: RegisterViewModel

    private val name: String = "Juan Centeno"
    private val email: String = "juancenteno132777@gmail.com"
    private val password = "12345678"
    private val password2 = "12345678"

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        viewModel = RegisterViewModel(repositoryImpTest,resourceProvider)
    }

    @Test
    fun `createUser should emit Success state on successful user creation`() =
        runTest {
            //Given
            Mockito.`when`(repositoryImpTest.createUser(name, email, password))
                .thenReturn(UserMotherObject.createUserResult)

            //When

            viewModel.createUser(name, email, password, password2)

            //Then
            viewModel.state.test {
                assertTrue(awaitItem() is RegisterState.Loading)
                advanceUntilIdle()
                val state = awaitItem()
                assertTrue(state is RegisterState.Success)
                val successState = state as RegisterState.Success
                assertEquals(successState.user, UserMotherObject.createUserResult.user)
            }

        }

    @Test
    fun `createUser should emit Error state on null user creation response`() =
        runTest {
            //Given
            Mockito.`when`(repositoryImpTest.createUser(name, email, password))
                .thenReturn(UserMotherObject.createErrorNullUserResult)

            //When

            viewModel.createUser(name, email, password, password2)

            //Then
            viewModel.state.test {
                assertTrue(awaitItem() is RegisterState.Loading)
                advanceUntilIdle()
                val state = awaitItem()
                assertTrue(state is RegisterState.Error)
                val errorState = state as RegisterState.Error
                assertEquals(errorState.error, UserMotherObject.createErrorNullUserResult.error)
            }
        }

    @Test
    fun `createUser should emit Error state on user creation error response`() =
        runTest {
            //Given
            Mockito.`when`(repositoryImpTest.createUser(name, email, password))
                .thenReturn(UserMotherObject.createErrorUserResult)

            //When

            viewModel.createUser(name, email, password, password2)

            //Then
            viewModel.state.test {
                assertTrue(awaitItem() is RegisterState.Loading)
                advanceUntilIdle()
                val state = awaitItem()
                assertTrue(state is RegisterState.Error)
                val errorState = state as RegisterState.Error
                assertEquals(errorState.error, "Error: An error has occurred")
            }

        }

    @Test
    fun `createUser should emit Error state if an exception occurs during user creation`() =
        runTest {
            //Given
            Mockito.`when`(repositoryImpTest.createUser(name, email, password))
                .thenThrow(RuntimeException("Exception happened"))

            //When

            viewModel.createUser(name, email, password, password2)

            //Then
            viewModel.state.test {
                assertTrue(awaitItem() is RegisterState.Loading)
                advanceUntilIdle()
                val state = awaitItem()
                assertTrue(state is RegisterState.Error)
                val errorState = state as RegisterState.Error
                assertEquals(errorState.error, "Error: Exception happened")
            }

        }


}