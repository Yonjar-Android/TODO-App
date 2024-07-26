package com.example.todoapp.tasks.ui.taskScreen

import app.cash.turbine.test
import com.example.todoapp.TestCoroutineRule
import com.example.todoapp.motherObject.TaskMotherObject
import com.example.todoapp.tasks.data.repositories.taskRepository.TaskResult
import com.example.todoapp.tasks.data.repositories.taskRepository.TasksRepositoryImp
import com.example.todoapp.tasks.ui.taskScreen.tasks.TaskScreenState
import com.example.todoapp.tasks.ui.taskScreen.tasks.TaskScreenViewModel
import com.google.firebase.firestore.DocumentReference
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
class TaskScreenViewModelTest{
    @get:Rule
    val testCoroutine = TestCoroutineRule()

    @Mock
    lateinit var repositoryImp: TasksRepositoryImp

    private lateinit var viewModel: TaskScreenViewModel

    private val category:String = "documents"

    @Mock
    lateinit var categoryMock:DocumentReference



    //variables
    private val taskName = "Create the database"
    private val date = "25/07/2024"

    private val error = "An error has occurred"


    @Before
    fun setUp(){
        MockitoAnnotations.openMocks(this)
        viewModel = TaskScreenViewModel(repositoryImp)
    }

    @Test
    fun `getCategories should emit a Success state is a CategoryResult_Success is received`() = runTest {
        //Given
        Mockito.`when`(repositoryImp.getAllCategories()).thenReturn(TaskMotherObject.categoryResult)

        //When
        viewModel.getCategories()

        //Then
        viewModel.state.test {
            assertTrue(awaitItem() is TaskScreenState.Initial)
            advanceUntilIdle()
            val state = awaitItem()
            assertTrue(state is TaskScreenState.Success)
            val successState = state as TaskScreenState.Success
            assertEquals(successState.categories, TaskMotherObject.categoryResult.categories)
        }
    }

    @Test
    fun `getCategories should emit Error state if a CategoryResult_Error is received`() = runTest {
        //Given
        Mockito.`when`(repositoryImp.getAllCategories()).thenReturn(TaskMotherObject.categoryResultError)

        //When
        viewModel.getCategories()

        //Then
        viewModel.state.test {
            assertTrue(awaitItem() is TaskScreenState.Initial)
            advanceUntilIdle()
            val state = awaitItem()
            assertTrue(state is TaskScreenState.Error)
            val errorState = state as TaskScreenState.Error
            assertEquals(errorState.error, "Error: ${TaskMotherObject.categoryResultError.error}")
        }
    }

    @Test
    fun `getCategories should emit Error state if an exception occurs`() = runTest {
        //Given
        Mockito.`when`(repositoryImp.getAllCategories()).thenThrow(RuntimeException(error))

        //When
        viewModel.getCategories()

        //Then
        viewModel.state.test {
            assertTrue(awaitItem() is TaskScreenState.Initial)
            advanceUntilIdle()
            val state = awaitItem()
            assertTrue(state is TaskScreenState.Error)
            val errorState = state as TaskScreenState.Error
            assertEquals(errorState.error, "Error: $error")
        }
    }

    @Test
    fun `createTask should emit Success state on successful task creation if TaskResult_Success is returned`() = runTest {
         val taskResultSuccess = TaskResult.Success("", categoryMock)

        //Given
        Mockito.`when`(repositoryImp.getCategoryReference(category)).thenReturn(taskResultSuccess)

        Mockito.`when`(repositoryImp.createTask(
            name = taskName,
            description = "",
            deliverables = listOf(),
            deliverablesDescription = "",
            users = listOf(),
            category = categoryMock,
            date = date
        )).thenReturn(TaskMotherObject.TaskResultSuccess)

        //When
        viewModel.createTask(
            name = taskName,
            description = "",
            deliverables = listOf(),
            deliverablesDescription = "",
            users = listOf(),
            category = category,
            date = date
        )

        //Then
        viewModel.state.test {
            assertTrue(awaitItem() is TaskScreenState.Loading)
            advanceUntilIdle()
            val state = awaitItem()
            assertTrue(state is TaskScreenState.Success)
            val successState = state as TaskScreenState.Success
            assertEquals(successState.message, TaskMotherObject.TaskResultSuccess.message)
        }
    }

    @Test
    fun `createTask should emit Error state if CategoryResult_Error is returned`() = runTest {
        //Given
        Mockito.`when`(repositoryImp.getCategoryReference(category)).thenReturn(TaskMotherObject.TaskResultError)

        Mockito.`when`(repositoryImp.createTask(
            name = taskName,
            description = "",
            deliverables = listOf(),
            deliverablesDescription = "",
            users = listOf(),
            category = categoryMock,
            date = date
        )).thenReturn(TaskMotherObject.TaskResultSuccess)

        //When
        viewModel.createTask(
            name = taskName,
            description = "",
            deliverables = listOf(),
            deliverablesDescription = "",
            users = listOf(),
            category = category,
            date = date
        )
        //When

        //Then
        viewModel.state.test {
            assertTrue(awaitItem() is TaskScreenState.Loading)
            advanceUntilIdle()
            val state = awaitItem()
            assertTrue(state is TaskScreenState.Error)
            val errorState = state as TaskScreenState.Error
            assertEquals(errorState.error, "Error: $error")
        }
    }

    @Test
    fun `createTask should emit Error state if a TaskResult_Error is returned`() = runTest{
         val taskResultSuccess = TaskResult.Success("", categoryMock)

        //Given
        Mockito.`when`(repositoryImp.getCategoryReference(category)).thenReturn(taskResultSuccess)

        Mockito.`when`(repositoryImp.createTask(
            name = taskName,
            description = "",
            deliverables = listOf(),
            deliverablesDescription = "",
            users = listOf(),
            category = categoryMock,
            date = date
        )).thenReturn(TaskMotherObject.TaskResultError)

        //When
        viewModel.createTask(
            name = taskName,
            description = "",
            deliverables = listOf(),
            deliverablesDescription = "",
            users = listOf(),
            category = category,
            date = date
        )

        //Then
        viewModel.state.test {
            assertTrue(awaitItem() is TaskScreenState.Loading)
            advanceUntilIdle()
            val state = awaitItem()
            assertTrue(state is TaskScreenState.Error)
            val errorState = state as TaskScreenState.Error
            assertEquals(errorState.error, "Error: ${TaskMotherObject.TaskResultError.error}")
        }
    }

    @Test
    fun `createTask should emit Error state if an exception occurs`() = runTest {
        val taskResultSuccess = TaskResult.Success("", categoryMock)

        //Given
        Mockito.`when`(repositoryImp.getCategoryReference(category)).thenReturn(taskResultSuccess)

        Mockito.`when`(repositoryImp.createTask(
            name = taskName,
            description = "",
            deliverables = listOf(),
            deliverablesDescription = "",
            users = listOf(),
            category = categoryMock,
            date = date
        )).thenThrow(RuntimeException(error))

        //When
        viewModel.createTask(
            name = taskName,
            description = "",
            deliverables = listOf(),
            deliverablesDescription = "",
            users = listOf(),
            category = category,
            date = date
        )

        //Then
        viewModel.state.test {
            assertTrue(awaitItem() is TaskScreenState.Loading)
            advanceUntilIdle()
            val state = awaitItem()
            assertTrue(state is TaskScreenState.Error)
            val errorState = state as TaskScreenState.Error
            assertEquals(errorState.error, "Error: $error")
        }
    }

}