package it.uninsubria.vaccinibiologici.rules

import it.uninsubria.vaccinibiologici.model.ClinicalCondition
import it.uninsubria.vaccinibiologici.model.PatientProfile
import it.uninsubria.vaccinibiologici.model.RecommendationReport
import it.uninsubria.vaccinibiologici.model.RecommendationStatus
import it.uninsubria.vaccinibiologici.model.RecommendationTiming
import it.uninsubria.vaccinibiologici.model.VaccineDefinition
import it.uninsubria.vaccinibiologici.model.VaccineRecommendation
import it.uninsubria.vaccinibiologici.model.VaccineType

class VaccineRecommendationEngine {

    fun evaluate(profile: PatientProfile): RecommendationReport {
        val recommendations = listOf(
            influenzaInjectable(profile),
            pneumococcal(profile),
            covid19(profile),
            rsv(profile),
            tetanusDiphtheriaPertussis(profile),
            hepatitisB(profile),
            recombinantZoster(profile),
            hpv(profile),
            meningococcal(profile)
        ).sortedWith(
            compareBy<VaccineRecommendation> { it.status.order }
                .thenBy { it.timing.order }
                .thenByDescending { it.priority }
                .thenBy { it.vaccine.name }
        )

        return RecommendationReport(
            profile = profile,
            recommendations = recommendations,
            summary = buildSummary(profile, recommendations)
        )
    }

    private fun influenzaInjectable(profile: PatientProfile): VaccineRecommendation {
        val priority = when {
            profile.age >= 65 -> 3
            profile.alteredImmunocompetence || profile.hasHighRiskCondition -> 2
            else -> 1
        }

        return recommendation(
            id = "influenza_injectable",
            name = "Antinfluenzale iniettivo",
            type = VaccineType.INACTIVATED,
            source = "CDC Adult Immunization Schedule; linee guida nazionali",
            status = RecommendationStatus.RECOMMENDED,
            timing = RecommendationTiming.DURING_THERAPY,
            priority = priority,
            reason = "Vaccino non vivo indicato annualmente, con priorità maggiore nei pazienti fragili.",
            clinicalNote = "Preferire formulazioni inattivate; evitare lo spray nasale vivo attenuato."
        )
    }

    private fun pneumococcal(profile: PatientProfile): VaccineRecommendation {
        val recommended = profile.age >= 65 || profile.alteredImmunocompetence || profile.hasHighRiskCondition

        return recommendation(
            id = "pneumococcal",
            name = "Pneumococcico",
            type = VaccineType.RECOMBINANT,
            source = "CDC Adult Immunization Schedule; linee guida nazionali",
            status = if (recommended) RecommendationStatus.RECOMMENDED else RecommendationStatus.POSSIBLE,
            timing = RecommendationTiming.DURING_THERAPY,
            priority = if (recommended) 3 else 1,
            reason = if (recommended) {
                "Età, immunosoppressione o comorbilità aumentano il rischio di infezione invasiva."
            } else {
                "Da valutare secondo calendario vaccinale e rischio individuale."
            },
            clinicalNote = "Lo schema preciso dipende dal vaccino disponibile e dalla storia vaccinale."
        )
    }

    private fun covid19(profile: PatientProfile): VaccineRecommendation {
        return recommendation(
            id = "covid19",
            name = "COVID-19",
            type = VaccineType.MRNA,
            source = "CDC Adult Immunization Schedule",
            status = RecommendationStatus.RECOMMENDED,
            timing = RecommendationTiming.DURING_THERAPY,
            priority = if (profile.alteredImmunocompetence || profile.age >= 65) 3 else 2,
            reason = "I vaccini non vivi sono utilizzabili anche nei pazienti immunomodulati.",
            clinicalNote = "Verificare numero di dosi e richiami secondo indicazioni aggiornate."
        )
    }

    private fun rsv(profile: PatientProfile): VaccineRecommendation {
        val recommended = profile.age >= 75 ||
                (profile.age >= 50 && (profile.alteredImmunocompetence || profile.hasHighRiskCondition))

        return recommendation(
            id = "rsv",
            name = "Virus respiratorio sinciziale (RSV)",
            type = VaccineType.RECOMBINANT,
            source = "CDC Adult Immunization Schedule",
            status = if (recommended) RecommendationStatus.RECOMMENDED else RecommendationStatus.POSSIBLE,
            timing = if (recommended) RecommendationTiming.DURING_THERAPY else RecommendationTiming.SPECIALIST_EVALUATION,
            priority = if (recommended) 2 else 1,
            reason = if (recommended) {
                "Età avanzata, immunosoppressione o comorbilità aumentano il rischio di malattia respiratoria grave."
            } else {
                "Da valutare in base a età, fragilità e indicazioni vaccinali aggiornate."
            },
            clinicalNote = "Vaccino non vivo; indicazione da confermare secondo calendario e disponibilità locale."
        )
    }

    private fun tetanusDiphtheriaPertussis(profile: PatientProfile): VaccineRecommendation {
        val needsVerification = profile.hasIncompleteOrUnknownHistory

        return recommendation(
            id = "tdap",
            name = "Difterite-Tetano-Pertosse",
            type = VaccineType.INACTIVATED,
            source = "CDC Adult Immunization Schedule; linee guida nazionali",
            status = if (needsVerification) RecommendationStatus.RECOMMENDED else RecommendationStatus.POSSIBLE,
            timing = if (needsVerification) RecommendationTiming.BEFORE_THERAPY else RecommendationTiming.SPECIALIST_EVALUATION,
            priority = if (needsVerification) 2 else 1,
            reason = if (needsVerification) {
                "Storia vaccinale incompleta o non documentata: raccomandata la verifica dei richiami."
            } else {
                "Da mantenere secondo calendario vaccinale e richiami periodici."
            },
            clinicalNote = if (needsVerification) {
                "Verificare documentazione vaccinale e completare i richiami se necessario."
            } else {
                "Vaccino non vivo; valutare in base al calendario vaccinale."
            }
        )
    }

