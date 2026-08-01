package it.uninsubria.vaccinibiologici.rules

import it.uninsubria.vaccinibiologici.model.BiologicalTherapy
import it.uninsubria.vaccinibiologici.model.ClinicalCondition
import it.uninsubria.vaccinibiologici.model.PatientProfile
import it.uninsubria.vaccinibiologici.model.RecommendationStatus
import it.uninsubria.vaccinibiologici.model.RecommendationTiming
import it.uninsubria.vaccinibiologici.model.VaccinationHistory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class VaccineRecommendationEngineTest {
    private val engine = VaccineRecommendationEngine()

    @Test
    fun evaluateRecommendsCoreNonLiveVaccinesForImmunosuppressedAdult() {
        val report = engine.evaluate(
            PatientProfile(
                therapy = BiologicalTherapy.ANTI_TNF,
                age = 67,
                vaccinationHistory = VaccinationHistory.COMPLETE,
                conditions = emptySet()
            )
        )

        val recommendedNames = report.recommendations
            .filter { it.status == RecommendationStatus.RECOMMENDED }
            .map { it.vaccine.name }

        assertTrue("Antinfluenzale iniettivo" in recommendedNames)
        assertTrue("Pneumococcico" in recommendedNames)
        assertTrue("COVID-19" in recommendedNames)
        assertTrue(report.summary.contains("vaccini raccomandati"))
    }

    @Test
    fun evaluateContraindicatesLiveVaccinesDuringBiologicTherapy() {
        val report = engine.evaluate(
            PatientProfile(
                therapy = BiologicalTherapy.ANTI_IL17,
                age = 40,
                vaccinationHistory = VaccinationHistory.UNKNOWN,
                conditions = emptySet()
            )
        )

        val liveRecommendations = report.recommendations
            .filter { it.vaccine.name in liveVaccineNames }

        assertEquals(liveVaccineNames.size, liveRecommendations.size)
        assertTrue(liveRecommendations.all { it.status == RecommendationStatus.CONTRAINDICATED })
        assertTrue(liveRecommendations.all { it.timing == RecommendationTiming.AVOID_DURING_IMMUNOSUPPRESSION })
    }

    @Test
    fun evaluatePlansLiveVaccineVerificationBeforePlannedTherapyWhenHistoryIsUnknown() {
        val report = engine.evaluate(
            PatientProfile(
                therapy = BiologicalTherapy.ANTI_TNF,
                age = 35,
                vaccinationHistory = VaccinationHistory.UNKNOWN,
                conditions = setOf(ClinicalCondition.PLANNED_THERAPY_START)
            )
        )

        val mmr = report.recommendations.firstOrNull {
            it.vaccine.name == "Morbillo-Parotite-Rosolia (MPR)"
        }

        assertNotNull(mmr)
        assertEquals(RecommendationStatus.POSTPONED, mmr?.status)
        assertEquals(RecommendationTiming.BEFORE_THERAPY, mmr?.timing)
        assertTrue(mmr?.clinicalNote?.contains("solo se necessario") == true)
    }

    @Test
    fun evaluatePrioritizesMeningococcalVaccinationForAsplenia() {
        val report = engine.evaluate(
            PatientProfile(
                therapy = BiologicalTherapy.NONE,
                age = 52,
                vaccinationHistory = VaccinationHistory.COMPLETE,
                conditions = setOf(ClinicalCondition.ASPLENIA)
            )
        )

        val meningococcal = report.recommendations.firstOrNull {
            it.vaccine.name == "Meningococcico"
        }

        assertNotNull(meningococcal)
        assertEquals(RecommendationStatus.RECOMMENDED, meningococcal?.status)
        assertEquals(RecommendationTiming.DURING_THERAPY, meningococcal?.timing)
        assertEquals(3, meningococcal?.priority)
    }

    private companion object {
        val liveVaccineNames = setOf(
            "Morbillo-Parotite-Rosolia (MPR)",
            "Varicella",
            "Febbre gialla",
            "Antinfluenzale vivo attenuato nasale"
        )
    }
}
