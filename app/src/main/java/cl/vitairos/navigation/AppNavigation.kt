package cl.vitairos.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.vitairos.ui.screen.LoginScreen
import cl.vitairos.ui.screen.ResumenScreen
import cl.vitairos.viewmodel.LoginViewModel

object Rutas {
    const val FORMULARIO = "formulario"
    const val RESUMEN = "resumen"
}

// [29] AppNavigation conecta las pantallas entre sí.
@Composable
fun AppNavigation() {

    // [30] NavController = sabe en qué pantalla estamos y permite ir a otra o volver.
    val navController = rememberNavController()

    // [31] VIEWMODEL COMPARTIDO: se crea UNA sola vez, AQUÍ, fuera del NavHost,
    // y se entrega a las DOS pantallas. Así ambas leen y modifican los mismos datos.
    val viewModel: LoginViewModel = viewModel()

    // [32] NavHost = el "escenario" que muestra la pantalla de la ruta actual.
    NavHost(navController = navController, startDestination = Rutas.FORMULARIO) {

        composable(Rutas.FORMULARIO) {
            LoginScreen(
                viewModel = viewModel,
                onEnviar = { navController.navigate(Rutas.RESUMEN) }
            )
        }

        composable(Rutas.RESUMEN) {
            ResumenScreen(
                viewModel = viewModel,
                onVolver = { navController.popBackStack() }   // [33] saca el resumen de la pila → vuelve al formulario
            )
        }
    }
}