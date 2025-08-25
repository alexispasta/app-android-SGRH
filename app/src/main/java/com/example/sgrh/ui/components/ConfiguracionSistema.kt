// 📂 ui/components/ConfiguracionSistema.kt
package com.example.sgrh.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions   // ✅ import correcto
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

// Modelo de datos de empresa
data class Empresa(
    var nombre: String = "",
    var pais: String = "",
    var correo: String = "",
    var ciudad: String = "",
    var telefono: String = "",
    var direccion: String = ""
)

@Composable
fun ConfiguracionSistema(
    empresaInicial: Empresa,
    onGuardar: (Empresa) -> Unit,
    onVolver: () -> Unit
) {
    var empresa by remember { mutableStateOf(empresaInicial) }
    var mensaje by remember { mutableStateOf<Pair<String, String>?>(null) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "Configuración del Sistema",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campos editables
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

        // Botón Guardar
        Button(
            onClick = {
                if (empresa.nombre.isNotBlank() && empresa.correo.isNotBlank()) {
                    onGuardar(empresa)
                    mensaje = "exito" to "Cambios guardados correctamente"
                } else {
                    mensaje = "error" to "Debes completar al menos Nombre y Correo"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar cambios")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Mostrar mensaje
        mensaje?.let { (tipo, texto) ->
            Text(
                text = texto,
                color = if (tipo == "exito") MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(4.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("← Volver al Menú")
        }
    }
}
