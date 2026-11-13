package cl.vitairos.data.remote

import cl.vitairos.model.HorasDisponibles
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


//se crea interface apiservice conectada a model.horasdisponibles, tieene metodos CRUD
interface ApiService {
    // [5] GET /posts?_limit=20 — lista de publicaciones. @Query agrega "?_limit=20" al final de la URL.
    // suspend: la función se ejecuta dentro de una corrutina y no bloquea el hilo que la llama (ver Parte II).
    @GET("posts")
    suspend fun obtenerPublicaciones(@Query("_limit") limite: Int = 20): List<HorasDisponibles>

    // [6] GET /posts/{id} — una publicación puntual. @Path reemplaza {id} en la URL por el valor recibido.
    @GET("posts/{id}")
    suspend fun obtenerPublicacion(@Path("id") id: Int): HorasDisponibles

    // [7] POST /posts — crea una publicación. @Body serializa el objeto completo como el cuerpo JSON de la petición.
    @POST("posts")
    suspend fun crearPublicacion(@Body HorasDisponibles: HorasDisponibles): HorasDisponibles

    // [8] PUT /posts/{id} — reemplaza una publicación existente por completo. Combina @Path (A QUIÉN)
    // y @Body (CON QUÉ la reemplaza).
    @PUT("posts/{id}")
    suspend fun actualizarPublicacion(@Path("id") id: Int, @Body HorasDisponibles: HorasDisponibles): HorasDisponibles

    // [9] DELETE /posts/{id} — elimina una publicación. Response<Unit> expone el código de estado HTTP
    // sin exigir que el cuerpo de la respuesta tenga una forma JSON concreta (aquí llega "{}").
    @DELETE("posts/{id}")
    suspend fun eliminarPublicacion(@Path("id") id: Int): Response<Unit>

}