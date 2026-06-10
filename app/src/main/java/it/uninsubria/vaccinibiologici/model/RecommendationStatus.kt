package it.uninsubria.vaccinibiologici.model

enum class RecommendationStatus(val label: String, val order: Int) {
    RECOMMENDED("Raccomandato", 0),
    POSSIBLE("Possibile", 1),
    POSTPONED("Rimandare/valutare", 2),
    CONTRAINDICATED("Controindicato", 3)
}