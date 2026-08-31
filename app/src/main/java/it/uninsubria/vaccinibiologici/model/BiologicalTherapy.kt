package it.uninsubria.vaccinibiologici.model

enum class BiologicalTherapy( // Elenca le categorie di farmaci biologici usate
    val label: String,
    val description: String
) {
    NONE(
        label = "Nessuna terapia biologica",
        description = "Profilo usato quando il paziente non assume farmaci biologici."
    ),
    ANTI_TNF(
        label = "Anti-TNF",
        description = "Esempi: adalimumab, etanercept, infliximab."
    ),
    ANTI_IL17(
        label = "Anti-IL17",
        description = "Esempi: secukinumab, ixekizumab, brodalumab."
    ),
    ANTI_IL23(
        label = "Anti-IL23",
        description = "Esempi: guselkumab, risankizumab, tildrakizumab."
    ),
    OTHER_IMMUNOSUPPRESSOR(
        label = "Altri immunosoppressori",
        description = "Profilo prudenziale per terapie sistemiche immunosoppressive."
    )
}