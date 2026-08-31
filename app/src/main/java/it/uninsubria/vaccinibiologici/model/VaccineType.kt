package it.uninsubria.vaccinibiologici.model

enum class VaccineType(val label: String) { // Elenca le categorie biologiche di un vaccino
    INACTIVATED("Inattivato"),
    RECOMBINANT("Ricombinante/subunitario"),
    MRNA("mRNA"),
    LIVE_ATTENUATED("Vivo attenuato")
}