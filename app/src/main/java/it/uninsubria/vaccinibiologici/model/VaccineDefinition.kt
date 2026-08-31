package it.uninsubria.vaccinibiologici.model

data class VaccineDefinition( // Definizione anagrafica di un vaccino presente nel catalogo
    val id: String,
    val name: String,
    val type: VaccineType,
    val source: String
)