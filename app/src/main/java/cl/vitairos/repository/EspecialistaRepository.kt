package cl.vitairos.repository

import android.content.Context
import cl.vitairos.model.HorasDisponibles

/** Persistencia local de datos sintéticos para el MVP. */
class EspecialistaRepository(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("agenda_especialista_demo", Context.MODE_PRIVATE)

    fun cargar(): List<HorasDisponibles> = prefs.getString("horarios", "").orEmpty()
        .split(";").filter { it.isNotBlank() }.mapNotNull { linea ->
            val campos = linea.split("|")
            if (campos.size != 4) null else {
                val dia = campos[0].toIntOrNull()
                if (dia == null || dia !in 0..6) null else HorasDisponibles(dia, campos[1], campos[2], campos[3] == "true")
            }
        }

    fun guardar(horarios: List<HorasDisponibles>) {
        val texto = horarios.joinToString(";") { "${it.dia}|${it.inicio}|${it.fin}|${it.disponible}" }
        prefs.edit().putString("horarios", texto).apply()
    }
}
