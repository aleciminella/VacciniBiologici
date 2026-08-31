package it.uninsubria.vaccinibiologici.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SavedScenarioDao {
    @Query("SELECT * FROM saved_scenarios ORDER BY createdAtMillis DESC")
    suspend fun findAll(): List<SavedScenarioEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(scenario: SavedScenarioEntity): Long

    @Query("DELETE FROM saved_scenarios WHERE id = :id")
    suspend fun deleteById(id: Long)
}
