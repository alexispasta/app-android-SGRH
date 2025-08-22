package com.example.sgrh.ui.pages.login

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun onEmailChange(newEmail: String) {
        _uiState.value = _uiState.value.copy(email = newEmail)
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.value = _uiState.value.copy(password = newPassword)
    }

    fun login(onLoginSuccess: (rol: String) -> Unit) {
        val email = _uiState.value.email
        val password = _uiState.value.password

        if (email == "admin@email.com" && password == "1234") {
            _uiState.value = _uiState.value.copy(error = "")
            onLoginSuccess("gerente") // puedes devolver el rol según tu lógica
        } else {
            _uiState.value = _uiState.value.copy(error = "Credenciales incorrectas")
        }
    }
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val error: String = ""
)
