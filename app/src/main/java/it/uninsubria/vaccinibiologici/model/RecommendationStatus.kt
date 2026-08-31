package it.uninsubria.vaccinibiologici.model

enum class RecommendationStatus(val label: String, val order: Int) { // Descrive il verdetto del sistema per un determinato vaccino.
    RECOMMENDED("Raccomandato", 0),
    POSSIBLE("Possibile", 1),
    POSTPONED("Rimandare/valutare", 2),
    CONTRAINDICATED("Controindicato", 3)
}