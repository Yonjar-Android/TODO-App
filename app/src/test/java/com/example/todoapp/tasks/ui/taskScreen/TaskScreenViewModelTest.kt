package com.example.todoapp.tasks.ui.taskScreen

import app.cash.turbine.test
import com.example.todoapp.TestCoroutineRule
import com.example.todoapp.motherObject.TaskMotherObject
import com.example.todoapp.tasks.data.repositories.taskRepository.TaskResult
import com.example.todoapp.tasks.data.repositories.taskRepository.TasksRepositoryImp
import com.example.todoapp.tasks.domain.models.TaskDom
import com.example.todoapp.tasks.ui.taskScreen.tasks.TaskScreenState
import com.example.todoapp.tasks.ui.taskScreen.tasks.TaskScreenViewModel
import com.google.firebase.firestore.DocumentReference
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalCoroutinesApi::class)
class TaskScreenViewModelTest {
    @get:Rule
    val testCoroutine = TestCoroutineRule()

    @Mock
    lateinit var repositoryImp: TasksRepositoryImp

    private lateinit var viewModel: TaskScreenViewModel

    private val category: String = "documents"

    @Mock
    lateinit var categoryMock: DocumentReference


    //variables
    private val taskId = "abc123"
    private val taskName = "Create the database"
    private val date: String = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

