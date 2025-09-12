package com.example.sgrh.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.sgrh.data.remote.ApiService
import com.example.sgrh.data.remote.Empresa
import kotlinx.coroutines.launch

@Composable
fun ConfiguracionSistema(
    empresaId: String,
    apiService: ApiService,
    onVolver: () -> Unit,
    onEliminarEmpresa: () -> Unit = {} // 👈 valor por defecto
) {
    var empresa by remember { mutableStateOf(Empresa()) }
    var mensaje by remember { mutableStateOf<Pair<String, String>?>(null) }
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    // Cargar datos de la empresa
    LaunchedEffect(empresaId) {
        try {
            val response = apiService.getEmpresaById(empresaId)
            if (response.isSuccessful) {
                response.body()?.let { empresa = it }
            } else {
                mensaje = "error" to "No se pudo cargar la empresa"
            }
        } catch (e: Exception) {
            mensaje = "error" to "Error al conectar con el servidor"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Configuración del Sistema", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        val campos = listOf(
            "nombre" to "Nombre",
            "pais" to "País",
            "correo" to "Correo",
            "ciudad" to "Ciudad",
            "telefono" to "Teléfono",
            "direccion" to "Dirección"
        )

        campos.forEach { (clave, label) ->
            val valor = when (clave) {
                "nombre" -> empresa.nombre
                "pais" -> empresa.pais
                "correo" -> empresa.correo
                "ciudad" -> empresa.ciudad
                "telefono" -> empresa.telefono
                "direccion" -> empresa.direccion
                else -> ""
            }

            val keyboardOpts = when (clave) {
                "correo" -> KeyboardOptions(keyboardType = KeyboardType.Email)
                "telefono" -> KeyboardOptions(keyboardType = KeyboardType.Phone)
                else -> KeyboardOptions.Default
            }

            OutlinedTextField(
                value = valor,
                onValueChange = { nuevo ->
                    empresa = when (clave) {
                        "nombre" -> empresa.copy(nombre = nuevo)
                        "pais" -> empresa.copy(pais = nuevo)
                        "correo" -> empresa.copy(correo = nuevo)
                        "ciudad" -> empresa.copy(ciudad = nuevo)
                        "telefono" -> empresa.copy(telefono = nuevo)
                        "direccion" -> empresa.copy(direccion = nuevo)
                        else -> empresa
                    }
                },
                label = { Text(label) },
                singleLine = clave != "direccion",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                keyboardOptions = keyboardOpts,
                maxLines = if (clave == "direccion") 4 else 1
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Guardar cambios
        Button(
            onClick = {
                scope.launch {
                    try {
                        val response = apiService.actualizarEmpresa(empresaId, empresa)
                        mensaje = if (response.isSuccessful) {
                            "exito" to "Cambios guardados correctamente"
                        } else {
                            "error" to "Error al guardar cambios"
                        }
                    } catch (e: Exception) {
                        mensaje = "error" to "Error al conectar con el servidor"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar cambios")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Eliminar empresa y empleados
        Button(
            onClick = {
                scope.launch {
                    try {
                        val response = apiService.eliminarEmpresa(empresaId)
                        if (response.isSuccessful) {
                            mensaje = "exito" to "❌ Empresa y empleados eliminados"
                            onEliminarEmpresa()
                        } else {
                            mensaje = "error" to "No se pudo eliminar la empresa"
                        }
                    } catch (e: Exception) {
                        mensaje = "error" to "Error al conectar con el servidor"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Eliminar Empresa y Empleados")
        }

        Spacer(modifier = Modifier.height(12.dp))

        mensaje?.let { (tipo, texto) ->
            Text(
                text = texto,
                color = if (tipo == "exito") MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(4.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(onClick = onVolver, modifier = Modifier.fillMaxWidth()) {
            Text("← Volver al Menú")
        }
    }
}
