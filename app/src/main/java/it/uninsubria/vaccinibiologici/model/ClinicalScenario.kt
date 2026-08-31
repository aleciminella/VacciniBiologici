package it.uninsubria.vaccinibiologici.model

data class ClinicalScenario( // Rappresenta uno scenario clinico salvato dall'utente nel database.
    val id: Long = 0,
    val title: String,
    val profile: PatientProfile,
    val report: RecommendationReport? = null,
    val createdAtMillis: Long = System.currentTimeMillis()
) {
    val displayTitle: String // Restituisce il titolo inserito o ne genera uno automatico basato sulla terapia e l'età.
        get() = title.ifBlank {
            "Scenario ${profile.therapy.label} - ${profile.age} anni"
        }
}
