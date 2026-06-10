package it.uninsubria.vaccinibiologici.model

data class RecommendationReport(
    val profile: PatientProfile,
    val recommendations: List<VaccineRecommendation>,
    val summary: String
)