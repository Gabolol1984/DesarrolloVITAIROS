package cl.vitairos.model

data class HorasDisponibles(
    val dia: Int,
    val inicio: String,
    val fin: String,
    val disponible: Boolean = true
)

val diasSemana = listOf(
    "Lunes",
    "Martes",
    "Miércoles",
    "Jueves",
    "Viernes",
    "Sábado"
)
