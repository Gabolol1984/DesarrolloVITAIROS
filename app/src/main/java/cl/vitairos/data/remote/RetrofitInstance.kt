package cl.vitairos.data.remote

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// [10] Singleton: arma UNA sola vez el cliente HTTP y el objeto Retrofit, y los reutiliza en toda la app
// (igual que TareasDatabase.obtener() armaba una única instancia de la base de datos en la Semana 11).
object RetrofitInstance {

    private const val URL_BASE = "https://jsonplaceholder.typicode.com/"

    // [11] Interceptor de registro: imprime cada petición y respuesta completa en Logcat (tag "OkHttp").
    // Level.BODY = método + URL + encabezados + el cuerpo JSON. Es el equivalente, para la red, del
    // Database Inspector que se usó con SQLite en la Semana 11: deja ver el tráfico real sin adivinar.
    private val interceptorLog = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // [12] OkHttpClient: el cliente HTTP real que abre la conexión. Retrofit NO habla con la red directamente;
    // le delega esa tarea a OkHttp, y aquí se le agrega el interceptor y los tiempos máximos de espera.
    private val cliente = OkHttpClient.Builder()
        .addInterceptor(interceptorLog)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    // [13] "by lazy": el objeto Retrofit (y la interfaz ApiService que genera) se crea la PRIMERA vez que
    // se usa RetrofitInstance.api, no antes. baseUrl fija el inicio de toda ruta declarada en ApiService.
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .client(cliente)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(ApiService::class.java)
    }
}