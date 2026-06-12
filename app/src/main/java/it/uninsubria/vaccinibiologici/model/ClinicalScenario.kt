package it.uninsubria.vaccinibiologici.model

data class ClinicalScenario(
    val id: Long = 0,
    val title: String,
    val profile: PatientProfile,
    val report: RecommendationReport? = null,
    val createdAtMillis: Long = System.currentTimeMillis()
) {
    val isSaved: Boolean
        get() = id > 0

    val displayTitle: String
        get() = title.ifBlank {
            "Scenario ${profile.therapy.label} - ${profile.age} anni"
        }
}