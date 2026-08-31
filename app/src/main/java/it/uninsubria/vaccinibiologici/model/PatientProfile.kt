package it.uninsubria.vaccinibiologici.model

data class PatientProfile( // raggruppa dati di un paziente
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

    val hasHighRiskCondition: Boolean // Identifica se il paziente ha almeno una condizione clinica che lo espone a un rischio elevato di infezioni
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

    val alteredImmunocompetence: Boolean // indica se il paziente è attualmente compromesso o indebolito. Accade se la terapia è già iniziata, se assume altri immunosoppressori o se ha una immunodeficienza severa nota.
        get() = therapyAlreadyStarted ||
                therapy == BiologicalTherapy.OTHER_IMMUNOSUPPRESSOR ||
                ClinicalCondition.SEVERE_IMMUNODEFICIENCY in conditions
}