package cl.vitairos.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import cl.vitairos.model.LoginErrores
import cl.vitairos.model.LoginUiState

// [5] VIEWMODEL: guarda el estado del formulario y contiene TODA la lógica (validaciones).
// Hereda de ViewModel(), por eso los datos NO se pierden al girar el teléfono.
class LoginViewModel : ViewModel() {

    // [6] Estado PRIVADO y modificable: solo el ViewModel puede cambiarlo.
    private val _estado = MutableStateFlow(LoginUiState())

    // [7] Estado PÚBLICO de solo lectura: las pantallas lo observan, pero no lo pueden modificar.
    val estado: StateFlow<LoginUiState> = _estado.asStateFlow()

    // [8] EVENTOS: la pantalla avisa "el usuario escribió algo" y el ViewModel actualiza el estado.
    // copy() crea una COPIA del estado cambiando solo lo indicado.
    // Además se borra el error de ese campo: si el usuario está corrigiendo, el mensaje rojo desaparece.
    fun onNombreChange(valor: String) {
        _estado.update {
            it.copy(nombre = valor, errores = it.errores.copy(nombre = null))
        }
    }

    fun onCorreoChange(valor: String) {
        _estado.update {
            it.copy(correo = valor, errores = it.errores.copy(correo = null))
        }
    }

    fun onAceptaTerminosChange(valor: Boolean) {
        _estado.update {
            it.copy(aceptaTerminos = valor, errores = it.errores.copy(aceptaTerminos = null))
        }
    }

    // [9] VALIDACIÓN: revisa las reglas de cada campo y guarda los mensajes de error.
    // Devuelve true si el formulario está correcto (sin errores).
    fun validarFormulario(): Boolean {
        val actual = _estado.value   // [10] .value = la "foto" actual del estado

        // [11] when { } elige el PRIMER caso que se cumple, de arriba hacia abajo.
        val errores = LoginErrores(
            nombre = when {
                actual.nombre.isBlank() -> "El nombre es obligatorio"
                actual.nombre.trim().length < 3 -> "El nombre debe tener al menos 3 letras"
                else -> null
            },
            correo = when {
                actual.correo.isBlank() -> "El correo es obligatorio"
                !actual.correo.contains("@") -> "El correo debe contener @"
                else -> null
            },
            aceptaTerminos = if (!actual.aceptaTerminos) "Debes aceptar los términos" else null
        )

        // [12] Se guardan los errores en el estado → la pantalla los dibuja en rojo automáticamente.
        _estado.update { it.copy(errores = errores) }

        return !errores.hayErrores()
    }

    // [13] Vuelve el formulario a su estado inicial (todo vacío y sin errores).
    fun limpiarFormulario() {
        _estado.value = LoginUiState()
    }
}