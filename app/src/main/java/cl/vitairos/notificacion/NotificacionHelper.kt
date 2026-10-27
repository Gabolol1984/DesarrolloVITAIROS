package cl.vitairos.notificacion

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit
import cl.vitairos.notification.RecordatorioWorker
import com.example.desarrollovitairos.R



object NotificacionHelper {

    private const val CANAL_ID = "recordatorios_citas"

    fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CANAL_ID,
                "Recordatorios de citas",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Avisos de próximas citas médicas"
            }

            val manager = context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

            manager.createNotificationChannel(canal)
        }
    }

    fun programarRecordatorio(
        context: Context,
        profesional: String,
        fecha: String,
        hora: String
    ) {
        // Interpretar la fecha y hora de la cita.
        val formato = SimpleDateFormat(
            "yyyy-MM-dd HH:mm",
            Locale.US
        ).apply {
            isLenient = false
        }

        val fechaCita = try {
            formato.parse("$fecha $hora")?.time
        } catch (_: Exception) {
            null
        } ?: return

        // Recordar 24 horas antes.
        val momentoRecordatorio =
            fechaCita - TimeUnit.HOURS.toMillis(24)

        val retraso = momentoRecordatorio -
                System.currentTimeMillis()

        // No programar recordatorios para citas pasadas.
        if (fechaCita <= System.currentTimeMillis()) return

        val datos = Data.Builder()
            .putString("profesional", profesional)
            .putString("fecha", fecha)
            .putString("hora", hora)
            .putInt("id", "$fecha-$hora-$profesional".hashCode())
            .build()

        val solicitud =
            OneTimeWorkRequestBuilder<RecordatorioWorker>()
                .setInitialDelay(
                    retraso.coerceAtLeast(0L),
                    TimeUnit.MILLISECONDS
                )
                .setInputData(datos)
                .build()

        val nombreUnico = "cita_${fecha}_${hora}_$profesional"

        WorkManager.getInstance(context).enqueueUniqueWork(
            nombreUnico,
            androidx.work.ExistingWorkPolicy.REPLACE,
            solicitud
        )
    }

    fun mostrarNotificacion(
        context: Context,
        profesional: String,
        fecha: String,
        hora: String,
        id: Int
    ) {
        crearCanal(context)

        if (
            Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val notificacion = NotificationCompat.Builder(
            context,
            CANAL_ID
        )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Recordatorio de tu cita")
            .setContentText(
                "Tienes una cita con $profesional el $fecha a las $hora."
            )
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    "Recuerda tu cita con $profesional.\n" +
                            "Fecha: $fecha\nHora: $hora"
                )
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context)
            .notify(id, notificacion)
    }
}
