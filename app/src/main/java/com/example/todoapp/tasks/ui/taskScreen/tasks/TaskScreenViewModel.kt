package com.example.todoapp.tasks.ui.taskScreen.tasks

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.R
import com.example.todoapp.tasks.data.repositories.taskRepository.CategoryResult
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

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class TaskScreenViewModel @Inject constructor(
    private val repositoryImp: TasksRepositoryImp,
    private val resourceProvider: ResourceProvider
) : ViewModel() {
    private val _state = MutableStateFlow<TaskScreenState>(TaskScreenState.Initial)
    var state: StateFlow<TaskScreenState> = _state

    private var _showToast = MutableStateFlow(false)
    val showToast: StateFlow<Boolean> = _showToast

    private val _resetFields = MutableStateFlow(false)
    val resetFields: StateFlow<Boolean> = _resetFields

    var categories: List<Category>? = listOf()
    var tasks: List<TaskDom>? = listOf()

    init {
        getCategories()
        getAllTasks()
    }

    fun getCategories() {
        viewModelScope.launch {
            try {
                when (val response = repositoryImp.getAllCategories()) {
                    is CategoryResult.Error -> {
                        _state.value = TaskScreenState.Error("Error: ${response.error}")
                    }

                    is CategoryResult.Success -> {
                        categories = response.categories
                        _state.value = TaskScreenState.Success("")
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
        users: List<String> = listOf(),
        category: String,
        creationDate:String
    ) {
        _state.value = TaskScreenState.Loading

        viewModelScope.launch {
            try {
                if (!validations(name, date, category)) return@launch

                val categoryReference = repositoryImp.getCategoryReference(category)

                when (categoryReference) {
                    is TaskResult.Error -> {
                        _state.value = TaskScreenState.Error("Error: ${categoryReference.error}")
                    }

                    is TaskResult.Success -> {
                        val response =
                            repositoryImp.createTask(
                                name = name,
                                description = description,
                                date = date,
                                check = check,
                                deliverables = deliverables,
                                deliverablesDescription = deliverablesDescription,
                                users = users,
                                category = categoryReference.documentReference!!,
                                creationDate = creationDate
                            )

                        when (response) {
                            is TaskResult.Error -> {
                                _state.value = TaskScreenState.Error("Error: ${response.error}")
                            }

                            is TaskResult.Success -> {
                                _state.value = TaskScreenState.Success(response.message)
                                _resetFields.value = true
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _state.value = TaskScreenState.Error("Error: ${e.message}")
            }
        }
    }

    private fun validations(name: String, date: String, category: String): Boolean {
        _showToast.value = true
        if (name.isBlank()) {
            _state.value = TaskScreenState.Error(resourceProvider.getString(R.string.name_empty))
            return false
        }
        if (category.isBlank()) {
            _state.value = TaskScreenState.Error(resourceProvider.getString(R.string.category_empty))
            return false
        }
        if (date.isBlank()) {
            _state.value = TaskScreenState.Error(resourceProvider.getString(R.string.date_empty))
            return false
        }

        // Date Validation
        val formatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy") // Match your date formatval taskDate = LocalDate.parse(date, formatter)
        val today = LocalDate.now()
        val dateSelected = LocalDate.parse(date, formatter)

        if (dateSelected.isBefore(today)) {
            _state.value = TaskScreenState.Error(resourceProvider.getString(R.string.date_past))
            return false
        }

        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getAllTasks() {
        viewModelScope.launch {
            try {
                when (val response = repositoryImp.getAllTasks()) {
                    is TaskResult.Error -> {
                        _state.value = TaskScreenState.Error("Error: ${response.error}")
                    }

                    is TaskResult.Success -> {
                        _state.value = TaskScreenState.Success(response.message)
                        tasks = sortTasksByDate(response.tasks)
                    }
                }

            } catch (e: Exception) {
                _state.value = TaskScreenState.Error("Error: ${e.message}")
            }
        }
    }

    private fun sortTasksByDate(tasks: List<TaskDom>): List<TaskDom> {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return tasks.sortedWith(compareBy<TaskDom> { it.check } // Primero por check, false primero
            .thenByDescending { LocalDate.parse(it.creationDate, formatter) }) // Luego por fecha de creación en orden descendente
    }


    fun onCheckedChange(taskId: String, check: Boolean) {
        viewModelScope.launch {
            try {
                when (val response = repositoryImp.onCheckChange(taskId, check)) {
                    is TaskResult.Error -> {
                        _state.value = TaskScreenState.Error("Error: ${response.error}")
                    }
                    is TaskResult.Success -> {
                        getAllTasks()
                    }
                }

            } catch (e: Exception) {
                _state.value = TaskScreenState.Error("Error: ${e.message}")
            }
        }
    }

    fun deleteTask(taskId:String){
        _state.value = TaskScreenState.Loading
        viewModelScope.launch {
            try {

                val response = repositoryImp.deleteTask(taskId)

                when(response){
                    is TaskResult.Error -> {
                        _state.value = TaskScreenState.Error("Error: ${response.error}")
                    }
                    is TaskResult.Success -> {
                        _showToast.value = true
                        _state.value = TaskScreenState.Success(response.message)
                    }
                }

            } catch (e:Exception){
                _state.value = TaskScreenState.Error("Error: ${e.message}")
            }
        }
    }

    fun resetState() {
        _state.value = TaskScreenState.Initial
        _showToast.value = false // Reset flag to hide Toast
    }

    fun cleanFieldHandled() {
        _resetFields.value = false
    }
}