package com.example.todoapp.tasks.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.tasks.data.repositories.AuthRepositoryImp
import com.example.todoapp.tasks.data.repositories.CreateUserResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repositoryImp: AuthRepositoryImp) :
    ViewModel() {
    private val _state = MutableStateFlow<LoginState>(LoginState.Initial)
    var state: StateFlow<LoginState> = _state

    fun loginUser(email: String, password: String) {
        _state.value = LoginState.Loading

        if (!validations(email, password)) return

        viewModelScope.launch {
            try {
                when(val response = repositoryImp.loginUser(email, password)){
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

    fun validations(email:String, password: String):Boolean{
        if (email == "" || password == ""){
            _state.value = LoginState.Error("Rellene los campos")
            return false
        }
        return true
    }

    fun resetState() {
        _state.value = LoginState.Initial
    }
}