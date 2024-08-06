package com.example.todoapp.tasks.ui.taskScreen.taskDetail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.R
import com.example.todoapp.tasks.data.repositories.authRepository.AuthRepositoryImp
import com.example.todoapp.tasks.data.repositories.authRepository.UsersResult
import com.example.todoapp.tasks.data.repositories.taskRepository.CategoryResult
import com.example.todoapp.tasks.data.repositories.taskRepository.TaskDetailResult
import com.example.todoapp.tasks.data.repositories.taskRepository.TaskResult
import com.example.todoapp.tasks.data.repositories.taskRepository.TasksRepositoryImp
import com.example.todoapp.tasks.domain.models.Category
import com.example.todoapp.tasks.domain.models.TaskDom
import com.example.todoapp.tasks.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val repositoryImp: TasksRepositoryImp,
    private val resourceProvider: ResourceProvider,
    private val authRepositoryImp: AuthRepositoryImp
    ) :
    ViewModel() {

    private val _state = MutableStateFlow<TaskDetailState>(TaskDetailState.Loading)
    var state: StateFlow<TaskDetailState> = _state

    private val _categories = MutableStateFlow<List<Category>>(mutableListOf())
    var categories = _categories

    private val _task = MutableStateFlow<TaskDom?>(null)
    var task = _task

    private val _userEmails = MutableStateFlow<List<String>>(mutableListOf())
    var userEmails = _userEmails

    var category = ""



    fun getTaskById(taskId: String) {

        _state.value = TaskDetailState.Loading

        viewModelScope.launch {
            try {
                val response = repositoryImp.getTaskById(taskId)

                when (response) {
                    is TaskDetailResult.Error -> {
                        _state.value = TaskDetailState.Error("Error: ${response.error}")
                    }

                    is TaskDetailResult.Success -> {
                        _state.value = TaskDetailState.Success("")
                        _task.value = response.task
                        category = response.category
                    }
                }
            } catch (e: Exception) {
                _state.value = TaskDetailState.Error("Error: ${e.message}")
            }
        }
    }

     @RequiresApi(Build.VERSION_CODES.O)
     fun updateTask(
        taskId: String,
        name: String,
        description: String?,
        date: String,
        check: Boolean = false,
        deliverablesDescription: String?,
        deliverables: List<String> = listOf(),
        users: List<String> = listOf(),
        category: String
    ) {
        _state.value = TaskDetailState.Loading

        viewModelScope.launch {
            try {
                if (!validations(name, date, category)) return@launch

                val categoryReference = repositoryImp.getCategoryReference(category)

                when(categoryReference){
                    is TaskResult.Error -> {
                        _state.value = TaskDetailState.Error("Error: ${categoryReference.error}")

                    }
                    is TaskResult.Success -> {
                        val response = repositoryImp.updateTask(
                            taskId = taskId,
                            name = name,
                            description = description,
                            deliverables = deliverables,
                            deliverablesDescription = deliverablesDescription,
                            date = date,
                            check = check,
                            users = users,
                            category = categoryReference.documentReference!!
                        )

                        when(response){
                            is TaskResult.Error -> {
                                _state.value = TaskDetailState.Error("Error: ${response.error}")

                            }
                            is TaskResult.Success -> {
                                _state.value = TaskDetailState.Success(response.message)

                            }
                        }
                    }
                }

            } catch (e: Exception) {
                _state.value = TaskDetailState.Error("Error: ${e.message}")
            }
        }
    }

     fun getCategories() {
        viewModelScope.launch {
            try {
                when (val response = repositoryImp.getAllCategories()) {
                    is CategoryResult.Error -> {
                        _state.value = TaskDetailState.Error("Error: ${response.error}")
                    }

                    is CategoryResult.Success -> {
                        _categories.value = response.categories!!
                        _state.value = TaskDetailState.Success("")
                    }
                }
            } catch (e: Exception) {
                _state.value = TaskDetailState.Error("Error: ${e.message}")
            }
        }
    }

    fun getEmails(){
        viewModelScope.launch {
            try {
                when (val response = authRepositoryImp.getUsers()) {
                    is UsersResult.Error -> {
                        _state.value = TaskDetailState.Error("Error: ${response.error}")
                    }
                    is UsersResult.Success -> {
                        _userEmails.value = response.listUsers
                    }
                }
            } catch (e: Exception) {
                _state.value = TaskDetailState.Error("Error: ${e.message}")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun validations(name: String, date: String, category: String): Boolean {
        if (name.isBlank()) {
            _state.value = TaskDetailState.Error(resourceProvider.getString(R.string.name_empty))
            return false
        }
        if (category.isBlank()) {
            _state.value = TaskDetailState.Error(resourceProvider.getString(R.string.category_empty))
            return false
        }
        if (date.isBlank()) {
            _state.value = TaskDetailState.Error(resourceProvider.getString(R.string.date_empty))
            return false
        }

        // Date Validation
        val formatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy") // Match your date formatval taskDate = LocalDate.parse(date, formatter)
        val today = LocalDate.now()
        val dateSelected = LocalDate.parse(date, formatter)

        if (dateSelected.isBefore(today)) {
            _state.value = TaskDetailState.Error(resourceProvider.getString(R.string.date_past))
            return false
        }

        return true
    }

    fun resetState(){
        _state.value = TaskDetailState.Initial
    }
}