package it.uninsubria.vaccinibiologici.data

import it.uninsubria.vaccinibiologici.model.VaccineDefinition
import it.uninsubria.vaccinibiologici.model.VaccineType

object VaccineCatalog {
    val influenzaInjectable = VaccineDefinition(
        id = "influenza_injectable",
        name = "Antinfluenzale iniettivo",
        type = VaccineType.INACTIVATED,
        source = "CDC Adult Immunization Schedule; linee guida nazionali"
    )

    val pneumococcal = VaccineDefinition(
        id = "pneumococcal",
        name = "Pneumococcico",
        type = VaccineType.RECOMBINANT,
        source = "CDC Adult Immunization Schedule; linee guida nazionali"
    )

    val covid19 = VaccineDefinition(
        id = "covid19",
        name = "COVID-19",
        type = VaccineType.MRNA,
        source = "CDC Adult Immunization Schedule"
    )

    val rsv = VaccineDefinition(
        id = "rsv",
        name = "Virus respiratorio sinciziale (RSV)",
        type = VaccineType.RECOMBINANT,
        source = "CDC Adult Immunization Schedule"
    )

    val tetanusDiphtheriaPertussis = VaccineDefinition(
        id = "tdap",
        name = "Difterite-Tetano-Pertosse",
        type = VaccineType.INACTIVATED,
        source = "CDC Adult Immunization Schedule; linee guida nazionali"
    )

    val hepatitisB = VaccineDefinition(
        id = "hepatitis_b",
        name = "Epatite B",
        type = VaccineType.RECOMBINANT,
        source = "CDC Adult Immunization Schedule; linee guida nazionali"
    )

    val recombinantZoster = VaccineDefinition(
        id = "recombinant_zoster",
        name = "Herpes zoster ricombinante",
        type = VaccineType.RECOMBINANT,
        source = "CDC Adult Immunization Schedule; linee guida nazionali"
    )

    val hpv = VaccineDefinition(
        id = "hpv",
        name = "HPV",
        type = VaccineType.RECOMBINANT,
        source = "CDC Adult Immunization Schedule; linee guida nazionali"
    )

    val meningococcal = VaccineDefinition(
        id = "meningococcal",
        name = "Meningococcico",
        type = VaccineType.RECOMBINANT,
        source = "CDC Adult Immunization Schedule; linee guida nazionali"
    )

    val all = listOf(
        influenzaInjectable,
        pneumococcal,
        covid19,
        rsv,
        tetanusDiphtheriaPertussis,
        hepatitisB,
        recombinantZoster,
        hpv,
        meningococcal
    )
}
