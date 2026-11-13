package cl.vitairos.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import cl.vitairos.model.LoginErrores
import cl.vitairos.model.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel : ViewModel() {
    private val _estado = MutableStateFlow(LoginUiState())

    val estado: StateFlow<LoginUiState> = _estado.asStateFlow()

    // Credenciales de prueba
    private val correoCorrecto = "admin@vitairos.cl"
    private val contrasenaCorrecta = "123456"

    fun cambiarCorreo(correo: String) {
        _estado.update {
            it.copy(
                correo = correo,
                errores = it.errores.copy(
                    correo = null,
                    general = null
                )
            )
        }
    }

    fun cambiarContrasena(contrasena: String) {
        _estado.update {
            it.copy(
                contrasena = contrasena,
                errores = it.errores.copy(
                    contrasena = null,
                    general = null
                )
            )
        }
    }

    fun iniciarSesion(): Boolean {
        val actual = _estado.value

        val errorCorreo = when {
            actual.correo.isBlank() ->
                "Ingresa tu correo electrónico"

            !android.util.Patterns.EMAIL_ADDRESS
                .matcher(actual.correo.trim()).matches() ->
                "Ingresa un correo válido"

            else -> null
        }

        val errorContrasena = when {
            actual.contrasena.isBlank() ->
                "Ingresa tu contraseña"

            else -> null
        }

        if (errorCorreo != null || errorContrasena != null) {
            _estado.update {
                it.copy(
                    errores = LoginErrores(
                        correo = errorCorreo,
                        contrasena = errorContrasena
                    )
                )
            }
            return false
        }

        val accesoCorrecto =
            actual.correo.trim().equals(
                correoCorrecto,
                ignoreCase = true
            ) && actual.contrasena == contrasenaCorrecta

        if (!accesoCorrecto) {
            _estado.update {
                it.copy(
                    errores = LoginErrores(
                        general = "Correo o contraseña incorrectos"
                    )
                )
            }
            return false
        }

        _estado.update {
            it.copy(errores = LoginErrores())
        }

        return true
    }

    fun cerrarSesion() {
        _estado.value = LoginUiState()
    }

    fun limpiarFormulario() {
        _estado.value = LoginUiState()
    }

}