package it.uninsubria.vaccinibiologici.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import it.uninsubria.vaccinibiologici.model.BiologicalTherapy
import it.uninsubria.vaccinibiologici.model.ClinicalCondition
import it.uninsubria.vaccinibiologici.model.PatientProfile
import it.uninsubria.vaccinibiologici.model.RecommendationReport
import it.uninsubria.vaccinibiologici.model.RecommendationStatus
import it.uninsubria.vaccinibiologici.model.RecommendationTiming
import it.uninsubria.vaccinibiologici.model.VaccinationHistory
import it.uninsubria.vaccinibiologici.model.VaccineRecommendation
import it.uninsubria.vaccinibiologici.rules.ClinicalInputValidator
import it.uninsubria.vaccinibiologici.rules.VaccineRecommendationEngine
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import it.uninsubria.vaccinibiologici.data.ClinicalScenarioRepository
import it.uninsubria.vaccinibiologici.model.ClinicalScenario
import it.uninsubria.vaccinibiologici.ui.theme.ClinicalBorder
import it.uninsubria.vaccinibiologici.ui.theme.ClinicalCardBackground
import it.uninsubria.vaccinibiologici.ui.theme.ClinicalMutedText
import it.uninsubria.vaccinibiologici.ui.theme.ClinicalOnStatus
import it.uninsubria.vaccinibiologici.ui.theme.StatusContraindicated
import it.uninsubria.vaccinibiologici.ui.theme.StatusPossible
import it.uninsubria.vaccinibiologici.ui.theme.StatusPostponed
import it.uninsubria.vaccinibiologici.ui.theme.StatusRecommended
import it.uninsubria.vaccinibiologici.ui.theme.TimingAvoidImmunosuppression
import it.uninsubria.vaccinibiologici.ui.theme.TimingBeforeTherapy
import it.uninsubria.vaccinibiologici.ui.theme.TimingDuringTherapy
import it.uninsubria.vaccinibiologici.ui.theme.TimingSpecialistEvaluation
import kotlinx.coroutines.launch