    private val error = "Error: An error has occurred"
    private val errorException = "An error has occurred"


    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        viewModel = TaskScreenViewModel(repositoryImp)
    }

    @Test
    fun `getCategories should emit a Success state is a CategoryResult_Success is received`() =
        runTest {
            //Given
            Mockito.`when`(repositoryImp.getAllCategories())
                .thenReturn(TaskMotherObject.categoryResult)

            //When
            viewModel.getCategories()

            //Then
            viewModel.state.test {
                assertTrue(awaitItem() is TaskScreenState.Initial)
                advanceUntilIdle()
                val state = awaitItem()
                assertTrue(state is TaskScreenState.Success)
                val successState = state as TaskScreenState.Success
                assertEquals(successState.message, "")
            }
        }

    @Test
    fun `getCategories should emit Error state if a CategoryResult_Error is received`() = runTest {
        //Given
        Mockito.`when`(repositoryImp.getAllCategories())
            .thenReturn(TaskMotherObject.categoryResultError)

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
        Mockito.`when`(repositoryImp.getAllCategories()).thenThrow(RuntimeException(errorException))

        //When
        viewModel.getCategories()

        //Then
        viewModel.state.test {
            assertTrue(awaitItem() is TaskScreenState.Initial)
            advanceUntilIdle()
            val state = awaitItem()
            assertTrue(state is TaskScreenState.Error)
            val errorState = state as TaskScreenState.Error
            assertEquals(errorState.error, error)
        }
    }

    @Test
    fun `createTask should emit Success state on successful task creation if TaskResult_Success is returned`() =
        runTest {
            val taskResultSuccess = TaskResult.Success("", categoryMock)

            //Given
            Mockito.`when`(repositoryImp.getCategoryReference(category))
                .thenReturn(taskResultSuccess)

            Mockito.`when`(
                repositoryImp.createTask(
                    name = taskName,
                    description = "",
                    deliverables = listOf(),
                    deliverablesDescription = "",
                    users = listOf(),
                    category = categoryMock,
                    date = date
                )
            ).thenReturn(TaskMotherObject.TaskResultSuccess)

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
        Mockito.`when`(repositoryImp.getCategoryReference(category))
            .thenReturn(TaskMotherObject.TaskResultError)

        Mockito.`when`(
            repositoryImp.createTask(
                name = taskName,
                description = "",
                deliverables = listOf(),
                deliverablesDescription = "",
                users = listOf(),
                category = categoryMock,
                date = date
            )
        ).thenReturn(TaskMotherObject.TaskResultSuccess)

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
            assertEquals(errorState.error, error)
        }
    }

    @Test
    fun `createTask should emit Error state if a TaskResult_Error is returned`() = runTest {
        val taskResultSuccess = TaskResult.Success("", categoryMock)

        //Given
        Mockito.`when`(repositoryImp.getCategoryReference(category)).thenReturn(taskResultSuccess)

        Mockito.`when`(
            repositoryImp.createTask(
                name = taskName,
                description = "",
                deliverables = listOf(),
                deliverablesDescription = "",
                users = listOf(),
                category = categoryMock,
                date = date
            )
        ).thenReturn(TaskMotherObject.TaskResultError)

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

        Mockito.`when`(
            repositoryImp.createTask(
                name = taskName,
                description = "",
                deliverables = listOf(),
                deliverablesDescription = "",
                users = listOf(),
                category = categoryMock,
                date = date
            )
        ).thenThrow(RuntimeException(errorException))

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
            assertEquals(errorState.error, error)
        }
    }

    @Test
    fun `getAllTasks should emit Success state if TaskResult_Success is returned`() = runTest {
        val tasksList = listOf(
            TaskDom(
                taskId = "123456789",
                name = "Crear base de datos",
                description = "",
                deliverablesDesc = "",
                deliverables = listOf(),
                users = listOf(),
                check = false,
                category = categoryMock,
                date = date
            )
        )

        val taskResult = TaskResult.Success(tasks = tasksList, message = "")

        //Given
        Mockito.`when`(repositoryImp.getAllTasks()).thenReturn(taskResult)

        //When
        viewModel.getAllTasks()

        //Then
        viewModel.state.test {
            assertTrue(awaitItem() is TaskScreenState.Initial)
            advanceUntilIdle()
            val state = awaitItem()
            assertTrue(state is TaskScreenState.Success)
            val successState = state as TaskScreenState.Success
            assertEquals(successState.message, taskResult.message)
        }
    }

    @Test
    fun `getAllTasks should emit Error state if TaskResult_Error is returned`() = runTest {
        //Given
        Mockito.`when`(repositoryImp.getAllTasks()).thenReturn(TaskMotherObject.TaskResultError)

        //When
        viewModel.getAllTasks()

        //Then
        viewModel.state.test {
            assertTrue(awaitItem() is TaskScreenState.Initial)
            advanceUntilIdle()
            val state = awaitItem()
            assertTrue(state is TaskScreenState.Error)
            val errorState = state as TaskScreenState.Error
            assertEquals(errorState.error, error)
        }
    }


    @Test
    fun `getAllTasks should emit Error state if an exception occurs`() = runTest {
        //Given
        Mockito.`when`(repositoryImp.getAllTasks()).thenThrow(RuntimeException(errorException))

        //When
        viewModel.getAllTasks()

        //Then
        viewModel.state.test {
            assertTrue(awaitItem() is TaskScreenState.Initial)
            advanceUntilIdle()
            val state = awaitItem()
            assertTrue(state is TaskScreenState.Error)
            val errorState = state as TaskScreenState.Error
            assertEquals(errorState.error, error)
        }
    }

    @Test
    fun `onCheckedChange should emit Error state if TaskResult_Error is returned`() = runTest {
        //Given
        Mockito.`when`(repositoryImp.onCheckChange(taskId, true))
            .thenReturn(TaskMotherObject.TaskResultError)

        //When
        viewModel.onCheckedChange(taskId, true)

        //Then
        viewModel.state.test {
            assertTrue(awaitItem() is TaskScreenState.Initial)
            advanceUntilIdle()
            val state = awaitItem()
            assertTrue(state is TaskScreenState.Error)
            val errorState = state as TaskScreenState.Error
            assertEquals(errorState.error, error)
        }
    }

    @Test
    fun `onCheckedChange should emit Error state if an exception occurs`() = runTest {
        //Given
        Mockito.`when`(repositoryImp.onCheckChange(taskId, true))
            .thenThrow(RuntimeException(errorException))

        //When
        viewModel.onCheckedChange(taskId, true)

        //Then
        viewModel.state.test {
            assertTrue(awaitItem() is TaskScreenState.Initial)
            advanceUntilIdle()
            val state = awaitItem()
            assertTrue(state is TaskScreenState.Error)
            val errorState = state as TaskScreenState.Error
            assertEquals(errorState.error, error)
        }
    }

    @Test
    fun `deleteTask should emit Success state if TaskResult_Success is received`() = runTest {
        //Given
        Mockito.`when`(repositoryImp.deleteTask(taskId))
            .thenReturn(TaskMotherObject.TaskResultSuccess)

        //When
        viewModel.deleteTask(taskId)

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
    fun `deleteTask should emit Error state if TaskResult_Error is received`() = runTest {
        //Given
        Mockito.`when`(repositoryImp.deleteTask(taskId))
            .thenReturn(TaskMotherObject.TaskResultError)

        //When
        viewModel.deleteTask(taskId)

        //Then
        //Then
        viewModel.state.test {
            assertTrue(awaitItem() is TaskScreenState.Loading)
            advanceUntilIdle()
            val state = awaitItem()
            assertTrue(state is TaskScreenState.Error)
            val errorState = state as TaskScreenState.Error
            assertEquals(errorState.error, error)
        }
    }

    @Test
    fun `deleteTask should emit Error state if an Exception occurs`() = runTest {
        //Given
        Mockito.`when`(repositoryImp.deleteTask(taskId))
            .thenThrow(RuntimeException(errorException))

        //When
        viewModel.deleteTask(taskId)

        //Then
        //Then
        viewModel.state.test {
            assertTrue(awaitItem() is TaskScreenState.Loading)
            advanceUntilIdle()
            val state = awaitItem()
            assertTrue(state is TaskScreenState.Error)
            val errorState = state as TaskScreenState.Error
            assertEquals(errorState.error, error)
        }
    }
}