package it.uninsubria.vaccinibiologici.model

enum class RecommendationTiming(val label: String, val order: Int) { // Indica il momento ideale per la somministrazione del vaccino rispetto alla terapia biologica.
    BEFORE_THERAPY("Prima dell'inizio della terapia", 0),
    DURING_THERAPY("Utilizzabile durante la terapia", 1),
    SPECIALIST_EVALUATION("Da valutare con specialista", 2),
    AVOID_DURING_IMMUNOSUPPRESSION("Da evitare durante immunosoppressione", 3)
}