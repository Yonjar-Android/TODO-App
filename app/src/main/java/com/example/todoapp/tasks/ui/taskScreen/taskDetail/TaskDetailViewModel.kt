package com.example.todoapp.tasks.ui.taskScreen.taskDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.tasks.data.repositories.taskRepository.CategoryResult
import com.example.todoapp.tasks.data.repositories.taskRepository.TaskDetailResult
import com.example.todoapp.tasks.data.repositories.taskRepository.TaskResult
import com.example.todoapp.tasks.data.repositories.taskRepository.TasksRepositoryImp
import com.example.todoapp.tasks.domain.models.Category
import com.example.todoapp.tasks.domain.models.TaskDom
import com.google.firebase.firestore.DocumentReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(private val repositoryImp: TasksRepositoryImp) :
    ViewModel() {

    private val _state = MutableStateFlow<TaskDetailState>(TaskDetailState.Initial)
    var state: StateFlow<TaskDetailState> = _state

    private val _categories = MutableStateFlow<List<Category>>(mutableListOf())
    var categories = _categories

    private val _task = MutableStateFlow<TaskDom?>(null)
    var task = _task

    var category = ""

    init {
        getCategories()
    }

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
                                _state.value = TaskDetailState.Success("Se ha actualizado la tarea con éxito")

                            }
                        }
                    }
                }

            } catch (e: Exception) {
                _state.value = TaskDetailState.Error("Error: ${e.message}")
            }
        }
    }

    private fun getCategories() {
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
}