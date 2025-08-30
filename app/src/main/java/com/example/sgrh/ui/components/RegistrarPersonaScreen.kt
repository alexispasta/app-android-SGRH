package com.example.sgrh.ui.components

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.sgrh.data.remote.ApiService
import com.example.sgrh.data.remote.RegistroPersona
import kotlinx.coroutines.launch
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrarPersonaScreen(
    apiService: ApiService,
    empresaId: String,
    onVolver: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var rol by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var ciudad by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    val scroll = rememberScrollState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scroll)
    ) {
        Text("Registrar Persona", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        val textFieldModifier = Modifier.fillMaxWidth()

        // Campos de texto
        OutlinedTextField(nombre, onValueChange = { nombre = it }, label = { Text("Nombres") }, modifier = textFieldModifier)
        OutlinedTextField(apellido, onValueChange = { apellido = it }, label = { Text("Apellidos") }, modifier = textFieldModifier)
        OutlinedTextField(email, onValueChange = { email = it }, label = { Text("Correo Electrónico") }, modifier = textFieldModifier)
        OutlinedTextField(password, onValueChange = { password = it }, label = { Text("Contraseña") }, modifier = textFieldModifier)
        OutlinedTextField(telefono, onValueChange = { telefono = it }, label = { Text("Teléfono") }, modifier = textFieldModifier)
        OutlinedTextField(direccion, onValueChange = { direccion = it }, label = { Text("Dirección") }, modifier = textFieldModifier)

        Spacer(Modifier.height(12.dp))

        // Dropdown para rol
        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = rol,
                onValueChange = {},
                readOnly = true,
                label = { Text("Rol de Empresa") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = textFieldModifier.menuAnchor()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                listOf("empleado", "rrhh", "gerente", "supervisor").forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item.replaceFirstChar { it.uppercase() }) },
                        onClick = {
                            rol = item
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Selector de fecha de nacimiento corregido
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                fechaNacimiento = "%04d-%02d-%02d".format(year, month + 1, dayOfMonth)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        OutlinedTextField(
            value = fechaNacimiento,
            onValueChange = {},
            readOnly = true,
            label = { Text("Fecha de Nacimiento") },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = "Seleccionar fecha",
                    modifier = Modifier.clickable { datePickerDialog.show() } // ícono clickeable
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { datePickerDialog.show() } // todo el campo clickeable
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(ciudad, onValueChange = { ciudad = it }, label = { Text("Ciudad") }, modifier = textFieldModifier)

        Spacer(Modifier.height(16.dp))

        // Botón de registrar
        Button(
            onClick = {
                mensaje = ""
                val nuevaPersona = RegistroPersona(
                    nombre = nombre,
                    apellido = apellido,
                    email = email,
                    password = password,
                    telefono = if (telefono.isEmpty()) null else telefono,
                    direccion = if (direccion.isEmpty()) null else direccion,
                    codigo = "",
                    rol = rol,
                    fecha = fechaNacimiento,
                    ciudad = if (ciudad.isEmpty()) null else ciudad,
                    empresaId = empresaId
                )



                scope.launch {
                    try {
                        // ✅ Aquí pasamos la variable, no la clase
                        val response = apiService.crearEmpleado(nuevaPersona)
                        mensaje = if (response.isSuccessful) {
                            // Limpiar campos
                            nombre = ""
                            apellido = ""
                            email = ""
                            password = ""
                            telefono = ""
                            direccion = ""
                            rol = ""
                            fechaNacimiento = ""
                            ciudad = ""
                            "✅ Persona registrada correctamente"
                        } else "❌ Error al registrar persona"
                    } catch (e: Exception) {
                        mensaje = "❌ ${e.message}"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Registrar Persona") }


        Spacer(Modifier.height(12.dp))

        if (mensaje.isNotEmpty()) {
            Text(
                text = mensaje,
                color = if (mensaje.startsWith("✅")) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.error
            )
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(onClick = onVolver, modifier = Modifier.fillMaxWidth()) {
            Text("← Volver al Menú")
        }
    }
}
