// ruta: C:/.../com/tuempresa/driverapp/data/local/db/dao/ScoreDao.kt
package com.tuempresa.driverapp.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tuempresa.driverapp.data.local.models.DriverScore
import kotlinx.coroutines.flow.Flow

@Dao
interface ScoreDao {

    /**
     * Inserta o actualiza una puntuación.
     * Usamos OnConflictStrategy.REPLACE para que si ya existe una fila con id=1,
     * simplemente se reemplace con los nuevos datos.
     */
    // CAMBIO: Quitado el ".Companion" para una sintaxis más limpia.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(score: DriverScore)

    /**
     * Obtiene la puntuación como un Flow.
     * La UI que observe este Flow se actualizará automáticamente cada vez que
     * la puntuación cambie en la base de datos.
     */
    @Query("SELECT * FROM driver_score WHERE id = 1")
    fun getScoreFlow(): Flow<DriverScore?>

    /**
     * Obtiene la puntuación actual una sola vez.
     * Es una función 'suspend' para ser llamada desde una corrutina, ideal para
     * obtener el valor actual antes de modificarlo.
     */
    @Query("SELECT * FROM driver_score WHERE id = 1")
    suspend fun getCurrentScore(): DriverScore?

}
