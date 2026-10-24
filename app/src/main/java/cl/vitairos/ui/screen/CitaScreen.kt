
package cl.vitairos.ui.screen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.util.Calendar

@Composable
fun CitaScreen(
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    val calendario = Calendar.getInstance()

    var profesional by remember {
        mutableStateOf("Seleccionar profesional")
    }
    var fecha by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }
    var menuAbierto by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }

    val profesionales = listOf(
        "psicologo 1",
        "psicologo 2"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Agendar una cita",
            style = MaterialTheme.typography.headlineMedium
        )

        Text("Selecciona un profesional")

        Box {
            OutlinedButton(
                onClick = { menuAbierto = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(profesional)
            }

            DropdownMenu(
                expanded = menuAbierto,
                onDismissRequest = { menuAbierto = false }
            ) {
                profesionales.forEach { nombre ->
                    DropdownMenuItem(
                        text = { Text(nombre) },
                        onClick = {
                            profesional = nombre
                            menuAbierto = false
                        }
                    )
                }
            }
        }

        Text("Selecciona el día")

        OutlinedButton(
            onClick = {
                DatePickerDialog(
                    context,
                    { _, year, month, day ->
                        fecha = "%04d-%02d-%02d".format(
                            year, month + 1, day
                        )
                    },
                    calendario.get(Calendar.YEAR),
                    calendario.get(Calendar.MONTH),
                    calendario.get(Calendar.DAY_OF_MONTH)
                ).apply {
                    datePicker.minDate =
                        System.currentTimeMillis()
                }.show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (fecha.isEmpty()) "Elegir fecha"
            else fecha)
        }

        Text("Selecciona la hora")

        OutlinedButton(
            onClick = {
                TimePickerDialog(
                    context,
                    { _, hour, minute ->
                        hora = "%02d:%02d".format(
                            hour, minute
                        )
                    },
                    9,
                    0,
                    true
                ).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (hora.isEmpty()) "Elegir hora"
            else hora)
        }

        Button(
            onClick = {
                mensaje = if (
                    profesional != "Seleccionar profesional" &&
                    fecha.isNotBlank() &&
                    hora.isNotBlank()
                ) {
                    "Datos seleccionados: $profesional, $fecha, $hora"
                } else {
                    "Completa el profesional, la fecha y la hora."
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Confirmar cita")
        }

        if (mensaje.isNotBlank()) {
            Text(mensaje)
        }

        OutlinedButton(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }
    }
}

