package cl.vitairos.repository

import cl.vitairos.data.remote.RetrofitInstance
import cl.vitairos.model.LoginUiState

// [14] REPOSITORIO: la única puerta de entrada a los datos para el ViewModel. Hoy habla con Retrofit;
// si mañana la fuente cambiara (otra API, una caché local), el ViewModel no tendría que saberlo.
class LoginRepository {

    private val api = RetrofitInstance.api

    // [15] READ — lista completa.
    suspend fun obtenerPublicaciones(): List<LoginUiState> = api.obtenerPublicaciones()

    // [16] READ — una publicación puntual (se usa al tocar una tarjeta para ver el detalle).
    suspend fun obtenerDetalle(id: Int): LoginUiState = api.obtenerPublicacion(id)

    // [17] CREATE.
    suspend fun crear(publicacion: Publicacion): Publicacion = api.crearPublicacion(publicacion)

    // [18] UPDATE.
    suspend fun actualizar(publicacion: Publicacion): Publicacion =
        api.actualizarPublicacion(publicacion.id, publicacion)

    // [19] DELETE.
    suspend fun eliminar(id: Int) {
        api.eliminarPublicacion(id)
    }
}