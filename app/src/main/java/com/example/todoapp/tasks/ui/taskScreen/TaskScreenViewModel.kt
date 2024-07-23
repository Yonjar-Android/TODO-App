package com.example.todoapp.tasks.ui.taskScreen

import android.os.Build
import androidx.annotation.RequiresApi
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class TaskScreenViewModel @Inject constructor(
    private val repositoryImp: TasksRepositoryImp
) : ViewModel() {
    private val _state = MutableStateFlow<TaskScreenState>(TaskScreenState.Initial)
    var state: StateFlow<TaskScreenState> = _state

    private var _showToast = MutableStateFlow(false)
    val showToast: StateFlow<Boolean> = _showToast

    private val _resetFields = MutableStateFlow(false)
    val resetFields: StateFlow<Boolean> = _resetFields

    var categories: List<Category>? = listOf()

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
                    }
                }
            } catch (e: Exception) {
                _state.value = TaskScreenState.Error("Error: ${e.message}")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createTask(
        name: String,
        description: String?,
        date: String,
        check: Boolean = false,
        deliverablesDescription: String?,
        deliverables: List<String> = listOf(),
        users: List<String> = listOf(),
        category: String
    ) {
        viewModelScope.launch {
            _state.value = TaskScreenState.Loading
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
                                category = categoryReference.documentReference!!
                            )

                        when (response) {
                            is TaskResult.Error -> {
                                _state.value = TaskScreenState.Error("Error: ${response.error}")
                            }

                            is TaskResult.Success -> {
                                _state.value = TaskScreenState.Success(response.message, categories)
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun validations(name: String, date: String, category: String):Boolean{
        _showToast.value = true
        if (name.isBlank()){
            _state.value = TaskScreenState.Error("Rellene el campo nombre")
            return false
        }
        if (category.isBlank()){
            _state.value = TaskScreenState.Error("Seleccione una categoría")
            return false
        }
        if (date.isBlank()){
            _state.value = TaskScreenState.Error("Seleccione una fecha")
            return false
        }

        // Date Validation
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy") // Match your date formatval taskDate = LocalDate.parse(date, formatter)
        val today = LocalDate.now()
        val dateSelected = LocalDate.parse(date,formatter)

        if (dateSelected.isBefore(today)) {
            _state.value = TaskScreenState.Error("No puede seleccionar una fecha anterior a hoy")
            return false
        }

        return true
    }

    fun resetState() {
        _state.value = TaskScreenState.Initial
        _showToast.value = false // Reset flag to hide Toast
    }

    fun cleanFieldHandled() {
        _resetFields.value = false
    }
}