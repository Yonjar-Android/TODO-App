package com.example.todoapp.tasks.ui.register

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
class RegisterViewModel @Inject constructor(private val repositoryImp: AuthRepositoryImp) :
    ViewModel() {

    private val _state = MutableStateFlow<RegisterState>(RegisterState.Initial)
    var state: StateFlow<RegisterState> = _state

    fun createUser(name: String, email: String, password: String, password2: String) {

        _state.value = RegisterState.Loading

        viewModelScope.launch {
            try {
                if (emailValidations(name, email) && passwordValidations(password, password2)) {

                    when(val response = repositoryImp.createUser(name, email, password)){
                        is CreateUserResult.Error -> {
                            if (response.error == null){
                                _state.value = RegisterState.Error("Response was null")
                            } else{
                                _state.value = RegisterState.Error("Error: ${response.error}")
                            }
                        }
                        is CreateUserResult.Success -> {
                            _state.value = RegisterState.Success(response.user)
                        }
                    }
                }

            } catch (e: Exception) {
                _state.value = RegisterState.Error("Error: ${e.message}")
            }
        }
    }

     fun resetState() {
        _state.value = RegisterState.Initial
    }

    private fun passwordValidations(password: String, password2: String): Boolean {
        if (password == "" || password2 == "") {
            _state.value = RegisterState.Error("Rellene los campos de contraseña")
            return false
        }

        if (password.length < 8) {
            _state.value = RegisterState.Error("La contraseña debe contener almenos 8 caracteres")
            return false
        }

        if (password != password2) {
            _state.value = RegisterState.Error("Las contraseñas no coinciden")
            return false
        }
        return true
    }

    private fun emailValidations(name: String, email: String): Boolean {

        val emailRegex = Regex("^([a-zA-Z0-9_.-]+)@([\\da-zA-Z.-]+)\\.([a-zA-Z.]{2,6})$")

        if (name == "") {
            _state.value = RegisterState.Error("Rellene el campo nombre")
            return false
        }

        if (email == "") {
            _state.value = RegisterState.Error("Rellene el campo correo electrónico")
            return false
        }

        if (!emailRegex.matches(email)) {
            _state.value = RegisterState.Error("El correo ingresado no es válido")
            return false
        }

        return emailRegex.matches(email)
    }



}