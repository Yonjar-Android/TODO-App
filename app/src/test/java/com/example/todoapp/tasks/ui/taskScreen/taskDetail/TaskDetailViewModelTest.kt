package com.example.todoapp.tasks.ui.taskScreen.taskDetail

import app.cash.turbine.test
import com.example.todoapp.TestCoroutineRule
import com.example.todoapp.motherObject.TaskMotherObject
import com.example.todoapp.tasks.data.repositories.taskRepository.TaskDetailResult
import com.example.todoapp.tasks.data.repositories.taskRepository.TaskResult
import com.example.todoapp.tasks.data.repositories.taskRepository.TasksRepositoryImp
import com.example.todoapp.tasks.domain.models.TaskDom
import com.example.todoapp.tasks.utils.ResourceProvider
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
class TaskDetailViewModelTest {
    @get: Rule
    val coroutineRule = TestCoroutineRule()

    @Mock
    lateinit var repositoryImp: TasksRepositoryImp

    @Mock
    lateinit var resourceProvider: ResourceProvider

    private lateinit var taskDetailViewModel: TaskDetailViewModel

    @Mock
    lateinit var categoryMock: DocumentReference

    //variables
    private val taskId = "myTaskId123"
    private val taskName = "Create the database"
    private val date: String = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    private val category: String = "documents"

    private lateinit var taskDetailResultSuccess: TaskDetailResult
    private lateinit var taskDetailResultError: TaskDetailResult.Error

    private val errorException = "An error has occurred"
    private val error = "Error: An error has occurred"


    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        taskDetailViewModel = TaskDetailViewModel(repositoryImp,resourceProvider)


        val task = TaskDom(
            taskId = taskId,
            name = taskName,
            description = "",
            deliverablesDesc = "",
            deliverables = listOf(),
            users = listOf(),
            check = false,
            category = categoryMock,
            date = date
        )

        taskDetailResultSuccess = TaskDetailResult.Success(
            task = task,
            category = category
        )

