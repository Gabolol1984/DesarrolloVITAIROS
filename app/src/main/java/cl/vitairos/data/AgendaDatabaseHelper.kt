
package cl.vitairos.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import cl.vitairos.model.HorasDisponibles

class AgendaDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "vitairos_agenda.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE disponibilidad (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                especialista_id TEXT NOT NULL,
                dia INTEGER NOT NULL,
                hora_inicio TEXT NOT NULL,
                hora_fin TEXT NOT NULL,
                disponible INTEGER NOT NULL DEFAULT 1
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE citas (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                paciente_correo TEXT NOT NULL,
                especialista_id TEXT NOT NULL,
                fecha TEXT NOT NULL,
                hora TEXT NOT NULL,
                estado TEXT NOT NULL DEFAULT 'CONFIRMADA',
                UNIQUE(especialista_id, fecha, hora)
            )
        """.trimIndent())
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        // Agregar migraciones aquí si cambia la estructura.
    }

    fun obtenerHorarios(especialistaId: String): List<HorasDisponibles> {
        val lista = mutableListOf<HorasDisponibles>()

        readableDatabase.query(
            "disponibilidad",
            null,
            "especialista_id = ?",
            arrayOf(especialistaId),
            null, null,
            "dia ASC, hora_inicio ASC"
        ).use { cursor ->
            while (cursor.moveToNext()) {
                lista.add(
                    HorasDisponibles(
                        dia = cursor.getInt(
                            cursor.getColumnIndexOrThrow("dia")
                        ),
                        inicio = cursor.getString(
                            cursor.getColumnIndexOrThrow("hora_inicio")
                        ),
                        fin = cursor.getString(
                            cursor.getColumnIndexOrThrow("hora_fin")
                        ),
                        disponible = cursor.getInt(
                            cursor.getColumnIndexOrThrow("disponible")
                        ) == 1,
                        especialistaId = especialistaId
                    )
                )
            }
        }

        return lista
    }

    fun guardarHorario(
        especialistaId: String,
        horario: HorasDisponibles
    ) {
        val valores = ContentValues().apply {
            put("especialista_id", especialistaId)
            put("dia", horario.dia)
            put("hora_inicio", horario.inicio)
            put("hora_fin", horario.fin)
            put("disponible", if (horario.disponible) 1 else 0)
        }

        writableDatabase.insertOrThrow(
            "disponibilidad", null, valores
        )
    }

    fun eliminarHorario(
        especialistaId: String,
        horario: HorasDisponibles
    ) {
        writableDatabase.delete(
            "disponibilidad",
            "especialista_id = ? AND dia = ? AND hora_inicio = ? AND hora_fin = ?",
            arrayOf(
                especialistaId,
                horario.dia.toString(),
                horario.inicio,
                horario.fin
            )
        )
    }

    fun cambiarDisponibilidad(
        especialistaId: String,
        horario: HorasDisponibles
    ) {
        val valores = ContentValues().apply {
            put("disponible", if (horario.disponible) 0 else 1)
        }

        writableDatabase.update(
            "disponibilidad",
            valores,
            "especialista_id = ? AND dia = ? AND hora_inicio = ? AND hora_fin = ?",
            arrayOf(
                especialistaId,
                horario.dia.toString(),
                horario.inicio,
                horario.fin
            )
        )
    }

    fun horasReservadas(
        especialistaId: String,
        fecha: String
    ): Set<String> {
        val horas = mutableSetOf<String>()

        readableDatabase.query(
            "citas",
            arrayOf("hora"),
            "especialista_id = ? AND fecha = ? AND estado = ?",
            arrayOf(especialistaId, fecha, "CONFIRMADA"),
            null, null, null
        ).use { cursor ->
            while (cursor.moveToNext()) {
                horas.add(cursor.getString(0))
            }
        }

        return horas
    }

    fun reservarCita(
        correo: String,
        especialistaId: String,
        fecha: String,
        hora: String
    ): Boolean {
        val valores = ContentValues().apply {
            put("paciente_correo", correo.trim().lowercase())
            put("especialista_id", especialistaId)
            put("fecha", fecha)
            put("hora", hora)
            put("estado", "CONFIRMADA")
        }

        return try {
            writableDatabase.insertOrThrow(
                "citas", null, valores
            ) != -1L
        } catch (_: SQLiteConstraintException) {
            false
        }
    }
}
