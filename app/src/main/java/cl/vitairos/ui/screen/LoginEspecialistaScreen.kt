package cl.vitairos.ui.screen

import cl.vitairos.model.PerfilEspecialista
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun LoginEspecialistaScreen(onIngresar: () -> Unit, onVolver: () -> Unit) {
    var correo by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
        Text("Acceso de especialistas", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(20.dp))
        OutlinedTextField(correo, { correo = it; error = "" }, label = { Text("Correo del especialista") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(clave, { clave = it; error = "" }, label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(), singleLine = true, modifier = Modifier.fillMaxWidth())
        if (error.isNotEmpty()) Text(error, color = MaterialTheme.colorScheme.error)
        Spacer(Modifier.height(20.dp))
        Button(onClick = { if (PerfilEspecialista().validar(correo, clave)) onIngresar() else error = "Correo o contraseña incorrectos" }, modifier = Modifier.fillMaxWidth()) { Text("Ingresar como especialista") }
        TextButton(onClick = onVolver) { Text("Volver al inicio de sesión") }
        Text("Acceso ficticio de demostración", style = MaterialTheme.typography.bodySmall)
    }
}
