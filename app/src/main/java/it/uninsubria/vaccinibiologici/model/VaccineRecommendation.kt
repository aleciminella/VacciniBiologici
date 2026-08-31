package it.uninsubria.vaccinibiologici.model

data class VaccineRecommendation( // L'esito della valutazione clinica per un singolo vaccino.
    val vaccine: VaccineDefinition,
    val status: RecommendationStatus,
    val timing: RecommendationTiming,
    val priority: Int,
    val reason: String,
    val clinicalNote: String
)