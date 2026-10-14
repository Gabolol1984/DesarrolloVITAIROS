package cl.vitairos.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.vitairos.viewmodel.LoginViewModel

@Composable
fun ResumenScreen(
    viewModel: LoginViewModel,
    onVolver: () -> Unit
) {
    val estado by viewModel.estado.collectAsState()

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("¡Registro exitoso!", style = MaterialTheme.typography.headlineSmall)

            // [26] Card = tarjeta con fondo y bordes redondeados para agrupar información.
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Nombre: ${estado.nombre}")
                    Text("Correo: ${estado.correo}")
                    Text("Aceptó términos: ${if (estado.aceptaTerminos) "Sí" else "No"}")
                }
            }

            // [27] Limpia el formulario en el ViewModel y vuelve a la pantalla anterior.
            Button(
                onClick = {
                    viewModel.limpiarFormulario()
                    onVolver()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrar otro estudiante")
            }
        }
    }
}