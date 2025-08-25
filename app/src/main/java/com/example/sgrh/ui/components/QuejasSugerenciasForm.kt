// components/QuejasSugerenciasForm.kt
package com.example.sgrh.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import java.net.HttpURLConnection
import java.net.URL

@Composable
fun QuejasSugerenciasForm(onBack: () -> Unit) {
    var asunto by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var mensajeServidor by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    fun enviarQueja() {
        scope.launch(Dispatchers.IO) {
            try {
                val url = URL("http://10.0.2.2:3000/api/quejas") // ⚠️ En Android usar 10.0.2.2 en lugar de localhost
                val connection = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    setRequestProperty("Content-Type", "application/json")
                    doOutput = true
                }

                val jsonBody = """{"asunto":"$asunto","mensaje":"$mensaje"}"""
                connection.outputStream.use { it.write(jsonBody.toByteArray()) }

                val responseCode = connection.responseCode
                if (responseCode == 200) {
                    mensajeServidor = "✅ ¡Mensaje enviado correctamente!"
                } else {
                    mensajeServidor = "❌ Error al enviar la queja o sugerencia"
                }
                submitted = true
                asunto = ""
                mensaje = ""

                delay(4000)
                submitted = false
                mensajeServidor = ""
            } catch (e: Exception) {
                mensajeServidor = "❌ ${e.message}"
                submitted = true
            }
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

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
            Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)) {
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
