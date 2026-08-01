package it.uninsubria.vaccinibiologici.rules

import it.uninsubria.vaccinibiologici.model.BiologicalTherapy
import it.uninsubria.vaccinibiologici.model.ClinicalCondition
import it.uninsubria.vaccinibiologici.model.PatientProfile
import it.uninsubria.vaccinibiologici.model.VaccinationHistory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ClinicalInputValidatorTest {
    @Test
    fun validateAgeAcceptsNumericAgeInClinicalRange() {
        val result = ClinicalInputValidator.validateAge("65")

        assertTrue(result.isSuccess)
        assertEquals(65, result.getOrNull())
    }

    @Test
    fun validateAgeRejectsNonNumericValue() {
        val result = ClinicalInputValidator.validateAge("sessantacinque")

        assertTrue(result.isFailure)
        assertEquals("Inserisci un'età numerica.", result.exceptionOrNull()?.message)
    }

    @Test
    fun validateAgeRejectsAgeOutsideClinicalRange() {
        val result = ClinicalInputValidator.validateAge("130")

        assertTrue(result.isFailure)
        assertEquals("L'età deve essere compresa tra 0 e 120 anni.", result.exceptionOrNull()?.message)
    }

    @Test
    fun validateProfileRejectsPlannedTherapyWithoutSelectedBiologicTherapy() {
        val profile = PatientProfile(
            therapy = BiologicalTherapy.NONE,
            age = 45,
            vaccinationHistory = VaccinationHistory.UNKNOWN,
            conditions = setOf(ClinicalCondition.PLANNED_THERAPY_START)
        )

        val result = ClinicalInputValidator.validateProfile(profile)

        assertTrue(result.isFailure)
        assertEquals(
            "Seleziona una terapia biologica prima di indicare che è pianificata.",
            result.exceptionOrNull()?.message
        )
    }

    @Test
    fun validateProfileRejectsPregnancyWhenAgeIsNotCoherent() {
        val profile = PatientProfile(
            therapy = BiologicalTherapy.ANTI_TNF,
            age = 10,
            vaccinationHistory = VaccinationHistory.COMPLETE,
            conditions = setOf(ClinicalCondition.PREGNANCY)
        )

        val result = ClinicalInputValidator.validateProfile(profile)

        assertTrue(result.isFailure)
        assertEquals(
            "La condizione gravidanza non è coerente con l'età inserita.",
            result.exceptionOrNull()?.message
        )
    }
}
