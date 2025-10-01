package com.example.sgrh.ui.components

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.sgrh.data.remote.Empleado
import com.example.sgrh.data.remote.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.util.*

@Composable
fun ConsultarInformacionScreen(
    onVolver: () -> Unit,
    usuarioId: String
) {
    var empleado by remember { mutableStateOf<Empleado?>(null) }
    var loading by remember { mutableStateOf(true) }
    var editando by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }

    // 🔹 Nuevo campo de contraseña (opcional)
    var password by remember { mutableStateOf("") }

    var formData by remember {
        mutableStateOf(
            Empleado(
                _id = "",
                nombre = "",
                apellido = "",
                email = "",
                telefono = "",
                direccion = "",
                ciudad = "",
                fecha = "",
                rol = "",
                codigo = "",
                empresaId = ""
            )
        )
    }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // 🔹 Cargar empleado completo desde el backend usando su ID
    LaunchedEffect(usuarioId) {
        scope.launch {
            try {
                val response = RetrofitClient.api.getEmpleadoById(usuarioId)
                if (response.isSuccessful && response.body() != null) {
                    empleado = response.body()
                    formData = empleado!!
                } else {
                    mensaje = "❌ No se pudo cargar la información del usuario."
                }
            } catch (e: Exception) {
                mensaje = "❌ Error de conexión con el servidor."
            }
            loading = false
        }
    }

    if (loading) {
        Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text("Cargando información...")
        }
        return
    }

    if (empleado == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text(mensaje, color = MaterialTheme.colorScheme.error)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Información de Cuenta", style = MaterialTheme.typography.headlineSmall)
        Text("Consulta y modifica los datos de tu cuenta.", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(12.dp))

        if (mensaje.isNotEmpty()) {
            Text(
                mensaje,
                color = if (mensaje.startsWith("✅")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
            Spacer(Modifier.height(12.dp))
        }

        // Lista de campos
        val campos = listOf(
            "nombre" to "Nombres",
            "apellido" to "Apellidos",
            "email" to "Correo Electrónico",
            "telefono" to "Teléfono",
            "direccion" to "Dirección",
            "ciudad" to "Ciudad"
        )

        campos.forEach { (key, label) ->
            val valorActual = when (key) {
                "nombre" -> empleado!!.nombre ?: ""
                "apellido" -> empleado!!.apellido ?: ""
                "email" -> empleado!!.email ?: ""
                "telefono" -> empleado!!.telefono ?: ""
                "direccion" -> empleado!!.direccion ?: ""
                "ciudad" -> empleado!!.ciudad ?: ""
                else -> ""
            }

            val valorForm = when (key) {
                "nombre" -> formData.nombre ?: ""
                "apellido" -> formData.apellido ?: ""
                "email" -> formData.email ?: ""
                "telefono" -> formData.telefono ?: ""
                "direccion" -> formData.direccion ?: ""
                "ciudad" -> formData.ciudad ?: ""
                else -> ""
            }

            OutlinedTextField(
                value = valorForm,
                onValueChange = {
                    formData = when (key) {
                        "nombre" -> formData.copy(nombre = it)
                        "apellido" -> formData.copy(apellido = it)
                        "email" -> formData.copy(email = it)
                        "telefono" -> formData.copy(telefono = it)
                        "direccion" -> formData.copy(direccion = it)
                        "ciudad" -> formData.copy(ciudad = it)
                        else -> formData
                    }
                },
                label = { Text("$label (Actual: $valorActual)") },
                enabled = editando,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            )
        }

        // 🔹 Fecha con DatePicker
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val fechaSeleccionada = "%04d-%02d-%02d".format(year, month + 1, dayOfMonth)
                formData = formData.copy(fecha = fechaSeleccionada)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        OutlinedTextField(
            value = formData.fecha ?: "",
            onValueChange = { },
            label = { Text("Fecha de Nacimiento (Actual: ${empleado!!.fecha ?: ""})") },
            enabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .clickable(enabled = editando) {
                    datePickerDialog.show()
                }
        )

        // 🔹 Campo contraseña opcional
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Nueva contraseña (opcional)") },
            enabled = editando,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(Modifier.height(12.dp))
        Text("Rol: ${empleado!!.rol ?: ""}")
        Text("Código de empresa: ${empleado!!.codigo ?: ""}")

        Spacer(Modifier.height(20.dp))

        Row {
            if (!editando) {
                Button(onClick = { editando = true }) { Text("Editar") }
            } else {
                Button(onClick = {
                    scope.launch {
                        try {
                            // Copiamos datos
                            var actualizado = empleado!!.copy(
                                nombre = formData.nombre,
                                apellido = formData.apellido,
                                email = formData.email,
                                telefono = formData.telefono,
                                direccion = formData.direccion,
                                ciudad = formData.ciudad,
                                fecha = formData.fecha,
                                empresaId = empleado!!.empresaId
                            )

                            // Si la contraseña no está vacía, la agregamos
                            if (password.isNotBlank()) {
                                // Aquí deberías mandar `password` en el body si tu API lo soporta
                            }

                            val response = RetrofitClient.api.actualizarEmpleado(usuarioId, actualizado)
                            if (response.isSuccessful) {
                                empleado = actualizado
                                mensaje = "✅ Información actualizada correctamente."
                                editando = false
                                password = "" // limpiar
                            } else {
                                mensaje = "❌ Error al actualizar la información."
                            }
                        } catch (e: HttpException) {
                            mensaje = "❌ Error de servidor."
                        } catch (e: Exception) {
                            mensaje = "❌ Error de conexión."
                        }
                    }
                }) { Text("Guardar Cambios") }

                Spacer(Modifier.width(8.dp))

                OutlinedButton(onClick = {
                    editando = false
                    formData = empleado!!
                    password = ""
                }) { Text("Cancelar") }
            }

            Spacer(Modifier.width(8.dp))
            OutlinedButton(onClick = onVolver) { Text("Volver") }
        }
    }
}
