package it.uninsubria.vaccinibiologici.data

import it.uninsubria.vaccinibiologici.model.ClinicalScenario

interface ClinicalScenarioRepository { // Definisce quali operazioni si possono fare con gli scenari salvati
    suspend fun findAll(): List<ClinicalScenario>

    suspend fun save(scenario: ClinicalScenario): Long

    suspend fun deleteById(id: Long)
}
