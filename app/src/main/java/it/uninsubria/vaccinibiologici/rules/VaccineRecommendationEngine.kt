package it.uninsubria.vaccinibiologici.rules

import it.uninsubria.vaccinibiologici.data.VaccineCatalog
import it.uninsubria.vaccinibiologici.model.ClinicalCondition
import it.uninsubria.vaccinibiologici.model.PatientProfile
import it.uninsubria.vaccinibiologici.model.RecommendationReport
import it.uninsubria.vaccinibiologici.model.RecommendationStatus
import it.uninsubria.vaccinibiologici.model.RecommendationTiming
import it.uninsubria.vaccinibiologici.model.VaccineDefinition
import it.uninsubria.vaccinibiologici.model.VaccineRecommendation

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
            meningococcal(profile),
            liveMmr(profile),
            liveVaricella(profile),
            liveYellowFever(profile),
            liveNasalInfluenza(profile)
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
            vaccine = VaccineCatalog.influenzaInjectable,
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
            vaccine = VaccineCatalog.pneumococcal,
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
            vaccine = VaccineCatalog.covid19,
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
            vaccine = VaccineCatalog.rsv,
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
            vaccine = VaccineCatalog.tetanusDiphtheriaPertussis,
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
            vaccine = VaccineCatalog.hepatitisB,
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
            vaccine = VaccineCatalog.recombinantZoster,
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
            vaccine = VaccineCatalog.hpv,
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
            vaccine = VaccineCatalog.meningococcal,
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

    private fun liveMmr(profile: PatientProfile): VaccineRecommendation {
        return liveVaccine(
            profile = profile,
            vaccine = VaccineCatalog.liveMmr,
            defaultReason = "Vaccino vivo attenuato da proporre solo se indicato e non controindicato.",
            pregnancyReason = "Controindicato in gravidanza.",
            defaultNote = "Verificare documentazione vaccinale o sierologia prima di proporlo."
        )
    }

    private fun liveVaricella(profile: PatientProfile): VaccineRecommendation {
        return liveVaccine(
            profile = profile,
            vaccine = VaccineCatalog.liveVaricella,
            defaultReason = "Vaccino vivo attenuato utile solo nei soggetti suscettibili.",
            pregnancyReason = "Controindicato in gravidanza.",
            defaultNote = "Verificare immunità o documentazione vaccinale prima di proporlo."
        )
    }

    private fun liveYellowFever(profile: PatientProfile): VaccineRecommendation {
        val recommendation = liveVaccine(
            profile = profile,
            vaccine = VaccineCatalog.liveYellowFever,
            defaultReason = "Indicato solo in caso di viaggio o rischio epidemiologico documentato.",
            pregnancyReason = "In gravidanza richiede valutazione specialistica molto prudente.",
            defaultNote = "Valutare presso centro vaccinale o medicina dei viaggi prima di procedere."
        )

        return if (recommendation.status == RecommendationStatus.POSSIBLE) {
            recommendation.copy(
                status = RecommendationStatus.POSTPONED,
                timing = RecommendationTiming.SPECIALIST_EVALUATION,
                clinicalNote = "Valutare indicazione, rischio di esposizione e controindicazioni presso un centro specialistico."
            )
        } else {
            recommendation
        }
    }

    private fun liveNasalInfluenza(profile: PatientProfile): VaccineRecommendation {
        val pregnant = ClinicalCondition.PREGNANCY in profile.conditions
        val contraindicated = profile.alteredImmunocompetence || pregnant

        return recommendation(
            vaccine = VaccineCatalog.liveNasalInfluenza,
            status = if (contraindicated) RecommendationStatus.CONTRAINDICATED else RecommendationStatus.POSTPONED,
            timing = if (contraindicated) {
                RecommendationTiming.AVOID_DURING_IMMUNOSUPPRESSION
            } else {
                RecommendationTiming.SPECIALIST_EVALUATION
            },
            priority = 3,
            reason = if (contraindicated) {
                "I vaccini vivi attenuati sono da evitare in immunosoppressione e gravidanza."
            } else {
                "Non è la formulazione preferibile in un percorso clinico fragile."
            },
            clinicalNote = "Preferire il vaccino antinfluenzale iniettivo inattivato."
        )
    }

    private fun liveVaccine(
        profile: PatientProfile,
        vaccine: VaccineDefinition,
        defaultReason: String,
        pregnancyReason: String,
        defaultNote: String
    ): VaccineRecommendation {
        val pregnant = ClinicalCondition.PREGNANCY in profile.conditions
        val plannedTherapy = ClinicalCondition.PLANNED_THERAPY_START in profile.conditions
        val liveContraindicated = profile.alteredImmunocompetence || pregnant

        val status = when {
            liveContraindicated -> RecommendationStatus.CONTRAINDICATED
            plannedTherapy && profile.hasIncompleteOrUnknownHistory -> RecommendationStatus.POSTPONED
            profile.hasIncompleteOrUnknownHistory -> RecommendationStatus.POSSIBLE
            else -> RecommendationStatus.POSSIBLE
        }

        val timing = when {
            liveContraindicated -> RecommendationTiming.AVOID_DURING_IMMUNOSUPPRESSION
            plannedTherapy && profile.hasIncompleteOrUnknownHistory -> RecommendationTiming.BEFORE_THERAPY
            else -> RecommendationTiming.SPECIALIST_EVALUATION
        }

        val reason = when {
            profile.alteredImmunocompetence -> "Vaccino vivo attenuato controindicato durante immunosoppressione o terapia biologica in corso."
            pregnant -> pregnancyReason
            plannedTherapy && profile.hasIncompleteOrUnknownHistory ->
                "Storia vaccinale non documentata: verificare immunità prima dell'inizio della terapia biologica."
            profile.hasIncompleteOrUnknownHistory ->
                "Storia vaccinale incompleta o non documentata: verificare immunità prima di decidere la somministrazione."
            else -> defaultReason
        }

        val clinicalNote = when {
            status == RecommendationStatus.CONTRAINDICATED -> "Non somministrare senza rivalutazione specialistica."
            status == RecommendationStatus.POSTPONED -> "Verificare documentazione o sierologia e completare prima dell'immunosoppressione solo se necessario."
            else -> defaultNote
        }

        return recommendation(
            vaccine = vaccine,
            status = status,
            timing = timing,
            priority = if (status == RecommendationStatus.CONTRAINDICATED) 3 else 1,
            reason = reason,
            clinicalNote = clinicalNote
        )
    }

    private fun recommendation(
        vaccine: VaccineDefinition,
        status: RecommendationStatus,
        timing: RecommendationTiming,
        priority: Int,
        reason: String,
        clinicalNote: String
    ): VaccineRecommendation {
        return VaccineRecommendation(
            vaccine = vaccine,
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
