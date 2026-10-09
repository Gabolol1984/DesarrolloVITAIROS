package cl.vitairos.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cl.vitairos.ui.components.CampoTexto
import cl.vitairos.viewmodel.LoginViewModel

// [18] PANTALLA 1: el formulario. Recibe el ViewModel (para leer el estado y avisar eventos)
// y una función onEnviar que se ejecuta cuando el formulario es válido.
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onEnviar: () -> Unit
) {
    // [19] collectAsState() convierte el StateFlow en estado de Compose.
    // Cada vez que el ViewModel cambia el estado, esta pantalla se vuelve a dibujar sola.
    val estado by viewModel.estado.collectAsState()

    // [20] Scaffold = el "esqueleto" de la pantalla. innerPadding evita que el contenido
    // quede debajo de la barra de estado o de la barra de navegación del teléfono.
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),   // permite desplazar si aparece el teclado
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Registro de estudiante", style = MaterialTheme.typography.headlineSmall)

            // [21] Cada campo muestra un dato del estado y AVISA al ViewModel cuando cambia.
            CampoTexto(
                valor = estado.nombre,
                error = estado.errores.nombre,
                label = "Nombre completo",
                onChange = { nuevo -> viewModel.onNombreChange(nuevo) }
            )

            CampoTexto(
                valor = estado.correo,
                error = estado.errores.correo,
                label = "Correo electrónico",
                onChange = { nuevo -> viewModel.onCorreoChange(nuevo) },
                tipoTeclado = KeyboardType.Email           // teclado con @ a la vista
            )

            // [22] Checkbox + texto en una misma fila (Row), centrados verticalmente.
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = estado.aceptaTerminos,
                    onCheckedChange = { marcado -> viewModel.onAceptaTerminosChange(marcado) }
                )
                Text("Acepto los términos y condiciones")
            }
            // El error del checkbox se muestra debajo, solo si existe.
            estado.errores.aceptaTerminos?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            // [23] Botón Enviar: PRIMERO valida; SOLO si todo está bien, navega al resumen.
            Button(
                onClick = {
                    if (viewModel.validarFormulario()) {
                        onEnviar()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enviar")
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            // [24] VISTA PREVIA EN TIEMPO REAL: lee el MISMO estado que los campos,
            // por eso cambia letra por letra mientras escribes, sin presionar ningún botón.
            Text("Vista previa en tiempo real", style = MaterialTheme.typography.titleMedium)
            Text("Nombre: ${estado.nombre}")
            Text("Correo: ${estado.correo}")
            Text("Términos aceptados: ${if (estado.aceptaTerminos) "Sí" else "No"}")
        }
    }
}