@Composable
fun VacciniScreen(
    modifier: Modifier = Modifier,
    recommendationEngine: VaccineRecommendationEngine = remember { VaccineRecommendationEngine() },
    scenarioRepository: ClinicalScenarioRepository? = null
) { // vengono memorizzati i dati inseriti, risultati e scenari salvati
    var selectedTherapy by remember { mutableStateOf(BiologicalTherapy.ANTI_TNF) }
    var selectedHistory by remember { mutableStateOf(VaccinationHistory.UNKNOWN) }
    var ageText by remember { mutableStateOf("") }
    var selectedConditions by remember { mutableStateOf(emptySet<ClinicalCondition>()) } // lista che non permette duplicati inizialmente vuota
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var report by remember { mutableStateOf<RecommendationReport?>(null) }
    val coroutineScope = rememberCoroutineScope() //  Serve per lanciare operazioni "pesanti" (come salvare o eliminare dal database) senza bloccare l'interfaccia grafica.
    var scenarioTitle by remember { mutableStateOf("") }
    var savedScenarios by remember { mutableStateOf(emptyList<ClinicalScenario>()) }
    var saveMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(scenarioRepository) { // Appena avviata l'app vengono presi tutti gli scenari salvati nel database (findAll) e messi nella variabile savedScenarios
        if (scenarioRepository != null) {
            savedScenarios = scenarioRepository.findAll()
        }
    }
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(
                    start = 20.dp,
                    top = 60.dp,
                    end = 20.dp,
                    bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Header()
            ClinicalForm(
                selectedTherapy = selectedTherapy,
                selectedHistory = selectedHistory,
                ageText = ageText,
                selectedConditions = selectedConditions,
                onTherapyChange = { selectedTherapy = it }, // se viene cambiata terapia viene messa in selectedTherapy
                onHistoryChange = { selectedHistory = it },
                onAgeChange = { ageText = it.filter(Char::isDigit) },
                onConditionChange = { condition, checked ->
                    selectedConditions = if (checked) {
                        selectedConditions + condition
                    } else {
                        selectedConditions - condition
                    }
                }
            )
            Button( // vengono inoltrati i dati all'InputValidator, solo se vengono validati il profilo viene inoltrato al motore di calcolo che restituisce il report
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                onClick = {
                    val age = ClinicalInputValidator.validateAge(ageText).getOrElse {
                        errorMessage = it.message ?: "Dati non validi."
                        report = null
                        return@Button
                    }

                    val profile = PatientProfile(
                        therapy = selectedTherapy,
                        age = age,
                        vaccinationHistory = selectedHistory,
                        conditions = selectedConditions
                    )

                    ClinicalInputValidator.validateProfile(profile).getOrElse {
                        errorMessage = it.message ?: "Dati clinici non coerenti."
                        report = null
                        return@Button
                    }

                    errorMessage = null
                    report = recommendationEngine.evaluate(profile)
                }
            ) {
                Text("Calcola raccomandazioni")
            }

            errorMessage?.let { // prende l'eventuale errore e lo scrive in rosso sotto il pulsante
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            ResultPreview(
                report = report,
                scenarioTitle = scenarioTitle,
                onScenarioTitleChange = { scenarioTitle = it },
                saveMessage = saveMessage,
                canSaveScenario = scenarioRepository != null,
                onSaveScenario = { currentReport ->
                    if (scenarioRepository != null) {
                        coroutineScope.launch {
                            val scenario = ClinicalScenario(
                                title = scenarioTitle,
                                profile = currentReport.profile,
                                report = currentReport
                            )
                            scenarioRepository.save(scenario)
                            savedScenarios = scenarioRepository.findAll()
                            scenarioTitle = ""
                            saveMessage = "Scenario salvato correttamente."
                        }
                    }
                }
            )

            if (scenarioRepository != null) {
                SavedScenariosSection(
                    scenarios = savedScenarios,
                    onRecalculateScenario = { scenario ->
                        val profile = scenario.profile
                        selectedTherapy = profile.therapy
                        selectedHistory = profile.vaccinationHistory
                        ageText = profile.age.toString()
                        selectedConditions = profile.conditions
                        errorMessage = null
                        report = recommendationEngine.evaluate(profile)
                        scenarioTitle = scenario.title
                        saveMessage = "Scenario ricalcolato."
                    },
                    onDeleteScenario = { scenario ->
                        coroutineScope.launch {
                            scenarioRepository.deleteById(scenario.id)
                            savedScenarios = scenarioRepository.findAll()
                            saveMessage = "Scenario eliminato."
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun Header() {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "Vaccini in terapia biologica",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Inserisci i dati clinici essenziali per ottenere una prima classificazione didattica delle raccomandazioni vaccinali.",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ClinicalForm(
    // Parametri che permettono al modulo di essere sincronizzato con lo stato principale della schermata
    selectedTherapy: BiologicalTherapy,
    selectedHistory: VaccinationHistory,
    ageText: String,
    selectedConditions: Set<ClinicalCondition>,
    onTherapyChange: (BiologicalTherapy) -> Unit, // Questo modulo avrà un'azione che, quando attivata, trasporterà una Terapia (BiologicalTherapy) verso l'esterno
    onHistoryChange: (VaccinationHistory) -> Unit,
    onAgeChange: (String) -> Unit,
    onConditionChange: (ClinicalCondition, Boolean) -> Unit
) {
    ClinicalPanel {
        Text("Dati clinici", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        FieldLabel("Terapia biologica")
        EnumDropdown(
            value = selectedTherapy,
            values = BiologicalTherapy.entries, // mostra opzioni enum
            label = { it.label },
            onChange = onTherapyChange
        )
        HelperText(selectedTherapy.description) // testo descrizione dinamico della terapia selezionata

        FieldLabel("Età")
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = ageText,
            onValueChange = onAgeChange,
            singleLine = true,
            label = { Text("Esempio: 65") }
        )

        FieldLabel("Documentazione vaccinale")
        EnumDropdown(
            value = selectedHistory,
            values = VaccinationHistory.entries,
            label = { it.label },
            onChange = onHistoryChange
        )
        HelperText("Serve a capire se verificare o completare richiami e cicli vaccinali prima/durante la terapia.")

        FieldLabel("Condizioni cliniche e patologie concomitanti")
        ClinicalCondition.entries.forEach { condition ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = condition in selectedConditions, // se presente mette la spunta
                    onCheckedChange = { onConditionChange(condition, it) }
                )
                Text(condition.label, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun ResultPreview(
    report: RecommendationReport?,
    scenarioTitle: String,
    onScenarioTitleChange: (String) -> Unit,
    saveMessage: String?,
    canSaveScenario: Boolean,
    onSaveScenario: (RecommendationReport) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Risultato", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

        if (report == null) {
            ClinicalPanel {
                Text("Inserisci i dati del paziente e avvia il calcolo.")
                HelperText("Il risultato organizza le raccomandazioni per momento clinico rispetto alla terapia biologica.")
            }
            return
        }

        ClinicalPanel {
            Text(report.summary, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            SummaryRow("Terapia", report.profile.therapy.label)
            SummaryRow("Età", "${report.profile.age} anni")
            SummaryRow("Documentazione vaccinale", report.profile.vaccinationHistory.label)
        }

        RecommendationCounters(report)
        TimingPlan(report)
        RecommendationTimeline(report)

        ScenarioSavePanel(
            report = report,
            scenarioTitle = scenarioTitle,
            onScenarioTitleChange = onScenarioTitleChange,
            saveMessage = saveMessage,
            canSaveScenario = canSaveScenario,
            onSaveScenario = onSaveScenario
        )
    }
}

@Composable
private fun RecommendationCounters(report: RecommendationReport) {
    ClinicalPanel {
        Text("Sintesi raccomandazioni", fontWeight = FontWeight.Bold)
        RecommendationStatus.entries.forEach { status ->
            val count = report.recommendations.count { it.status == status }
            if (count > 0) {
                SummaryRow(status.label, "$count vaccini")
            }
        }
    }
}

@Composable
private fun TimingPlan(report: RecommendationReport) {
    ClinicalPanel {
        Text("Piano vaccinale temporale", fontWeight = FontWeight.Bold)
        HelperText("Le raccomandazioni sono divise in base al momento piu' adatto rispetto all'inizio o alla prosecuzione della terapia biologica.")
        RecommendationTiming.entries.forEach { timing ->
            val count = report.recommendations.count { it.timing == timing }
            if (count > 0) {
                SummaryRow(timing.label, "$count vaccini")
            }
        }
    }
}

@Composable
private fun RecommendationTimeline(report: RecommendationReport) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Dettaglio per momento clinico", fontWeight = FontWeight.Bold)
        RecommendationTiming.entries.forEach { timing ->
            val items = report.recommendations
                .filter { it.timing == timing }
                .sortedWith(
                    compareBy<VaccineRecommendation> { it.status.order }
                        .thenByDescending { it.priority }
                        .thenBy { it.vaccine.name }
                )

            if (items.isNotEmpty()) {
                TimingSection(timing = timing, recommendations = items)
            }
        }
    }
}
@Composable
private fun ScenarioSavePanel(
    report: RecommendationReport,
    scenarioTitle: String,
    onScenarioTitleChange: (String) -> Unit,
    saveMessage: String?,
    canSaveScenario: Boolean,
    onSaveScenario: (RecommendationReport) -> Unit
) {
    ClinicalPanel {
        Text("Salva scenario clinico", fontWeight = FontWeight.Bold)
        HelperText("Gli scenari sono anonimi e servono solo per confrontare casi didattici simulati.")

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = scenarioTitle,
            onValueChange = onScenarioTitleChange,
            singleLine = true,
            label = { Text("Titolo scenario") }
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = canSaveScenario,
            onClick = { onSaveScenario(report) }
        ) {
            Text("Salva scenario")
        }

        saveMessage?.let {
            HelperText(it)
        }
    }
}

@Composable
private fun SavedScenariosSection(
    scenarios: List<ClinicalScenario>,
    onRecalculateScenario: (ClinicalScenario) -> Unit,
    onDeleteScenario: (ClinicalScenario) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Scenari salvati", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

        if (scenarios.isEmpty()) {
            ClinicalPanel {
                Text("Nessuno scenario salvato.")
                HelperText("Dopo il calcolo puoi salvare casi anonimi per rivederli durante la discussione del progetto.")
            }
            return
        }

        scenarios.forEach { scenario ->
            SavedScenarioCard(
                scenario = scenario,
                onRecalculateScenario = onRecalculateScenario,
                onDeleteScenario = onDeleteScenario
            )
        }
    }
}

@Composable
private fun SavedScenarioCard(
    scenario: ClinicalScenario,
    onRecalculateScenario: (ClinicalScenario) -> Unit,
    onDeleteScenario: (ClinicalScenario) -> Unit
) {
    ClinicalPanel {
        Text(scenario.displayTitle, fontWeight = FontWeight.Bold)
        SummaryRow("Terapia", scenario.profile.therapy.label)
        SummaryRow("Età", "${scenario.profile.age} anni")
        SummaryRow("Documentazione vaccinale", scenario.profile.vaccinationHistory.label)
        SummaryRow("Condizioni", formatConditions(scenario.profile.conditions))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onRecalculateScenario(scenario) }
        ) {
            Text("Ricalcola scenario")
        }

        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onDeleteScenario(scenario) }
        ) {
            Text("Elimina scenario")
        }
    }
}

private fun formatConditions(conditions: Set<ClinicalCondition>): String {
    return if (conditions.isEmpty()) {
        "Nessuna condizione selezionata"
    } else {
        conditions.joinToString { it.label }
    }
}

@Composable
private fun TimingSection(
    timing: RecommendationTiming,
    recommendations: List<VaccineRecommendation>
) {
    ClinicalPanel {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = timing.label,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${recommendations.size}",
                color = timingColor(timing),
                fontWeight = FontWeight.Bold
            )
        }
        HelperText(timingDescription(timing))
        recommendations.forEach { recommendation ->
            RecommendationCard(recommendation)
        }
    }
}

