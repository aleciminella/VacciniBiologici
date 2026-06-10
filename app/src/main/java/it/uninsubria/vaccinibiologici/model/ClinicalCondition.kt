package it.uninsubria.vaccinibiologici.model

enum class ClinicalCondition(val label: String) {
    PLANNED_THERAPY_START("Terapia biologica pianificata ma non ancora iniziata"),
    SEVERE_IMMUNODEFICIENCY("Immunodeficienza severa o terapia immunosoppressiva associata"),
    CHRONIC_CARDIOPULMONARY_DISEASE("Patologia cronica cardio-polmonare"),
    DIABETES("Diabete o patologia metabolica"),
    CHRONIC_KIDNEY_DISEASE("Insufficienza renale cronica"),
    CHRONIC_LIVER_DISEASE("Epatopatia cronica"),
    ASPLENIA("Asplenia o deficit funzionale splenico"),
    PREGNANCY("Gravidanza")
}