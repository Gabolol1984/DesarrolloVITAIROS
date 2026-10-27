package cl.vitairos.model

data class PerfilEspecialista(
    val correo: String = "especialista@vitairos.cl",
    val clave: String = "Especialista123",
    val nombre: String = "Dra. Camila Torres (demo)",
    val especialidad: String = "Psicología"
) {
    fun validar(correoIngresado: String, claveIngresada: String): Boolean {
        return correoIngresado.trim().equals(correo, ignoreCase = true) &&
                claveIngresada == clave
    }
}
