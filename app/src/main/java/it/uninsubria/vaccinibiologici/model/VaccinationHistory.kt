package it.uninsubria.vaccinibiologici.model

enum class VaccinationHistory(val label: String) {
    COMPLETE("Completa e documentata"),
    INCOMPLETE("Incompleta"),
    UNKNOWN("Non disponibile / non documentata")
}