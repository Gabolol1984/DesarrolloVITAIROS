
package cl.vitairos.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.vitairos.model.HorasDisponibles

import cl.vitairos.ui.screen.HomeScreen
import cl.vitairos.ui.screen.LoginScreen
import cl.vitairos.ui.screen.CitaScreen
import cl.vitairos.ui.screen.LoginEspecialistaScreen
import cl.vitairos.ui.screen.PerfilEspecialistaScreen

import cl.vitairos.viewmodel.LoginViewModel
import cl.vitairos.viewmodel.EspecialistaViewModel

object Rutas {
    const val LOGIN = "login"
    const val HOME = "home"
    const val CITA = "cita"
    const val LOGIN_ESPECIALISTA = "login_especialista"
    const val PERFIL_ESPECIALISTA = "perfil_especialista"
}

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    val loginViewModel: LoginViewModel = viewModel()
    val especialistaViewModel: EspecialistaViewModel = viewModel()

    val estadoLogin by loginViewModel.estado.collectAsState()
    val horarios by especialistaViewModel.horarios.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Rutas.LOGIN
    ) {

        // LOGIN NORMAL
        composable(Rutas.LOGIN) {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginExitoso = {
                    navController.navigate(Rutas.HOME) {
                        popUpTo(Rutas.LOGIN) {
                            inclusive = true
                        }
                    }
                },
                onEspecialista = {
                    navController.navigate(
                        Rutas.LOGIN_ESPECIALISTA
                    )
                }
            )
        }

        // LOGIN ESPECIALISTA
        composable(Rutas.LOGIN_ESPECIALISTA) {
            LoginEspecialistaScreen(
                onIngresar = {
                    navController.navigate(
                        Rutas.PERFIL_ESPECIALISTA
                    ) {
                        popUpTo(Rutas.LOGIN_ESPECIALISTA) {
                            inclusive = true
                        }
                    }
                },
                onVolver = {
                    navController.popBackStack()
                }
            )
        }

        // PERFIL ESPECIALISTA
        composable(Rutas.PERFIL_ESPECIALISTA) {
            PerfilEspecialistaScreen(
                horarios = horarios,

                onAgregarHorario = { dia, inicio, fin ->
                    especialistaViewModel.agregarHorario(
                        dia, inicio, fin
                    )
                },

                onCambiarDisponibilidad = { horario ->
                    val indice = horarios.indexOf(horario)
                    if (indice >= 0) {
                        especialistaViewModel
                            .cambiarDisponibilidad(indice)
                    }
                },
                onEliminarHorario = { horario ->
                    val indice = horarios.indexOf(horario)
                    if (indice >= 0) {
                        especialistaViewModel.eliminar(indice)
                    }
                },

                onSalir = {
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.LOGIN) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        // HOME USUARIO NORMAL
        composable(Rutas.HOME) {
            HomeScreen(
                correo = estadoLogin.correo,

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

        // CITAS
        // CITAS
        composable(Rutas.CITA) {
            CitaScreen(
                horarios = horarios,
                onVolver = {
                    navController.popBackStack()
                }
            )
        }
   }
}