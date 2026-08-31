package it.uninsubria.vaccinibiologici.model

data class RecommendationReport( // Risultato finale dell'elaborazione del motore di calcolo. Contiene il riepilogo per l'utente e la lista di tutte le raccomandazioni specifiche.
    val profile: PatientProfile,
    val recommendations: List<VaccineRecommendation>,
    val summary: String
)