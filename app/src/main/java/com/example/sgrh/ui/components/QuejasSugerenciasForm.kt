package com.example.sgrh.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sgrh.data.remote.ApiService
import com.example.sgrh.data.remote.GenericResponse
import com.example.sgrh.data.remote.QuejaRequest
import kotlinx.coroutines.launch

@Composable
fun QuejasSugerenciasForm(
    apiService: ApiService,
    onBack: () -> Unit
) {
    var asunto by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var mensajeServidor by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    fun enviarQueja() {
        if (asunto.isBlank() || mensaje.isBlank()) {
            mensajeServidor = "❌ Por favor completa todos los campos"
            submitted = true
            return
        }

        scope.launch {
            try {
                val response = apiService.enviarQueja(
                    QuejaRequest(asunto = asunto, mensaje = mensaje)
                )
                if (response.isSuccessful) {
                    mensajeServidor = "✅ ¡Mensaje enviado correctamente!"
                    asunto = ""
                    mensaje = ""
                } else {
                    mensajeServidor = "❌ Error al enviar la queja"
                }
            } catch (e: Exception) {
                mensajeServidor = "❌ ${e.message}"
            } finally {
                submitted = true
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Quejas y Sugerencias", style = MaterialTheme.typography.headlineSmall)
        Text("Envíanos tus comentarios para mejorar el sistema.", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = asunto,
            onValueChange = { asunto = it },
            label = { Text("Asunto") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = mensaje,
            onValueChange = { mensaje = it },
            label = { Text("Mensaje") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Button(onClick = { enviarQueja() }) {
                Text("Enviar")
            }
            Spacer(modifier = Modifier.width(12.dp))
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("Volver")
            }
        }

        if (submitted) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = mensajeServidor,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
