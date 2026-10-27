
package cl.vitairos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import cl.vitairos.data.AgendaDatabaseHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CitaViewModel(application: Application) :
    AndroidViewModel(application) {

    private val db = AgendaDatabaseHelper(application)

    private val _horasOcupadas =
        MutableStateFlow<Set<String>>(emptySet())

    val horasOcupadas = _horasOcupadas.asStateFlow()

    fun cargarHorasOcupadas(
        especialistaId: String,
        fecha: String
    ) {
        _horasOcupadas.value =
            if (fecha.isBlank()) emptySet()
            else db.horasReservadas(especialistaId, fecha)
    }

    fun reservar(
        correo: String,
        especialistaId: String,
        fecha: String,
        hora: String
    ): Boolean {
        if (correo.isBlank()) return false

        val formato = SimpleDateFormat(
            "yyyy-MM-dd HH:mm",
            Locale.US
        ).apply { isLenient = false }

        val instante = runCatching {
            formato.parse("$fecha $hora")?.time
        }.getOrNull() ?: return false

        if (instante <= System.currentTimeMillis()) return false

        val calendario = Calendar.getInstance().apply {
            timeInMillis = instante
        }
        val dia = (calendario.get(Calendar.DAY_OF_WEEK) + 5) % 7
        val minutos = hora.substring(0, 2).toInt() * 60 +
                hora.substring(3, 5).toInt()

        val horarioValido = db.obtenerHorarios(especialistaId).any {
            if (!it.disponible || it.dia != dia) {
                false
            } else {
                val inicio = it.inicio.substring(0, 2).toInt() * 60 +
                        it.inicio.substring(3, 5).toInt()
                val fin = it.fin.substring(0, 2).toInt() * 60 +
                        it.fin.substring(3, 5).toInt()

                minutos >= inicio &&
                        minutos + 60 <= fin &&
                        (minutos - inicio) % 60 == 0
            }
        }

        if (!horarioValido) return false

        val resultado = db.reservarCita(
            correo, especialistaId, fecha, hora
        )

        cargarHorasOcupadas(especialistaId, fecha)
        return resultado
    }
}


