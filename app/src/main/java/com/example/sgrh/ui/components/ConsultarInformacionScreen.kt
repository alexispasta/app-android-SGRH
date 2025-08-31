package com.example.sgrh.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sgrh.data.remote.Empleado
import com.example.sgrh.data.remote.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.HttpException

@Composable
fun ConsultarInformacionScreen(
    onVolver: () -> Unit,
    usuarioId: String
) {
    var empleado by remember { mutableStateOf<Empleado?>(null) }
    var loading by remember { mutableStateOf(true) }
    var editando by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }

    var formData by remember { mutableStateOf(
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
    )}

    val scope = rememberCoroutineScope()

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

        val campos = listOf(
            "nombre" to "Nombres",
            "apellido" to "Apellidos",
            "email" to "Correo Electrónico",
            "telefono" to "Teléfono",
            "direccion" to "Dirección",
            "fecha" to "Fecha de Nacimiento",
            "ciudad" to "Ciudad"
        )

        campos.forEach { (key, label) ->
            val valorActual = when (key) {
                "nombre" -> empleado!!.nombre
                "apellido" -> empleado!!.apellido
                "email" -> empleado!!.email
                "telefono" -> empleado!!.telefono ?: ""
                "direccion" -> empleado!!.direccion ?: ""
                "fecha" -> empleado!!.fecha ?: ""
                "ciudad" -> empleado!!.ciudad ?: ""
                else -> ""
            }

            val valorForm = when (key) {
                "nombre" -> formData.nombre
                "apellido" -> formData.apellido
                "email" -> formData.email
                "telefono" -> formData.telefono ?: ""
                "direccion" -> formData.direccion ?: ""
                "fecha" -> formData.fecha ?: ""
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
                        "fecha" -> formData.copy(fecha = it)
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

        Spacer(Modifier.height(12.dp))
        Text("Rol: ${empleado!!.rol}")
        Text("Código de empresa: ${empleado!!.codigo ?: ""}")

        Spacer(Modifier.height(20.dp))

        Row {
            if (!editando) {
                Button(onClick = { editando = true }) { Text("Editar") }
            } else {
                Button(onClick = {
                    scope.launch {
                        try {
                            val actualizado = empleado!!.copy(
                                nombre = formData.nombre,
                                apellido = formData.apellido,
                                email = formData.email,
                                telefono = formData.telefono,
                                direccion = formData.direccion,
                                ciudad = formData.ciudad,
                                fecha = formData.fecha,
                                empresaId = empleado!!.empresaId
                            )
                            val response = RetrofitClient.api.actualizarEmpleado(usuarioId, actualizado)
                            if (response.isSuccessful) {
                                empleado = actualizado
                                mensaje = "✅ Información actualizada correctamente."
                                editando = false
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
                }) { Text("Cancelar") }
            }

            Spacer(Modifier.width(8.dp))
            OutlinedButton(onClick = onVolver) { Text("Volver") }
        }
    }
}
