package cl.vitairos.model

data class LoginErrores (
    val nombre: String? = null,
    val correo: String? = null,
    val aceptaTerminos: String? = null
){
    fun hayErrores(): Boolean {
        return nombre != null || correo != null || aceptaTerminos != null
    }
}
