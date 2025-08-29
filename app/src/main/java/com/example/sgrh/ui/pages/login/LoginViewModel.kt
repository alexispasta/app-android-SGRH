package com.example.sgrh.ui.pages.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sgrh.data.remote.LoginRequest
import com.example.sgrh.data.remote.LoginResponse
import com.example.sgrh.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

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
            _uiState.value = _uiState.value.copy(loading = true, error = "")
            try {
                val response: Response<LoginResponse> =
                    RetrofitClient.api.login(LoginRequest(email, password))

                println("Login response code: ${response.code()}")
                println("Login response body: ${response.body()}") // ✅ debug

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.usuario != null) {
                        println("Login exitoso! Rol: ${body.usuario.rol}")
                        _uiState.value = _uiState.value.copy(
                            loading = false,
                            error = "",
                            rol = body.usuario.rol,
                            userId = body.usuario._id,
                            empresaId = body.usuario.empresaId
                        )
                        onLoginSuccess(body.usuario.rol)
                    } else {
                        _uiState.value = _uiState.value.copy(
                            loading = false,
                            error = body?.message ?: "Usuario o contraseña incorrecta"
                        )
                    }
                } else {
                    val errorMessage = when (response.code()) {
                        400 -> "Email y contraseña son requeridos"
                        401 -> "Contraseña incorrecta"
                        404 -> "Usuario no encontrado"
                        else -> "Error HTTP: ${response.code()}"
                    }
                    _uiState.value = _uiState.value.copy(
                        loading = false,
                        error = errorMessage
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
