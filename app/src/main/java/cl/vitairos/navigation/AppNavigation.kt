package cl.vitairos.navigation


import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.vitairos.ui.screen.HomeScreen
import cl.vitairos.ui.screen.LoginScreen
import cl.vitairos.viewmodel.LoginViewModel
import cl.vitairos.ui.screen.CitaScreen

object Rutas {
    const val LOGIN = "login"
    const val HOME = "home"
    const val CITA = "cita"
}

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val loginViewModel: LoginViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Rutas.LOGIN
    ) {


        composable(Rutas.LOGIN) {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginExitoso = {
                    navController.navigate(Rutas.HOME) {
                        popUpTo(Rutas.LOGIN) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Rutas.HOME) {
            HomeScreen(
                correo = loginViewModel.estado.value.correo,
                onAgendarCita = {
                    navController.navigate(Rutas.CITA)
                },
                onCerrarSesion = {
                    loginViewModel.cerrarSesion()

                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.HOME) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Rutas.CITA) {
            CitaScreen(
                onVolver = {
                    navController.popBackStack()
                }
            )
        }
    }
}
