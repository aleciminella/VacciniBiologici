package it.uninsubria.vaccinibiologici.data

import android.content.Context
import androidx.room.Room
import it.uninsubria.vaccinibiologici.data.local.AppDatabase

class AppContainer(context: Context) { // Crea una sola volta il database e il Repository e li tiene pronti per essere usati da tutta l'app.
    private val database = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "vaccini_biologici.db"
    ).build()

    val clinicalScenarioRepository: ClinicalScenarioRepository =
        RoomClinicalScenarioRepository(database.savedScenarioDao())
}