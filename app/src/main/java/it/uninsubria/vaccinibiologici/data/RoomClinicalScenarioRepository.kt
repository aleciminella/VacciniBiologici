package it.uninsubria.vaccinibiologici.data

import it.uninsubria.vaccinibiologici.data.local.SavedScenarioDao
import it.uninsubria.vaccinibiologici.data.local.SavedScenarioEntity
import it.uninsubria.vaccinibiologici.model.ClinicalScenario

class RoomClinicalScenarioRepository(
    private val dao: SavedScenarioDao
) : ClinicalScenarioRepository {
    override suspend fun findAll(): List<ClinicalScenario> {
        return dao.findAll().map { it.toScenario() }
    }

    override suspend fun findById(id: Long): ClinicalScenario? {
        return dao.findById(id)?.toScenario()
    }

    override suspend fun save(scenario: ClinicalScenario): Long {
        return dao.save(SavedScenarioEntity.fromScenario(scenario))
    }

    override suspend fun deleteById(id: Long) {
        dao.deleteById(id)
    }
}