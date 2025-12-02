package com.tuempresa.driverapp.data.local.dao // Tu package está bien

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy // Ahora sí lo vamos a usar
import androidx.room.Query
import com.tuempresa.driverapp.data.local.models.TripEvent
import kotlinx.coroutines.flow.Flow

@Dao
interface TripEventDao {

    /**
     * Inserta un nuevo evento. Si ya existe un evento con el mismo ID, lo reemplaza.
     * Usar OnConflictStrategy.REPLACE es más seguro.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE) // CAMBIO 1: Añadir estrategia de conflicto
    suspend fun insertEvent(event: TripEvent) // CAMBIO 2: Cambiar nombre para claridad (opcional pero recomendado)

    /**
     * Obtiene todos los eventos de un viaje específico y los emite como un Flow.
     * La pantalla escuchará este Flow y se actualizará automáticamente cuando se inserte un nuevo evento.
     * Ordenamos por 'timestamp' para que los eventos más recientes aparezcan primero.
     */
    @Query("SELECT * FROM trip_events WHERE tripId = :tripId ORDER BY timestamp DESC") // CAMBIO 3: Añadir ORDER BY
    fun getEventsForTrip(tripId: String): Flow<List<TripEvent>> // CAMBIO 4: Devolver Flow en lugar de List

    /**
     * Borra todos los eventos de la tabla. Puede ser útil para mantenimiento.
     */
    @Query("DELETE FROM trip_events")
    suspend fun deleteAll()

    // La función getEventsByTrip que tenías ya no es necesaria, porque getEventsForTrip(..) con Flow la reemplaza y mejora.
}
