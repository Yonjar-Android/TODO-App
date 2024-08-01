package com.example.todoapp.tasks.ui.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.tasks.data.repositories.authRepository.AuthRepositoryImp
import com.example.todoapp.tasks.data.repositories.authRepository.ResetResult
import com.example.todoapp.tasks.data.repositories.authRepository.UserResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserScreenViewModel @Inject constructor(private val repositoryImp: AuthRepositoryImp) :
    ViewModel() {
    private val _state = MutableStateFlow<UserScreenState>(UserScreenState.Initial)
    var state: StateFlow<UserScreenState> = _state

    fun getUserByEmail(email: String) {
        _state.value = UserScreenState.Loading
        viewModelScope.launch {
            try {
                val response = repositoryImp.getUserByEmail(email)

                when (response) {
                    is UserResult.Error -> {
                        _state.value = UserScreenState.Error("Error: ${response.error}")
                    }

                    is UserResult.Success -> {
                        _state.value = UserScreenState.Success(response.user)
                    }
                }
            } catch (e: Exception) {
                _state.value = UserScreenState.Error("Error: ${e.message}")
            }
        }
    }

    fun logOutUser() {
        _state.value = UserScreenState.Loading
        viewModelScope.launch {
            try {
                when (val response = repositoryImp.logOutUser()) {
                    is ResetResult.Error -> {
                        _state.value = UserScreenState.Error("Error: ${response.error}")
                    }

                    is ResetResult.Success -> {
                        _state.value = UserScreenState.Initial
                    }
                }
            } catch (e: Exception) {
                _state.value = UserScreenState.Error("Error: ${e.message}")
            }
        }
    }
}