    private fun hepatitisB(profile: PatientProfile): VaccineRecommendation {
        val needsVerification = profile.hasIncompleteOrUnknownHistory
        val recommended = needsVerification || profile.alteredImmunocompetence

        return recommendation(
            id = "hepatitis_b",
            name = "Epatite B",
            type = VaccineType.RECOMBINANT,
            source = "CDC Adult Immunization Schedule; linee guida nazionali",
            status = if (recommended) RecommendationStatus.RECOMMENDED else RecommendationStatus.POSSIBLE,
            timing = if (recommended) RecommendationTiming.BEFORE_THERAPY else RecommendationTiming.SPECIALIST_EVALUATION,
            priority = if (recommended) 2 else 1,
            reason = if (recommended) {
                "Storia vaccinale incompleta/non documentata o immunosoppressione richiedono verifica dello stato vaccinale."
            } else {
                "Possibile secondo anamnesi, esposizione professionale e calendario vaccinale."
            },
            clinicalNote = "Verificare documentazione o sierologia e completare il ciclo se necessario."
        )
    }

    private fun recombinantZoster(profile: PatientProfile): VaccineRecommendation {
        val recommended = profile.age >= 50 || (profile.isAdult && profile.alteredImmunocompetence)

        return recommendation(
            id = "recombinant_zoster",
            name = "Herpes zoster ricombinante",
            type = VaccineType.RECOMBINANT,
            source = "CDC Adult Immunization Schedule; linee guida nazionali",
            status = if (recommended) RecommendationStatus.RECOMMENDED else RecommendationStatus.POSSIBLE,
            timing = RecommendationTiming.DURING_THERAPY,
            priority = if (recommended) 2 else 1,
            reason = if (recommended) {
                "Età adulta avanzata o immunosoppressione aumentano il rischio di zoster e complicanze."
            } else {
                "Da valutare se compaiono fattori di rischio aggiuntivi."
            },
            clinicalNote = "Usare vaccino ricombinante, non vivo."
        )
    }

    private fun hpv(profile: PatientProfile): VaccineRecommendation {
        val recommended = profile.age <= 26

        return recommendation(
            id = "hpv",
            name = "HPV",
            type = VaccineType.RECOMBINANT,
            source = "CDC Adult Immunization Schedule; linee guida nazionali",
            status = if (recommended) RecommendationStatus.RECOMMENDED else RecommendationStatus.POSSIBLE,
            timing = if (recommended) RecommendationTiming.BEFORE_THERAPY else RecommendationTiming.SPECIALIST_EVALUATION,
            priority = if (recommended) 2 else 1,
            reason = if (recommended) {
                "Rientra nelle fasce in cui il completamento vaccinale è più rilevante."
            } else {
                "Richiede decisione condivisa in base ad anamnesi e rischio individuale."
            },
            clinicalNote = "Non è un vaccino vivo attenuato."
        )
    }

    private fun meningococcal(profile: PatientProfile): VaccineRecommendation {
        val asplenia = ClinicalCondition.ASPLENIA in profile.conditions

        return recommendation(
            id = "meningococcal",
            name = "Meningococcico",
            type = VaccineType.RECOMBINANT,
            source = "CDC Adult Immunization Schedule; linee guida nazionali",
            status = if (asplenia) RecommendationStatus.RECOMMENDED else RecommendationStatus.POSSIBLE,
            timing = if (asplenia) RecommendationTiming.DURING_THERAPY else RecommendationTiming.SPECIALIST_EVALUATION,
            priority = if (asplenia) 3 else 1,
            reason = if (asplenia) {
                "Asplenia o deficit splenico aumentano il rischio di infezioni invasive da batteri capsulati."
            } else {
                "Da valutare secondo età, fattori di rischio e calendario vaccinale."
            },
            clinicalNote = "Verificare copertura MenACWY/MenB secondo centro vaccinale."
        )
    }

    private fun recommendation(
        id: String,
        name: String,
        type: VaccineType,
        source: String,
        status: RecommendationStatus,
        timing: RecommendationTiming,
        priority: Int,
        reason: String,
        clinicalNote: String
    ): VaccineRecommendation {
        return VaccineRecommendation(
            vaccine = VaccineDefinition(
                id = id,
                name = name,
                type = type,
                source = source
            ),
            status = status,
            timing = timing,
            priority = priority,
            reason = reason,
            clinicalNote = clinicalNote
        )
    }

    private fun buildSummary(
        profile: PatientProfile,
        recommendations: List<VaccineRecommendation>
    ): String {
        val recommended = recommendations.count { it.status == RecommendationStatus.RECOMMENDED }
        val toVerify = recommendations.count {
            it.timing == RecommendationTiming.BEFORE_THERAPY ||
                    it.timing == RecommendationTiming.SPECIALIST_EVALUATION
        }

        return "${profile.therapy.label}, storia vaccinale ${profile.vaccinationHistory.label.lowercase()}: " +
                "$recommended vaccini raccomandati, $toVerify elementi da verificare o pianificare."
    }
}