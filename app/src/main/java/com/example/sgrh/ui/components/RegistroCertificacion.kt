package com.example.sgrh.ui.components

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.sgrh.data.remote.ApiService
import com.example.sgrh.data.remote.Certificado
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.InputStream

// 🔹 Función corregida: obtener nombre real + MIME correcto
fun Uri.toMultipartBody(context: Context, paramName: String): MultipartBody.Part? {
    val contentResolver = context.contentResolver
    val inputStream: InputStream = contentResolver.openInputStream(this) ?: return null

    // Nombre real del archivo
    var fileName = "archivo_${System.currentTimeMillis()}"
    val cursor = contentResolver.query(this, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (index >= 0) fileName = it.getString(index)
        }
    }

    // Guardar en cache con ese nombre
    val tempFile = File(context.cacheDir, fileName)
    tempFile.outputStream().use { output -> inputStream.copyTo(output) }

    // Detectar MIME real
    val mimeType = contentResolver.getType(this) ?: "application/octet-stream"

    // Crear parte del request
    val reqFile = tempFile.asRequestBody(mimeType.toMediaType())
    return MultipartBody.Part.createFormData(paramName, fileName, reqFile)
}

@Composable
fun RegistroCertificacion(
    apiService: ApiService,
    personaId: String,
    empresaId: String,
    onVolver: () -> Unit
) {
    var certificados by remember { mutableStateOf<List<Certificado>>(emptyList()) }
    var mensaje by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var nombreCertificado by remember { mutableStateOf("") }
    var archivoUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> archivoUri = uri }

    fun cargarCertificados() {
        if (personaId.isBlank()) {
            mensaje = "❌ Persona inválida"
            return
        }
        scope.launch {
            try {
                val resp = apiService.getCertificadosPorPersona(personaId)
                if (resp.isSuccessful) certificados = resp.body() ?: emptyList()
                else mensaje = "❌ Error al cargar certificados: ${resp.code()}"
            } catch (e: Exception) {
                mensaje = "❌ ${e.message}"
            }
        }
    }

    LaunchedEffect(personaId) { cargarCertificados() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Gestión de Certificaciones", style = MaterialTheme.typography.headlineSmall)
            mensaje?.let {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = it,
                    color = if (it.contains("✅")) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error
                )
            }
            Spacer(Modifier.height(16.dp))
        }

        if (certificados.isEmpty()) {
            item { Text("No hay certificados para esta persona") }
        } else {
            items(certificados) { cert ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(cert.nombre, style = MaterialTheme.typography.bodyLarge)
                        Text("Subido el ${cert.fecha}", style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = {
                                if (cert._id.isBlank()) {
                                    mensaje = "❌ Certificado inválido"
                                    return@OutlinedButton
                                }
                                scope.launch {
                                    try {
                                        val resp = apiService.eliminarCertificado(cert._id)
                                        mensaje = if (resp.isSuccessful) {
                                            "✅ Certificado eliminado correctamente"
                                        } else {
                                            "❌ Error al eliminar: ${resp.code()}"
                                        }
                                        cargarCertificados()
                                    } catch (e: Exception) {
                                        mensaje = "❌ ${e.message}"
                                    }
                                }
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) { Text("Eliminar") }
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(16.dp))
            OutlinedButton(
                onClick = { launcher.launch("*/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (archivoUri == null) "Seleccionar archivo" else "Archivo seleccionado")
            }
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = nombreCertificado,
                onValueChange = { nombreCertificado = it },
                label = { Text("Nombre del certificado") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    if (archivoUri == null || nombreCertificado.isBlank()) {
                        mensaje = "⚠️ Debes seleccionar archivo y escribir nombre"
                        return@Button
                    }
                    scope.launch {
                        try {
                            val multipart = archivoUri!!.toMultipartBody(context, "archivo")
                            if (multipart == null) {
                                mensaje = "❌ Error al leer archivo"
                                return@launch
                            }

                            val resp = apiService.registrarCertificado(
                                personaId = personaId.toRequestBody("text/plain".toMediaType()),
                                empresaId = empresaId.toRequestBody("text/plain".toMediaType()),
                                nombre = nombreCertificado.toRequestBody("text/plain".toMediaType()),
                                archivo = multipart
                            )

                            if (resp.isSuccessful) {
                                mensaje = "✅ Certificado guardado correctamente"
                                nombreCertificado = ""
                                archivoUri = null
                                cargarCertificados()
                            } else {
                                mensaje = "❌ Error al guardar: ${resp.code()}"
                            }
                        } catch (e: Exception) {
                            mensaje = "❌ ${e.message}"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Guardar certificado") }

            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = onVolver, modifier = Modifier.fillMaxWidth()) {
                Text("← Volver al Menú")
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
