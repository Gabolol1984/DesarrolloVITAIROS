

package cl.vitairos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import cl.vitairos.data.AgendaDatabaseHelper
import cl.vitairos.model.HorasDisponibles
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class EspecialistaViewModel(application: Application) :
    AndroidViewModel(application) {

    private val db = AgendaDatabaseHelper(application)

    // Debe coincidir con el identificador de la agenda del paciente.
    private val especialistaId = "psicologo 1"

    private val _horarios = MutableStateFlow(
        db.obtenerHorarios(especialistaId)
    )

    val horarios = _horarios.asStateFlow()

    fun agregarHorario(
        dia: Int,
        inicio: String,
        fin: String
    ): String? {
        val patron = Regex("""^([01]\d|2[0-3]):[0-5]\d$""")

        fun aMinutos(h: String) = h.substring(0, 2).toInt() * 60 + h.substring(3, 5).toInt()
        if (aMinutos(fin) - aMinutos(inicio) < 60) {
            return "El bloque debe durar al menos 60 minutos"
        }

        if (!patron.matches(inicio) || !patron.matches(fin)) {
            return "Utiliza el formato HH:mm"
        }

        if (inicio >= fin) {
            return "La hora inicial debe ser menor que la final"
        }

        val superpuesto = _horarios.value.any {
            it.dia == dia &&
                    inicio < it.fin &&
                    fin > it.inicio
        }

        if (superpuesto) {
            return "Este horario se superpone con otro existente"
        }

        val nuevo = HorasDisponibles(
            dia = dia,
            inicio = inicio,
            fin = fin,
            disponible = true,
            especialistaId = especialistaId
        )

        return try {
            db.guardarHorario(especialistaId, nuevo)
            recargar()
            null
        } catch (e: Exception) {
            "No se pudo guardar el horario: ${e.message}"
        }
    }

    fun cambiarDisponibilidad(indice: Int) {
        val horario = _horarios.value.getOrNull(indice) ?: return

        db.cambiarDisponibilidad(especialistaId, horario)
        recargar()
    }

    fun eliminar(indice: Int) {
        val horario = _horarios.value.getOrNull(indice) ?: return

        db.eliminarHorario(especialistaId, horario)
        recargar()
    }

    fun recargar() {
        _horarios.value = db.obtenerHorarios(especialistaId)
    }
}
