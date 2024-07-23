package com.example.todoapp.tasks.ui.taskScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.tasks.data.repositories.taskRepository.CategoryResult
import com.example.todoapp.tasks.data.repositories.taskRepository.TaskResult
import com.example.todoapp.tasks.data.repositories.taskRepository.TasksRepositoryImp
import com.example.todoapp.tasks.domain.models.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskScreenViewModel @Inject constructor(
    private val repositoryImp: TasksRepositoryImp
) : ViewModel() {
    private val _state = MutableStateFlow<TaskScreenState>(TaskScreenState.Initial)
    var state: StateFlow<TaskScreenState> = _state

    private var _showToast = MutableStateFlow(false)
    val showToast: StateFlow<Boolean> = _showToast

    private var categories: List<Category>? = listOf()

    fun getCategories() {
        viewModelScope.launch {
            try {
                when (val response = repositoryImp.getAllCategories()) {
                    is CategoryResult.Error -> {
                        _state.value = TaskScreenState.Error("Error: ${response.error}")
                    }

                    is CategoryResult.Success -> {
                        categories = response.categories
                        _state.value = TaskScreenState.Success("", response.categories)
                        _showToast.value = true
                    }
                }
            } catch (e: Exception) {
                _state.value = TaskScreenState.Error("Error: ${e.message}")
            }
        }
    }

    fun createTask(
        name: String,
        description: String?,
        date: String,
        check: Boolean = false,
        deliverablesDescription: String?,
        deliverables: List<String> = listOf(),
        users: List<String> = listOf()
    ) {
        viewModelScope.launch {
            try {
                val response =
                    repositoryImp.createTask(
                        name = name,
                        description = description,
                        date = date,
                        check = check,
                        deliverables = deliverables,
                        deliverablesDescription = deliverablesDescription,
                        users = users
                    )

                when(response){
                    is TaskResult.Error -> {
                        _state.value = TaskScreenState.Error("Error: ${response.error}")
                    }
                    is TaskResult.Success -> {
                        _state.value = TaskScreenState.Success(response.message, categories)
                        _showToast.value = true
                    }
                }
            } catch (e: Exception) {
                _state.value = TaskScreenState.Error("Error: ${e.message}")
            }
        }
    }

    fun resetShowToast() {
        _showToast.value = false // Reset flag to hide Toast
    }
}