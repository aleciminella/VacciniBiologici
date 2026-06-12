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

    val liveMmr = VaccineDefinition(
        id = "live_mmr",
        name = "Morbillo-Parotite-Rosolia (MPR)",
        type = VaccineType.LIVE_ATTENUATED,
        source = "EULAR; CDC Adult Immunization Schedule"
    )

    val liveVaricella = VaccineDefinition(
        id = "live_varicella",
        name = "Varicella",
        type = VaccineType.LIVE_ATTENUATED,
        source = "EULAR; CDC Adult Immunization Schedule"
    )

    val liveYellowFever = VaccineDefinition(
        id = "live_yellow_fever",
        name = "Febbre gialla",
        type = VaccineType.LIVE_ATTENUATED,
        source = "EULAR; CDC Adult Immunization Schedule; medicina dei viaggi"
    )

    val liveNasalInfluenza = VaccineDefinition(
        id = "live_nasal_influenza",
        name = "Antinfluenzale vivo attenuato nasale",
        type = VaccineType.LIVE_ATTENUATED,
        source = "CDC Adult Immunization Schedule; EULAR"
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
        meningococcal,
        liveMmr,
        liveVaricella,
        liveYellowFever,
        liveNasalInfluenza
    )
}
