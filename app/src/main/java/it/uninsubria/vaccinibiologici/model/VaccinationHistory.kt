package it.uninsubria.vaccinibiologici.model

enum class VaccinationHistory(val label: String) { // Rappresenta lo stato della documentazione vaccinale pregressa del paziente.
    COMPLETE("Completa e documentata"),
    INCOMPLETE("Incompleta"),
    UNKNOWN("Non disponibile / non documentata")
}