@Composable
private fun RecommendationCard(item: VaccineRecommendation) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, ClinicalBorder),
        colors = CardDefaults.cardColors(containerColor = ClinicalCardBackground)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = item.vaccine.name,
                    fontWeight = FontWeight.Bold
                )
                RecommendationBadge(item.status)
            }
            SummaryRow("Tipo", item.vaccine.type.label)
            SummaryRow("Priorità", "${item.priority}/3")
            SummaryRow("Motivazione", item.reason)
            SummaryRow("Nota clinica", item.clinicalNote)
            SummaryRow("Fonte", item.vaccine.source)
        }
    }
}

@Composable
private fun RecommendationBadge(status: RecommendationStatus) {
    Surface(
        color = statusColor(status),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            text = status.label,
            color = ClinicalOnStatus,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun timingDescription(timing: RecommendationTiming): String {
    return when (timing) {
        RecommendationTiming.BEFORE_THERAPY ->
            "Da considerare prima di iniziare la terapia, soprattutto quando servono cicli o richiami da completare."
        RecommendationTiming.DURING_THERAPY ->
            "Generalmente utilizzabile anche durante la terapia, con verifica del calendario vaccinale."
        RecommendationTiming.SPECIALIST_EVALUATION ->
            "Richiede valutazione individuale in base a rischio, anamnesi e indicazioni aggiornate."
        RecommendationTiming.AVOID_DURING_IMMUNOSUPPRESSION ->
            "Da evitare o rimandare durante immunosoppressione, in particolare per vaccini vivi attenuati."
    }
}

private fun timingColor(timing: RecommendationTiming): Color {
    return when (timing) {
        RecommendationTiming.BEFORE_THERAPY -> TimingBeforeTherapy
        RecommendationTiming.DURING_THERAPY -> TimingDuringTherapy
        RecommendationTiming.SPECIALIST_EVALUATION -> TimingSpecialistEvaluation
        RecommendationTiming.AVOID_DURING_IMMUNOSUPPRESSION -> TimingAvoidImmunosuppression
    }
}

@Composable
private fun ClinicalPanel(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, ClinicalBorder),
        colors = CardDefaults.cardColors(containerColor = ClinicalOnStatus)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun <T> EnumDropdown(
    value: T,
    values: List<T>,
    label: (T) -> String,
    onChange: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp),
            onClick = { expanded = true }
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = label(value)
            )
            Text("v")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            values.forEach { item ->
                DropdownMenuItem(
                    text = { Text(label(item)) },
                    onClick = {
                        onChange(item)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(text, fontWeight = FontWeight.Bold)
}

@Composable
private fun HelperText(text: String) {
    Text(text, color = ClinicalMutedText, style = MaterialTheme.typography.bodySmall)
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Column {
        Text(label, color = ClinicalMutedText, style = MaterialTheme.typography.labelMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

private fun statusColor(status: RecommendationStatus): Color {
    return when (status) {
        RecommendationStatus.RECOMMENDED -> StatusRecommended
        RecommendationStatus.POSSIBLE -> StatusPossible
        RecommendationStatus.POSTPONED -> StatusPostponed
        RecommendationStatus.CONTRAINDICATED -> StatusContraindicated
    }
}
