package cl.vitairos.model


data class LoginUiState(
    val correo: String = "",
    val contrasena: String = "",
    val nombre: String = "",
    val aceptaTerminos: Boolean = false,
    val errores: LoginErrores = LoginErrores()
)
