package it.uninsubria.vaccinibiologici.rules

import it.uninsubria.vaccinibiologici.model.BiologicalTherapy
import it.uninsubria.vaccinibiologici.model.ClinicalCondition
import it.uninsubria.vaccinibiologici.model.PatientProfile

object ClinicalInputValidator { // Controllo degli errori
    fun validateAge(rawAge: String): Result<Int> {
        val age = rawAge.trim().toIntOrNull()
            ?: return Result.failure(IllegalArgumentException("Inserisci un'età numerica."))

        if (age !in 0..120) {
            return Result.failure(IllegalArgumentException("L'età deve essere compresa tra 0 e 120 anni."))
        }

        return Result.success(age)
    }

    fun validateProfile(profile: PatientProfile): Result<Unit> {
        if (profile.therapy == BiologicalTherapy.NONE &&
            ClinicalCondition.PLANNED_THERAPY_START in profile.conditions
        ) {
            return Result.failure(
                IllegalArgumentException("Seleziona una terapia biologica prima di indicare che è pianificata.")
            )
        }

        if (profile.age < 12 && ClinicalCondition.PREGNANCY in profile.conditions) {
            return Result.failure(
                IllegalArgumentException("La condizione gravidanza non è coerente con l'età inserita.")
            )
        }

        return Result.success(Unit)
    }
}