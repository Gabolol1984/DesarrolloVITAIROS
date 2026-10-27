
package cl.vitairos.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.vitairos.model.PerfilEspecialista
import cl.vitairos.model.HorasDisponibles
import cl.vitairos.model.diasSemana

@Composable
fun PerfilEspecialistaScreen(
    horarios: List<HorasDisponibles>,
    onAgregarHorario: (Int, String, String) -> Unit,
    onCambiarDisponibilidad: (HorasDisponibles) -> Unit,
    onEliminarHorario: (HorasDisponibles) -> Unit,
    onSalir: () -> Unit
) {
    val especialista = remember { PerfilEspecialista() }

    val diasSeleccionados = remember {
        mutableStateListOf<Int>()
    }

    var horaInicio by remember {
        mutableStateOf("09:00")
    }

    var horaFin by remember {
        mutableStateOf("17:00")
    }

    var mensajeError by remember {
        mutableStateOf("")
    }

    // Solo lunes a sábado (índices 0 a 5)
    val diasAtencion = diasSemana.take(6)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {
            Text(
                text = "Perfil del especialista",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        item {
            Column {
                Text("Nombre: ${especialista.nombre}")
                Text("Especialidad: ${especialista.especialidad}")
                Text("Correo: ${especialista.correo}")
            }
        }

        item {
            Text(
                text = "Administrar disponibilidad",
                style = MaterialTheme.typography.titleLarge
            )
        }

        item {
            Text(
                text = "Selecciona los días de atención",
                style = MaterialTheme.typography.titleMedium
            )
        }

        // DÍAS EN DOS COLUMNAS
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                diasAtencion.indices.toList()
                    .chunked(2)
                    .forEach { fila ->

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.spacedBy(8.dp)
                        ) {
                            fila.forEach { index ->

                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment =
                                        Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked =
                                            index in diasSeleccionados,
                                        onCheckedChange = { seleccionado ->
                                            if (seleccionado) {
                                                if (index !in diasSeleccionados) {
                                                    diasSeleccionados.add(index)
                                                }
                                            } else {
                                                diasSeleccionados.remove(index)
                                            }
                                        }
                                    )

                                    Text(
                                        text = diasAtencion[index],
                                        style =
                                            MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
            }
        }

        item {
            OutlinedTextField(
                value = horaInicio,
                onValueChange = { horaInicio = it },
                label = {
                    Text("Hora de inicio (HH:mm)")
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = horaFin,
                onValueChange = { horaFin = it },
                label = {
                    Text("Hora de término (HH:mm)")
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (mensajeError.isNotEmpty()) {
            item {
                Text(
                    text = mensajeError,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        item {
            Button(
                onClick = {
                    val formato = Regex(
                        """^([01]\d|2[0-3]):[0-5]\d$"""
                    )

                    when {
                        diasSeleccionados.isEmpty() -> {
                            mensajeError =
                                "Selecciona al menos un día"
                        }

                        !formato.matches(horaInicio) ||
                                !formato.matches(horaFin) -> {
                            mensajeError =
                                "Utiliza el formato HH:mm"
                        }

                        horaInicio >= horaFin -> {
                            mensajeError =
                                "La hora inicial debe ser menor"
                        }

                        else -> {
                            diasSeleccionados.toList()
                                .forEach { dia ->
                                    onAgregarHorario(
                                        dia,
                                        horaInicio,
                                        horaFin
                                    )
                                }

                            mensajeError = ""
                            diasSeleccionados.clear()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar horarios seleccionados")
            }
        }

        item {
            HorizontalDivider()
        }

        item {
            Text(
                text = "Horarios registrados",
                style = MaterialTheme.typography.titleLarge
            )
        }

        if (horarios.isEmpty()) {
            item {
                Text("Todavía no hay horarios registrados")
            }
        }

        items(horarios) { horario ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${
                            diasSemana.getOrElse(horario.dia) {
                                "Día"
                            }
                        }: ${horario.inicio} - ${horario.fin}",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {
                        Text(
                            if (horario.disponible)
                                "Disponible"
                            else
                                "No disponible"
                        )

                        Spacer(
                            modifier = Modifier.width(12.dp)
                        )

                        Switch(
                            checked = horario.disponible,
                            onCheckedChange = {
                                onCambiarDisponibilidad(horario)
                            }
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            onEliminarHorario(horario)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Eliminar horario")
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onSalir,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar sesión")
            }
        }
    }
}
