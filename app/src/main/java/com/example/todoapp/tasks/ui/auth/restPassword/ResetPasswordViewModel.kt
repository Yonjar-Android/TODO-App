package com.example.todoapp.tasks.ui.auth.restPassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.tasks.data.repositories.authRepository.AuthRepositoryImp
import com.example.todoapp.tasks.data.repositories.authRepository.ResetResult
import com.example.todoapp.tasks.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val repositoryImp: AuthRepositoryImp,
    private val resourceProvider: ResourceProvider
) :
    ViewModel() {

    private val _state = MutableStateFlow<ResetPasswordState>(ResetPasswordState.Initial)
    var state: StateFlow<ResetPasswordState> = _state

    fun resetPassword(email: String) {
        _state.value = ResetPasswordState.Loading

        viewModelScope.launch {
            try {
                when(val response = repositoryImp.resetPassword(email)){
                    is ResetResult.Error -> {
                        _state.value = ResetPasswordState.Error("Error: ${response.error}")
                    }
                    is ResetResult.Success -> {
                        _state.value = ResetPasswordState.Success(response.message)
                    }
                }
            } catch (e: Exception) {
                _state.value = ResetPasswordState.Error("Error: ${e.message}")
            }
        }
    }

    fun resetState() {
        _state.value = ResetPasswordState.Initial
    }

}