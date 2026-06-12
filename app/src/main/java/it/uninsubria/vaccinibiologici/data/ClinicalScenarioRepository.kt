package it.uninsubria.vaccinibiologici.data

import it.uninsubria.vaccinibiologici.model.ClinicalScenario

interface ClinicalScenarioRepository {
    suspend fun findAll(): List<ClinicalScenario>

    suspend fun findById(id: Long): ClinicalScenario?

    suspend fun save(scenario: ClinicalScenario): Long

    suspend fun deleteById(id: Long)
}