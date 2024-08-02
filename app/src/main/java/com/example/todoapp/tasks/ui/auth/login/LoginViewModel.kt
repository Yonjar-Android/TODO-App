package com.example.todoapp.tasks.ui.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.R
import com.example.todoapp.tasks.data.repositories.authRepository.AuthRepositoryImp
import com.example.todoapp.tasks.data.repositories.authRepository.CreateUserResult
import com.example.todoapp.tasks.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repositoryImp: AuthRepositoryImp,
    private val resourceProvider: ResourceProvider
) :
    ViewModel() {
    private val _state = MutableStateFlow<LoginState>(LoginState.Initial)
    var state: StateFlow<LoginState> = _state

    fun loginUser(email: String, password: String) {
        _state.value = LoginState.Loading

        if (!validations(email, password)) return

        viewModelScope.launch {
            try {
                when (val response = repositoryImp.loginUser(email, password)) {
                    is CreateUserResult.Error -> {
                        _state.value = LoginState.Error("Error: ${response.error}")
                    }

                    is CreateUserResult.Success -> {
                        _state.value = LoginState.Success(response.user)
                    }
                }

            } catch (e: Exception) {
                _state.value = LoginState.Error("Error: ${e.message}")
            }
        }
    }

    private fun validations(email: String, password: String): Boolean {
        if (email == "" || password == "") {
            _state.value = LoginState.Error(resourceProvider.getString(R.string.fillOutFields))
            return false
        }
        return true
    }

    fun resetState() {
        _state.value = LoginState.Initial
    }
}