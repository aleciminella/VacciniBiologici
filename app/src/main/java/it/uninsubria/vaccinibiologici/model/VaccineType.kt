package it.uninsubria.vaccinibiologici.model

enum class VaccineType(val label: String) {
    INACTIVATED("Inattivato"),
    RECOMBINANT("Ricombinante/subunitario"),
    MRNA("mRNA"),
    LIVE_ATTENUATED("Vivo attenuato")
}