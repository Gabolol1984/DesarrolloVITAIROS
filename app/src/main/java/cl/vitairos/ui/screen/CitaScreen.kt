
package cl.vitairos.ui.screen

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cl.vitairos.model.HorasDisponibles
import java.util.Calendar
import java.util.Locale

@Composable
fun CitaScreen(
    horarios: List<HorasDisponibles>,
    onVolver: () -> Unit
) {
    val context = LocalContext.current

    var profesional by remember {
        mutableStateOf("Seleccionar profesional")
    }
    var fecha by remember { mutableStateOf("") }
    var diaSeleccionado by remember { mutableIntStateOf(-1) }
    var horaSeleccionada by remember { mutableStateOf("") }
    var menuAbierto by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }

    val profesionales = listOf("psicologo 1", "psicologo 2")

    // Los días del modelo van de lunes (0) a domingo (6).
    val horariosDelDia = horarios.filter {
        it.dia == diaSeleccionado && it.disponible
    }

    // Generar bloques de 60 minutos dentro de cada intervalo.
    val horasDisponibles = remember(horariosDelDia) {
        horariosDelDia.flatMap { intervalo ->
            val inicio = convertirMinutos(intervalo.inicio)
            val fin = convertirMinutos(intervalo.fin)

            if (inicio == null || fin == null || inicio >= fin) {
                emptyList()
            } else {
                generateSequence(inicio) { anterior ->
                    (anterior + 60).takeIf { it + 60 <= fin }
                }.map { minutos ->
                    "%02d:%02d".format(
                        Locale.US,
                        minutos / 60,
                        minutos % 60
                    )
                }.toList()
            }
        }.distinct().sorted()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Agendar una cita",
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
                            fecha = ""
                            diaSeleccionado = -1
                            horaSeleccionada = ""
                            mensaje = ""
                        }
                    )
                }
            }
        }

        Text("Selecciona el día")

        OutlinedButton(
            onClick = {
                val calendario = Calendar.getInstance()

                DatePickerDialog(
                    context,
                    { _, year, month, day ->
                        val seleccion = Calendar.getInstance().apply {
                            set(year, month, day)
                        }

                        fecha = "%04d-%02d-%02d".format(
                            Locale.US, year, month + 1, day
                        )

                        diaSeleccionado =
                            (seleccion.get(Calendar.DAY_OF_WEEK) + 5) % 7

                        horaSeleccionada = ""
                        mensaje = ""
                    },
                    calendario.get(Calendar.YEAR),
                    calendario.get(Calendar.MONTH),
                    calendario.get(Calendar.DAY_OF_MONTH)
                ).apply {
                    datePicker.minDate =
                        Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, 0)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }.timeInMillis
                }.show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (fecha.isEmpty()) "Elegir fecha" else fecha)
        }

        Text(
            "Horarios disponibles",
            style = MaterialTheme.typography.titleMedium
        )

        if (fecha.isEmpty()) {
            Text("Primero selecciona una fecha.")
        } else if (horasDisponibles.isEmpty()) {
            Text("El especialista no tiene horarios disponibles este día.")
        } else {
            horasDisponibles.forEach { hora ->
                val seleccionado = horaSeleccionada == hora

                OutlinedButton(
                    onClick = {
                        horaSeleccionada = hora
                        mensaje = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (seleccionado) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    )
                ) {
                    Text(
                        if (seleccionado) "$hora - Seleccionada"
                        else hora
                    )
                }
            }
        }

        Button(
            onClick = {
                mensaje = when {
                    profesional == "Seleccionar profesional" ->
                        "Selecciona un profesional."

                    fecha.isBlank() ->
                        "Selecciona una fecha."

                    horaSeleccionada.isBlank() ->
                        "Selecciona una hora disponible."

                    horaSeleccionada !in horasDisponibles ->
                        "El horario seleccionado ya no está disponible."

                    else ->
                        "Datos seleccionados:\n" +
                                "Profesional: $profesional\n" +
                                "Fecha: $fecha\n" +
                                "Hora: $horaSeleccionada"
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

private fun convertirMinutos(hora: String): Int? {
    val partes = hora.split(":")
    if (partes.size != 2) return null

    val horas = partes[0].toIntOrNull() ?: return null
    val minutos = partes[1].toIntOrNull() ?: return null

    if (horas !in 0..23 || minutos !in 0..59) return null

    return horas * 60 + minutos
}
