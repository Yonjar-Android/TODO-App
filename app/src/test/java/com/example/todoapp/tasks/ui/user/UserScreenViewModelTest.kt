package com.example.todoapp.tasks.ui.user

import app.cash.turbine.test
import com.example.todoapp.TestCoroutineRule
import com.example.todoapp.motherObject.UserMotherObject
import com.example.todoapp.tasks.data.repositories.authRepository.AuthRepositoryImp
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
class UserScreenViewModelTest {
    @get: Rule
    val coroutineRule = TestCoroutineRule()

    @Mock
    lateinit var repositoryImp: AuthRepositoryImp

    lateinit var userScreenViewModel: UserScreenViewModel

    private val email = "juan132y@gmail.com"

    private val exceptionError = "An error has occurred"
    private val error = "Error: $exceptionError"

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        userScreenViewModel = UserScreenViewModel(repositoryImp)
    }

    @Test
    fun `getUserByEmail should emit Success state if UserResult_Success is received`() = runTest {
        //Given
        Mockito.`when`(repositoryImp.getUserByEmail(email))
            .thenReturn(UserMotherObject.userResultSuccess)

        //When
        userScreenViewModel.getUserByEmail(email)

        //Then
        userScreenViewModel.state.test {
            assertTrue(awaitItem() is UserScreenState.Loading)
            advanceUntilIdle()
            val state = awaitItem()
            assertTrue(state is UserScreenState.Success)
            val successState = state as UserScreenState.Success
            assertEquals(successState.user, UserMotherObject.createUserResult.user)
        }
    }

    @Test
    fun `getUserByEmail should emit Error state if UserResult_Error is received`() = runTest {
        //Given
        Mockito.`when`(repositoryImp.getUserByEmail(email))
            .thenReturn(UserMotherObject.userResultError)

        //When
        userScreenViewModel.getUserByEmail(email)

        //Then
        userScreenViewModel.state.test {
            assertTrue(awaitItem() is UserScreenState.Loading)
            advanceUntilIdle()
            val state = awaitItem()
            assertTrue(state is UserScreenState.Error)
            val errorState = state as UserScreenState.Error
            assertEquals(errorState.error, error)
        }
    }

    @Test
    fun `getUserByEmail should emit Error state if an exception occurs`() = runTest {
        //Given
        Mockito.`when`(repositoryImp.getUserByEmail(email))
            .thenThrow(RuntimeException(exceptionError))

        //When
        userScreenViewModel.getUserByEmail(email)

        //Then
        userScreenViewModel.state.test {
            assertTrue(awaitItem() is UserScreenState.Loading)
            advanceUntilIdle()
            val state = awaitItem()
            assertTrue(state is UserScreenState.Error)
            val errorState = state as UserScreenState.Error
            assertEquals(errorState.error, error)
        }
    }

    @Test
    fun `logOutUser should emit Initial state if ResetResult_Success is received`() = runTest {
        //Given
        Mockito.`when`(repositoryImp.logOutUser())
            .thenReturn(UserMotherObject.resetResultSuccess)

        //When
        userScreenViewModel.logOutUser()

        //Then
        userScreenViewModel.state.test {
            assertTrue(awaitItem() is UserScreenState.Loading)
            advanceUntilIdle()
            val state = awaitItem()
            assertTrue(state is UserScreenState.Initial)
        }
    }

    @Test
    fun `logOutUser should emit Error state if ResetResult_Error is received`() = runTest {
        //Given
        Mockito.`when`(repositoryImp.logOutUser())
            .thenReturn(UserMotherObject.resetResultError)

        //When
        userScreenViewModel.logOutUser()

        //Then
        userScreenViewModel.state.test {
            assertTrue(awaitItem() is UserScreenState.Loading)
            advanceUntilIdle()
            val state = awaitItem()
            assertTrue(state is UserScreenState.Error)
            val errorState = state as UserScreenState.Error
            assertEquals(errorState.error, error)
        }
    }

    @Test
    fun `logOutUser should emit Error state if an exception occurs`() = runTest {
        //Given
        Mockito.`when`(repositoryImp.logOutUser())
            .thenThrow(RuntimeException(exceptionError))

        //When
        userScreenViewModel.logOutUser()

        //Then
        userScreenViewModel.state.test {
            assertTrue(awaitItem() is UserScreenState.Loading)
            advanceUntilIdle()
            val state = awaitItem()
            assertTrue(state is UserScreenState.Error)
            val errorState = state as UserScreenState.Error
            assertEquals(errorState.error, error)
        }
    }
}