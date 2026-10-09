package cl.vitairos.model

data class LoginUiState(
    val nombre: String = "",                          // lo que el usuario escribió en "Nombre"
    val correo: String = "",                          // lo que el usuario escribió en "Correo"
    val aceptaTerminos: Boolean = false,              // ¿marcó la casilla de términos?
    val errores: LoginErrores = LoginErrores()  // [4] submodelo con los errores de cada campo
)