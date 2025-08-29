package com.example.sgrh.ui.pages.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.sgrh.data.remote.LoginRequest
import com.example.sgrh.data.remote.LoginResponse
import com.example.sgrh.data.remote.RetrofitClient

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

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(loading = true, error = "")

                val response: LoginResponse =
                    RetrofitClient.api.login(LoginRequest(email, password))

                if (response.usuario != null) {
                    // Login exitoso
                    _uiState.value = _uiState.value.copy(
                        loading = false,
                        error = "",
                        rol = response.usuario?.rol,
                        userId = response.usuario?._id,
                        empresaId = response.usuario?.empresaId
                    )

                    // Llamamos al callback
                    response.usuario?.rol?.let { onLoginSuccess(it) }
                } else {
                    // Usuario o contraseña incorrecta
                    _uiState.value = _uiState.value.copy(
                        loading = false,
                        error = "Usuario o contraseña incorrecta"
                    )
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    error = e.message ?: "Error de conexión"
                )
            }
        }
    }
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val error: String = "",
    val loading: Boolean = false,
    val rol: String? = null,
    val userId: String? = null,
    val empresaId: String? = null
)
