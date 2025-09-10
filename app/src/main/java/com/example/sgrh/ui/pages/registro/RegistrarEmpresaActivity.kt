package com.example.sgrh.ui.pages.registro

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.sgrh.data.remote.RetrofitClient
import com.example.sgrh.data.remote.RegistrarEmpresaRequest
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun RegistrarEmpresaScreen(
    onSuccess: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // --- Datos Empresa ---
    var nombreEmpresa by remember { mutableStateOf("") }
    var correoEmpresa by remember { mutableStateOf("") }
    var pais by remember { mutableStateOf("") }
    var telefonoEmpresa by remember { mutableStateOf("") }
    var direccionEmpresa by remember { mutableStateOf("") }

    // --- Datos Gerente ---
    var nombrePersona by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var codigo by remember { mutableStateOf("") }   // Identificación del gerente
    var email by remember { mutableStateOf("") }
    var passwordPersona by remember { mutableStateOf("") }
    var telefonoPersona by remember { mutableStateOf("") }
    var direccionPersona by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") } // yyyy-MM-dd
    var ciudad by remember { mutableStateOf("") }

    var loading by remember { mutableStateOf(false) }

    fun validarCampos(): Boolean {
        val campos = listOf(
            nombreEmpresa, correoEmpresa, pais, telefonoEmpresa, direccionEmpresa,
            nombrePersona, apellido, codigo, email, passwordPersona,
            telefonoPersona, direccionPersona, fecha, ciudad
        )
        return campos.all { it.isNotBlank() }
    }

    fun onSubmit() {
        if (!validarCampos()) {
            scope.launch { snackbarHostState.showSnackbar("⚠️ Completa todos los campos") }
            return
        }

        val request = RegistrarEmpresaRequest(
            nombreEmpresa, correoEmpresa, pais, telefonoEmpresa, direccionEmpresa,
            nombrePersona, apellido, email, passwordPersona,
            telefonoPersona, direccionPersona, fecha, ciudad, codigo
        )

        scope.launch {
            loading = true
            try {
                val response = RetrofitClient.api.registrarEmpresa(request)
                if (response.isSuccessful) {
                    val body = response.body()
                    snackbarHostState.showSnackbar(body?.message ?: "✅ Empresa y gerente registrados correctamente")
                    onSuccess()
                } else {
                    snackbarHostState.showSnackbar("❌ Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                snackbarHostState.showSnackbar("⚠️ Error de red: ${e.message}")
            } finally {
                loading = false
            }
        }
    }

    // 📅 DatePicker para la fecha de nacimiento
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            // Formato yyyy-MM-dd
            val mes = (month + 1).toString().padStart(2, '0')
            val dia = day.toString().padStart(2, '0')
            fecha = "$year-$mes-$dia"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            Text("Registro Empresa y Gerente", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))

            // ----- Empresa -----
            Text("Datos de la Empresa", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = nombreEmpresa, onValueChange = { nombreEmpresa = it },
                label = { Text("Nombre de Empresa") }, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = correoEmpresa, onValueChange = { correoEmpresa = it },
                label = { Text("Correo Empresa") }, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = pais, onValueChange = { pais = it },
                label = { Text("País") }, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = telefonoEmpresa, onValueChange = { telefonoEmpresa = it },
                label = { Text("Teléfono Empresa") }, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = direccionEmpresa, onValueChange = { direccionEmpresa = it },
                label = { Text("Dirección Empresa") }, modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
            Divider()
            Spacer(Modifier.height(16.dp))

            // ----- Gerente -----
            Text("Datos del Gerente", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = nombrePersona, onValueChange = { nombrePersona = it },
                label = { Text("Nombres") }, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = apellido, onValueChange = { apellido = it },
                label = { Text("Apellidos") }, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = codigo, onValueChange = { codigo = it },
                label = { Text("Identificación") }, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = email, onValueChange = { email = it },
                label = { Text("Correo Electrónico") }, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = passwordPersona, onValueChange = { passwordPersona = it },
                label = { Text("Contraseña") }, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = telefonoPersona, onValueChange = { telefonoPersona = it },
                label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = direccionPersona, onValueChange = { direccionPersona = it },
                label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            // 📅 Botón para elegir fecha
            OutlinedButton(
                onClick = { datePickerDialog.show() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (fecha.isBlank()) "Seleccionar Fecha de Nacimiento" else "Fecha: $fecha")
            }

            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = ciudad, onValueChange = { ciudad = it },
                label = { Text("Ciudad") }, modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = { onSubmit() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !loading
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Registrando...")
                } else {
                    Text("Registrar Empresa y Gerente")
                }
            }
        }
    }
}
