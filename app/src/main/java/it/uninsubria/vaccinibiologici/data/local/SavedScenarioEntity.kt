package it.uninsubria.vaccinibiologici.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import it.uninsubria.vaccinibiologici.model.BiologicalTherapy
import it.uninsubria.vaccinibiologici.model.ClinicalCondition
import it.uninsubria.vaccinibiologici.model.ClinicalScenario
import it.uninsubria.vaccinibiologici.model.PatientProfile
import it.uninsubria.vaccinibiologici.model.VaccinationHistory

@Entity(tableName = "saved_scenarios")
data class SavedScenarioEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val therapy: String,
    val age: Int,
    val vaccinationHistory: String,
    val conditions: String,
    val reportSummary: String?,
    val createdAtMillis: Long
) {
    fun toScenario(): ClinicalScenario {
        return ClinicalScenario(
            id = id,
            title = title,
            profile = PatientProfile(
                therapy = BiologicalTherapy.valueOf(therapy),
                age = age,
                vaccinationHistory = VaccinationHistory.valueOf(vaccinationHistory),
                conditions = conditions.toConditionSet()
            ),
            report = null,
            createdAtMillis = createdAtMillis
        )
    }

    companion object {
        fun fromScenario(scenario: ClinicalScenario): SavedScenarioEntity {
            return SavedScenarioEntity(
                id = scenario.id,
                title = scenario.title,
                therapy = scenario.profile.therapy.name,
                age = scenario.profile.age,
                vaccinationHistory = scenario.profile.vaccinationHistory.name,
                conditions = scenario.profile.conditions.joinToString("|") { it.name },
                reportSummary = scenario.report?.summary,
                createdAtMillis = scenario.createdAtMillis
            )
        }
    }
}

private fun String.toConditionSet(): Set<ClinicalCondition> {
    if (isBlank()) return emptySet()
    return split("|").mapNotNull { name ->
        runCatching { ClinicalCondition.valueOf(name) }.getOrNull()
    }.toSet()
}