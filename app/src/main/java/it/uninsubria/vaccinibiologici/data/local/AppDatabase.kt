package it.uninsubria.vaccinibiologici.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database( // Punto di accesso principale al database. Definisce la versione del database (1) e quali tabelle (Entity) contiene.
    entities = [SavedScenarioEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun savedScenarioDao(): SavedScenarioDao
}