        taskDetailResultError = TaskDetailResult.Error(errorException)

    }

    @Test
    fun `getTaskById should emit Success state if TaskDetailResult_Success is received`() =
        runTest {
            //Given
            Mockito.`when`(repositoryImp.getTaskById(taskId)).thenReturn(taskDetailResultSuccess)

            //When
            taskDetailViewModel.getTaskById(taskId)

            //Then
            taskDetailViewModel.state.test {
                assertTrue(awaitItem() is TaskDetailState.Loading)
                advanceUntilIdle()
                val state = awaitItem()
                assertTrue(state is TaskDetailState.Success)
            }
        }

    @Test
    fun `getTaskById should emit Error state if TaskDetailResult_Error is received`() = runTest {
        //Given
        Mockito.`when`(repositoryImp.getTaskById(taskId)).thenReturn(taskDetailResultError)

        //When
        taskDetailViewModel.getTaskById(taskId)

        //Then
        taskDetailViewModel.state.test {
            assertTrue(awaitItem() is TaskDetailState.Loading)
            advanceUntilIdle()
            val state = awaitItem()
            assertTrue(state is TaskDetailState.Error)
            assertEquals(
                (state as TaskDetailState.Error).error,
                error
            )
        }
    }

    @Test
    fun `getTaskById should emit Error state if an Exception occurs`() = runTest {
        //Given
        Mockito.`when`(repositoryImp.getTaskById(taskId)).thenThrow(RuntimeException(errorException))

        //When
        taskDetailViewModel.getTaskById(taskId)

        //Then
        taskDetailViewModel.state.test {
            assertTrue(awaitItem() is TaskDetailState.Loading)
            advanceUntilIdle()
            val state = awaitItem()
            assertTrue(state is TaskDetailState.Error)
            assertEquals((state as TaskDetailState.Error).error, error)
        }
    }

    @Test
    fun `updateTask should Emit Success state if TaskResult_Success is returned in both functions`() = runTest {

        val taskResultSuccessT = TaskResult.Success("", categoryMock)

        //Given
        Mockito.`when`(repositoryImp.getCategoryReference(category))
            .thenReturn(taskResultSuccessT)

        Mockito.`when`(
            repositoryImp.updateTask(
                taskId = taskId,
                name = taskName,
                description = "",
                deliverables = listOf(),
                deliverablesDescription = "",
                users = listOf(),
                category = categoryMock,
                date = date
            )).thenReturn(TaskMotherObject.TaskResultSuccess)

        //When
        taskDetailViewModel.updateTask(
            taskId = taskId,
            name = taskName,
            description = "",
            deliverables = listOf(),
            deliverablesDescription = "",
            users = listOf(),
            category = category,
            date = date
        )

        taskDetailViewModel.state.test {
            assertTrue(awaitItem() is TaskDetailState.Loading)
            advanceUntilIdle()
            val state = awaitItem()
            assertTrue(state is TaskDetailState.Success)
        }
    }

    @Test
    fun `updateTask should emit Error state if TaskResult_Error is received after getCategoryReference is called`() =
        runTest {
            //Given
            Mockito.`when`(repositoryImp.getCategoryReference(category))
                .thenReturn(TaskMotherObject.TaskResultError)

            //When
            taskDetailViewModel.updateTask(
                taskId = taskId,
                name = taskName,
                description = "",
                deliverables = listOf(),
                deliverablesDescription = "",
                users = listOf(),
                category = category,
                date = date
            )

            taskDetailViewModel.state.test {
                assertTrue(awaitItem() is TaskDetailState.Loading)
                advanceUntilIdle()
                val state = awaitItem()
                assertTrue(state is TaskDetailState.Error)
                val errorState = state as TaskDetailState.Error
                assertEquals(errorState.error, error)
            }
        }

    @Test
    fun `updateTask should emit Error state if TaskResult_Error is received after updateTask is called`() =
        runTest {
            val taskResultSuccessT = TaskResult.Success("", categoryMock)

            //Given
            Mockito.`when`(repositoryImp.getCategoryReference(category))
                .thenReturn(taskResultSuccessT)
            Mockito.`when`(
                repositoryImp.updateTask(
                    taskId = taskId,
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
            taskDetailViewModel.updateTask(
                taskId = taskId,
                name = taskName,
                description = "",
                deliverables = listOf(),
                deliverablesDescription = "",
                users = listOf(),
                category = category,
                date = date
            )

            taskDetailViewModel.state.test {
                assertTrue(awaitItem() is TaskDetailState.Loading)
                advanceUntilIdle()
                val state = awaitItem()
                assertTrue(state is TaskDetailState.Error)
                val errorState = state as TaskDetailState.Error
                assertEquals(errorState.error, error)
            }
        }

    @Test
    fun`updateTask should emit Error state if an Exception occurs`() = runTest {
        //Given
        Mockito.`when`(repositoryImp.getCategoryReference(category))
            .thenThrow(RuntimeException(errorException))

        Mockito.`when`(
            repositoryImp.updateTask(
                taskId = taskId,
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
        taskDetailViewModel.updateTask(
            taskId = taskId,
            name = taskName,
            description = "",
            deliverables = listOf(),
            deliverablesDescription = "",
            users = listOf(),
            category = category,
            date = date
        )

        taskDetailViewModel.state.test {
            assertTrue(awaitItem() is TaskDetailState.Loading)
            advanceUntilIdle()
            val state = awaitItem()
            assertTrue(state is TaskDetailState.Error)
            val errorState = state as TaskDetailState.Error
            assertEquals(errorState.error, error)
        }
    }

    @Test
    fun `getCategories should emit a Success state is a CategoryResult_Success is received`() =
        runTest {
            //Given
            Mockito.`when`(repositoryImp.getAllCategories())
                .thenReturn(TaskMotherObject.categoryResult)

            //When
            taskDetailViewModel.getCategories()

            //Then
            taskDetailViewModel.state.test {
                assertTrue(awaitItem() is TaskDetailState.Loading)
                advanceUntilIdle()
                val state = awaitItem()
                assertTrue(state is TaskDetailState.Success)
                val successState = state as TaskDetailState.Success
                assertEquals(successState.message, "")
            }
        }

    @Test
    fun `getCategories should emit Error state if a CategoryResult_Error is received`() = runTest {
        //Given
        Mockito.`when`(repositoryImp.getAllCategories())
            .thenReturn(TaskMotherObject.categoryResultError)

        //When
        taskDetailViewModel.getCategories()

        //Then
        taskDetailViewModel.state.test {
            assertTrue(awaitItem() is TaskDetailState.Loading)
            advanceUntilIdle()
            val state = awaitItem()
            assertTrue(state is TaskDetailState.Error)
            val errorState = state as TaskDetailState.Error
            assertEquals(errorState.error, error)
        }
    }

    @Test
    fun `getCategories should emit Error state if an exception occurs`() = runTest {
        //Given
        Mockito.`when`(repositoryImp.getAllCategories()).thenThrow(RuntimeException(errorException))

        //When
        taskDetailViewModel.getCategories()

        //Then
        taskDetailViewModel.state.test {
            assertTrue(awaitItem() is TaskDetailState.Loading)
            advanceUntilIdle()
            val state = awaitItem()
            assertTrue(state is TaskDetailState.Error)
            val errorState = state as TaskDetailState.Error
            assertEquals(errorState.error, error)
        }
    }

}
