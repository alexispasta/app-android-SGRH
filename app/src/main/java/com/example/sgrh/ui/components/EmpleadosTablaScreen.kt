// 📂 ui/pages/gerente/EmpleadosTablaScreen.kt
package com.example.sgrh.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

// 🔹 Modelo de Empleado
data class Empleado(
    val id: String,
    var nombre: String,
    var apellido: String,
    var email: String,
    var telefono: String,
    var codigo: String,
    var rol: String,
    var ciudad: String,
    var direccion: String? = "",
    var fecha: String? = ""
)

@Composable
fun EmpleadosTablaScreen(onVolver: () -> Unit) {
    var empleados by remember { mutableStateOf(listOf<Empleado>()) }
    var empleadoEditando by remember { mutableStateOf<Empleado?>(null) }
    var mensaje by remember { mutableStateOf<Pair<String, String>?>(null) } // tipo, texto

    val scope = rememberCoroutineScope()
    val empresaId = "123456" // TODO: reemplazar con empresa del usuario logueado (SharedPreferences o ViewModel)

    // 🔹 Cargar empleados al iniciar
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val url = URL("http://10.0.2.2:3000/api/personas/empresa/$empresaId")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                val data = conn.inputStream.bufferedReader().readText()
                val json = JSONArray(data)

                val lista = mutableListOf<Empleado>()
                for (i in 0 until json.length()) {
                    val obj = json.getJSONObject(i)
                    lista.add(
                        Empleado(
                            id = obj.getString("_id"),
                            nombre = obj.getString("nombre"),
                            apellido = obj.getString("apellido"),
                            email = obj.getString("email"),
                            telefono = obj.optString("telefono", ""),
                            codigo = obj.optString("codigo", ""),
                            rol = obj.optString("rol", ""),
                            ciudad = obj.optString("ciudad", "")
                        )
                    )
                }
                empleados = lista
            } catch (e: Exception) {
                mensaje = "error" to (e.message ?: "Error al cargar empleados")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Gestión de Empleados", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(12.dp))

        // 🔹 Mensaje de estado
        mensaje?.let { (tipo, texto) ->
            val color = if (tipo == "exito") Color(0xFFDFF2BF) else Color(0xFFFFBABA)
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(color)
                    .padding(8.dp)
            ) {
                Text(texto)
            }
            Spacer(Modifier.height(8.dp))
        }

        // 🔹 Tabla de empleados
        if (empleados.isEmpty()) {
            Text(
                text = if (empresaId.isNotEmpty()) "No hay empleados" else "Sin empresa asignada",
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            LazyColumn {
                item {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(Color.DarkGray)
                            .padding(8.dp)
                    ) {
                        listOf("Nombre", "Apellido", "Email", "Teléfono", "Código", "Rol", "Ciudad", "Acción")
                            .forEach { header ->
                                Text(
                                    text = header,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                    }
                }

                items(empleados) { empleado ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        listOf(
                            empleado.nombre,
                            empleado.apellido,
                            empleado.email,
                            empleado.telefono,
                            empleado.codigo,
                            empleado.rol,
                            empleado.ciudad
                        ).forEach { campo ->
                            Text(campo, modifier = Modifier.weight(1f))
                        }

                        Button(
                            onClick = { empleadoEditando = empleado.copy() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Editar")
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // 🔹 Formulario de edición
        empleadoEditando?.let { emp ->
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .background(Color(0xFFF0F0F0))
                    .padding(12.dp)
            ) {
                Text("Editando: ${emp.nombre}", fontWeight = FontWeight.Bold)

                listOf(
                    "nombre" to emp.nombre,
                    "apellido" to emp.apellido,
                    "email" to emp.email,
                    "telefono" to emp.telefono,
                    "direccion" to emp.direccion,
                    "codigo" to emp.codigo,
                    "rol" to emp.rol,
                    "fecha" to emp.fecha,
                    "ciudad" to emp.ciudad
                ).forEach { (campo, valor) ->
                    OutlinedTextField(
                        value = valor ?: "",
                        onValueChange = { nuevo ->
                            empleadoEditando = when (campo) {
                                "nombre" -> emp.copy(nombre = nuevo)
                                "apellido" -> emp.copy(apellido = nuevo)
                                "email" -> emp.copy(email = nuevo)
                                "telefono" -> emp.copy(telefono = nuevo)
                                "direccion" -> emp.copy(direccion = nuevo)
                                "codigo" -> emp.copy(codigo = nuevo)
                                "rol" -> emp.copy(rol = nuevo)
                                "fecha" -> emp.copy(fecha = nuevo)
                                "ciudad" -> emp.copy(ciudad = nuevo)
                                else -> emp
                            }
                        },
                        label = { Text(campo.replaceFirstChar { it.uppercase() }) },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    )
                }

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(onClick = {
                        scope.launch {
                            try {
                                // 🔹 Guardar cambios (PUT)
                                val url = URL("http://10.0.2.2:3000/api/personas/${emp.id}")
                                val conn = url.openConnection() as HttpURLConnection
                                conn.requestMethod = "PUT"
                                conn.setRequestProperty("Content-Type", "application/json")
                                conn.doOutput = true
                                conn.outputStream.write(
                                    """
                                    {
                                        "nombre": "${emp.nombre}",
                                        "apellido": "${emp.apellido}",
                                        "email": "${emp.email}",
                                        "telefono": "${emp.telefono}",
                                        "direccion": "${emp.direccion}",
                                        "codigo": "${emp.codigo}",
                                        "rol": "${emp.rol}",
                                        "fecha": "${emp.fecha}",
                                        "ciudad": "${emp.ciudad}"
                                    }
                                    """.trimIndent().toByteArray()
                                )

                                if (conn.responseCode == 200) {
                                    mensaje = "exito" to "Empleado actualizado correctamente ✅"
                                    empleadoEditando = null
                                    // 🔹 Recargar lista
                                    val reload = URL("http://10.0.2.2:3000/api/personas/empresa/$empresaId")
                                    val data = reload.openStream().bufferedReader().readText()
                                    val json = JSONArray(data)
                                    val lista = mutableListOf<Empleado>()
                                    for (i in 0 until json.length()) {
                                        val obj = json.getJSONObject(i)
                                        lista.add(
                                            Empleado(
                                                id = obj.getString("_id"),
                                                nombre = obj.getString("nombre"),
                                                apellido = obj.getString("apellido"),
                                                email = obj.getString("email"),
                                                telefono = obj.optString("telefono", ""),
                                                codigo = obj.optString("codigo", ""),
                                                rol = obj.optString("rol", ""),
                                                ciudad = obj.optString("ciudad", "")
                                            )
                                        )
                                    }
                                    empleados = lista
                                } else {
                                    mensaje = "error" to "Error al actualizar empleado"
                                }
                            } catch (e: Exception) {
                                mensaje = "error" to (e.message ?: "Error al guardar")
                            }
                        }
                    }) {
                        Text("Guardar")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = { empleadoEditando = null }) {
                        Text("Cancelar")
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(onClick = onVolver, colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)) {
            Text("← Volver al menú")
        }
    }
}
