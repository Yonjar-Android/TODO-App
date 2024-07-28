package com.example.todoapp.tasks.ui.taskScreen.taskDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.tasks.data.repositories.taskRepository.TasksRepositoryImp
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

    fun getTaskById(taskId: String) {
        viewModelScope.launch {
            try {

            } catch (e: Exception) {
                _state.value = TaskDetailState.Error(e.message ?: "")
            }
        }
    }

}