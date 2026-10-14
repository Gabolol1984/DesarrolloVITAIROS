package cl.vitairos.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

// [14] COMPONENTE REUTILIZABLE: un campo de texto que ya sabe mostrar su error.
// No guarda nada: recibe el valor y AVISA los cambios con onChange (state hoisting).
@Composable
fun CampoTexto(
    valor: String,                                  // texto que se muestra
    error: String?,                                 // mensaje de error, o null si está bien
    label: String,                                  // etiqueta del campo ("Nombre", "Correo"...)
    onChange: (String) -> Unit,                     // [15] función que se llama cada vez que el usuario escribe
    tipoTeclado: KeyboardType = KeyboardType.Text   // [16] teclado a mostrar (por defecto, texto normal)
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onChange,
        label = { Text(label) },
        singleLine = true,                          // el campo ocupa una sola línea
        isError = error != null,                    // [17] si hay error, el borde y la etiqueta se ponen rojos
        supportingText = {
            // ?.let { } solo se ejecuta si error NO es null
            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        },
        keyboardOptions = KeyboardOptions(keyboardType = tipoTeclado),
        modifier = Modifier.fillMaxWidth()
    )
}