package it.uninsubria.vaccinibiologici.model

data class VaccineDefinition(
    val id: String,
    val name: String,
    val type: VaccineType,
    val source: String
)