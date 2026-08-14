package it.uninsubria.vaccinibiologici.data

import android.content.Context
import androidx.room.Room
import it.uninsubria.vaccinibiologici.data.local.AppDatabase

class AppContainer(context: Context) {
    private val database = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "vaccini_biologici.db"
    ).build()

    val clinicalScenarioRepository: ClinicalScenarioRepository =
        RoomClinicalScenarioRepository(database.savedScenarioDao())
}