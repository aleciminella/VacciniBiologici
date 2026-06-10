package it.uninsubria.vaccinibiologici.model

data class PatientProfile(
    val therapy: BiologicalTherapy,
    val age: Int,
    val vaccinationHistory: VaccinationHistory,
    val conditions: Set<ClinicalCondition>
) {
    val isAdult: Boolean
        get() = age >= 18

    val therapyAlreadyStarted: Boolean
        get() = therapy != BiologicalTherapy.NONE &&
                ClinicalCondition.PLANNED_THERAPY_START !in conditions

    val hasHighRiskCondition: Boolean
        get() = conditions.any {
            it == ClinicalCondition.SEVERE_IMMUNODEFICIENCY ||
                    it == ClinicalCondition.CHRONIC_CARDIOPULMONARY_DISEASE ||
                    it == ClinicalCondition.DIABETES ||
                    it == ClinicalCondition.CHRONIC_KIDNEY_DISEASE ||
                    it == ClinicalCondition.CHRONIC_LIVER_DISEASE ||
                    it == ClinicalCondition.ASPLENIA
        }

    val hasIncompleteOrUnknownHistory: Boolean
        get() = vaccinationHistory != VaccinationHistory.COMPLETE

    val alteredImmunocompetence: Boolean
        get() = therapyAlreadyStarted ||
                therapy == BiologicalTherapy.OTHER_IMMUNOSUPPRESSOR ||
                ClinicalCondition.SEVERE_IMMUNODEFICIENCY in conditions
}