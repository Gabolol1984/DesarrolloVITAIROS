

package cl.vitairos.ui.screen

import android.Manifest
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.vitairos.model.HorasDisponibles
import cl.vitairos.notificacion.NotificacionHelper
import cl.vitairos.viewmodel.CitaViewModel
import java.util.Calendar
import java.util.Locale
import cl.vitairos.model.diasSemana

@Composable
fun CitaScreen(
    horarios: List<HorasDisponibles>,
    correoPaciente: String,
    onVolver: () -> Unit,
    citaViewModel: CitaViewModel = viewModel()
) {
    val context = LocalContext.current

    val horasOcupadas by citaViewModel.horasOcupadas.collectAsState()

    var profesional by remember {
        mutableStateOf("Seleccionar profesional")
    }
    var fecha by remember { mutableStateOf("") }
    var diaSeleccionado by remember { mutableIntStateOf(-1) }
    var horaSeleccionada by remember { mutableStateOf("") }
    var menuAbierto by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }

    // Los profesionales se obtienen de los horarios persistidos en SQLite.
    // Así no se ofrecen perfiles inventados que no tienen agenda publicada.
    val profesionales = remember(horarios) {
        horarios.map { it.especialistaId }.distinct().sorted()
    }

    // Solicitar permiso para mostrar notificaciones.
    val solicitarPermiso = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (!concedido) {
            mensaje = "Las notificaciones están desactivadas."
        }
    }

    LaunchedEffect(Unit) {
        NotificacionHelper.crearCanal(context)

        if (Build.VERSION.SDK_INT >= 33) {
            val permisoConcedido =
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

            if (!permisoConcedido) {
                solicitarPermiso.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }
    }

    // ACTUALIZAR LAS HORAS OCUPADAS CUANDO CAMBIA
    // EL ESPECIALISTA O LA FECHA.
    LaunchedEffect(profesional, fecha) {
        horaSeleccionada = ""

        if (
            profesional != "Seleccionar profesional" &&
            fecha.isNotBlank()
        ) {
            citaViewModel.cargarHorasOcupadas(
                profesional,
                fecha
            )
        } else {
            citaViewModel.cargarHorasOcupadas("", "")
        }
    }

    // Obtener los horarios configurados para ese especialista.
    val horariosDelDia = horarios.filter {
        it.especialistaId == profesional &&
                it.dia == diaSeleccionado &&
                it.disponible
    }

    // Generar horas de una hora de duración y excluir
    // aquellas que ya están reservadas.
    val horasDisponibles = remember(
        horariosDelDia,
        horasOcupadas
    ) {
        horariosDelDia
            .flatMap { intervalo ->
                val inicio = convertirMinutos(intervalo.inicio)
                val fin = convertirMinutos(intervalo.fin)

                if (inicio == null || fin == null || inicio >= fin) {
                    emptyList()
                } else {
                    generateSequence(inicio) { anterior ->
                        (anterior + 60).takeIf {
                            it + 60 <= fin
                        }
                    }.map { minutos ->
                        "%02d:%02d".format(
                            Locale.US,
                            minutos / 60,
                            minutos % 60
                        )
                    }.toList()
                }
            }
            .distinct()
            .sorted()
            .filterNot { it in horasOcupadas }
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
                if (profesionales.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("Aún no hay agendas publicadas") },
                        onClick = { menuAbierto = false },
                        enabled = false
                    )
                }
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

        if (profesional != "Seleccionar profesional") {
            Text(
                "Días y horarios de atención",
                style = MaterialTheme.typography.titleMedium
            )

            val publicados = horarios
                .filter { it.especialistaId == profesional && it.disponible }
                .sortedWith(compareBy({ it.dia }, { it.inicio }))

            if (publicados.isEmpty()) {
                Text("Este profesional aún no ha publicado horarios.")
            } else {
                publicados.forEach {
                    Text(
                        "${diasSemana.getOrElse(it.dia) { "Día" }}: " +
                                "${it.inicio} - ${it.fin}"
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
                            Locale.US,
                            year,
                            month + 1,
                            day
                        )

                        // Lunes = 0, domingo = 6.
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

        if (profesional == "Seleccionar profesional") {
            Text("Primero selecciona un profesional.")
        } else if (fecha.isEmpty()) {
            Text("Primero selecciona una fecha.")
        } else if (horasDisponibles.isEmpty()) {
            Text("No hay horarios disponibles para esta fecha.")
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
                when {
                    profesional == "Seleccionar profesional" -> {
                        mensaje = "Selecciona un profesional."
                    }

                    fecha.isBlank() -> {
                        mensaje = "Selecciona una fecha."
                    }

                    correoPaciente.isBlank() -> {
                        mensaje = "Debes iniciar sesión como paciente."
                    }

                    horaSeleccionada.isBlank() -> {
                        mensaje = "Selecciona una hora disponible."
                    }

                    horaSeleccionada !in horasDisponibles -> {
                        mensaje = "Esa hora ya no está disponible."
                    }

                    else -> {
                        // GUARDAR LA CITA EN SQLITE.
                        val guardada = citaViewModel.reservar(
                            correo = correoPaciente,
                            especialistaId = profesional,
                            fecha = fecha,
                            hora = horaSeleccionada
                        )

                        if (guardada) {
                            // Programar el recordatorio solamente
                            // después de guardar la cita.
                            NotificacionHelper.programarRecordatorio(
                                context = context,
                                profesional = profesional,
                                fecha = fecha,
                                hora = horaSeleccionada
                            )

                            mensaje = "Cita guardada correctamente."
                            horaSeleccionada = ""
                        } else {
                            citaViewModel.cargarHorasOcupadas(
                                profesional,
                                fecha
                            )

                            horaSeleccionada = ""
                            mensaje =
                                "No se pudo reservar. " +
                                        "Comprueba la fecha y las horas disponibles."
                        }
                    }
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