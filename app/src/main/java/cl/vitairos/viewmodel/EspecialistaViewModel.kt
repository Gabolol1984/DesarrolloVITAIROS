
package cl.vitairos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import cl.vitairos.model.HorasDisponibles
import cl.vitairos.repository.EspecialistaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class EspecialistaViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repo = EspecialistaRepository(application)

    private val _horarios = MutableStateFlow(repo.cargar())

    val horarios = _horarios.asStateFlow()

    // AGREGAR UN HORARIO
    fun agregarHorario(
        dia: Int,
        inicio: String,
        fin: String
    ): String? {

        val patron = Regex(
            """^([01]\d|2[0-3]):[0-5]\d$"""
        )

        if (dia !in 0..6) {
            return "Selecciona un día válido"
        }

        if (!patron.matches(inicio) ||
            !patron.matches(fin)
        ) {
            return "Utiliza el formato HH:mm"
        }

        if (inicio >= fin) {
            return "La hora inicial debe ser menor que la final"
        }

        val existeSuperposicion = _horarios.value.any {
            it.dia == dia &&
                    inicio < it.fin &&
                    fin > it.inicio
        }

        if (existeSuperposicion) {
            return "Este horario se superpone con otro existente"
        }

        val nuevoHorario = HorasDisponibles(
            dia = dia,
            inicio = inicio,
            fin = fin,
            disponible = true
        )

        actualizar(_horarios.value + nuevoHorario)

        return null
    }

    // CAMBIAR DISPONIBILIDAD
    fun cambiarDisponibilidad(indice: Int) {

        if (indice !in _horarios.value.indices) {
            return
        }

        val nuevosHorarios = _horarios.value.mapIndexed {
                i, horario ->

            if (i == indice) {
                horario.copy(
                    disponible = !horario.disponible
                )
            } else {
                horario
            }
        }

        actualizar(nuevosHorarios)
    }

    // ELIMINAR UN HORARIO
    fun eliminar(indice: Int) {

        if (indice !in _horarios.value.indices) {
            return
        }

        val nuevosHorarios = _horarios.value.filterIndexed {
                i, _ -> i != indice
        }

        actualizar(nuevosHorarios)
    }

    // ACTUALIZAR Y GUARDAR HORARIOS
    private fun actualizar(
        nuevos: List<HorasDisponibles>
    ) {

        _horarios.value = nuevos.sortedWith(
            compareBy(
                { it.dia },
                { it.inicio }
            )
        )

        repo.guardar(_horarios.value)
    }
}
