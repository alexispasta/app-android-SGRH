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

    fun login(onLoginSuccess: (rol: String, userId: String, empresaId: String?) -> Unit) {
        val email = _uiState.value.email
        val password = _uiState.value.password

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = "")
            try {
                val response: Response<LoginResponse> =
                    RetrofitClient.api.login(LoginRequest(email, password))

                println("Login response code: ${response.code()}")
                println("Login response body: ${response.body()}")

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.usuario != null) {
                        val usuario = body.usuario
                        println("✅ Login exitoso! Rol: ${usuario.rol}")
                        _uiState.value = _uiState.value.copy(
                            loading = false,
                            error = "",
                            rol = usuario.rol,
                            userId = usuario._id,
                            empresaId = usuario.empresaId
                        )
                        onLoginSuccess(usuario.rol, usuario._id, usuario.empresaId)
                    } else {
                        _uiState.value = _uiState.value.copy(
                            loading = false,
                            error = body?.error ?: body?.message ?: "Usuario o contraseña incorrecta"
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        loading = false,
                        error = "Error HTTP: ${response.code()}"
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
