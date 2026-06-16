package it.uninsubria.vaccinibiologici.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [SavedScenarioEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun savedScenarioDao(): SavedScenarioDao
}