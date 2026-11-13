

package cl.vitairos.notification

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import cl.vitairos.notificacion.NotificacionHelper

class RecordatorioWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        val profesional =
            inputData.getString("profesional") ?: return Result.failure()

        val fecha =
            inputData.getString("fecha") ?: return Result.failure()

        val hora =
            inputData.getString("hora") ?: return Result.failure()

        val id = inputData.getInt("id", 1)

        NotificacionHelper.mostrarNotificacion(
            context = applicationContext,
            profesional = profesional,
            fecha = fecha,
            hora = hora,
            id = id
        )

        return Result.success()
    }
}
