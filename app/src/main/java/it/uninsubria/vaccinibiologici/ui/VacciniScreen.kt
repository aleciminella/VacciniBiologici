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
import it.uninsubria.vaccinibiologici.rules.ClinicalInputValidator
import it.uninsubria.vaccinibiologici.rules.VaccineRecommendationEngine

@Composable
fun VacciniScreen(
    modifier: Modifier = Modifier,
    recommendationEngine: VaccineRecommendationEngine = remember { VaccineRecommendationEngine() }
) {
    var selectedTherapy by remember { mutableStateOf(BiologicalTherapy.ANTI_TNF) }
    var selectedHistory by remember { mutableStateOf(VaccinationHistory.UNKNOWN) }
    var ageText by remember { mutableStateOf("") }
    var selectedConditions by remember { mutableStateOf(emptySet<ClinicalCondition>()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var report by remember { mutableStateOf<RecommendationReport?>(null) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Header()
            ClinicalForm(
                selectedTherapy = selectedTherapy,
                selectedHistory = selectedHistory,
                ageText = ageText,
                selectedConditions = selectedConditions,
                onTherapyChange = { selectedTherapy = it },
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
            Button(
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

            errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            ResultPreview(report)
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
    selectedTherapy: BiologicalTherapy,
    selectedHistory: VaccinationHistory,
    ageText: String,
    selectedConditions: Set<ClinicalCondition>,
    onTherapyChange: (BiologicalTherapy) -> Unit,
    onHistoryChange: (VaccinationHistory) -> Unit,
    onAgeChange: (String) -> Unit,
    onConditionChange: (ClinicalCondition, Boolean) -> Unit
) {
    ClinicalPanel {
        Text("Dati clinici", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        FieldLabel("Terapia biologica")
        EnumDropdown(
            value = selectedTherapy,
            values = BiologicalTherapy.entries,
            label = { it.label },
            onChange = onTherapyChange
        )
        HelperText(selectedTherapy.description)

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
                    checked = condition in selectedConditions,
                    onCheckedChange = { onConditionChange(condition, it) }
                )
                Text(condition.label, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun ResultPreview(report: RecommendationReport?) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Risultato", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

        if (report == null) {
            ClinicalPanel {
                Text("Inserisci i dati del paziente e avvia il calcolo.")
                HelperText("I risultati dettagliati e il salvataggio degli scenari verranno completati nei prossimi moduli.")
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

        ClinicalPanel {
            Text("Stati raccomandazione", fontWeight = FontWeight.Bold)
            RecommendationStatus.entries.forEach { status ->
                val count = report.recommendations.count { it.status == status }
                if (count > 0) {
                    SummaryRow(status.label, count.toString())
                }
            }
        }

        ClinicalPanel {
            Text("Momento clinico", fontWeight = FontWeight.Bold)
            RecommendationTiming.entries.forEach { timing ->
                val count = report.recommendations.count { it.timing == timing }
                if (count > 0) {
                    SummaryRow(timing.label, count.toString())
                }
            }
        }
    }
}

@Composable
private fun ClinicalPanel(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color(0xFFD8E1DE)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
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
    Text(text, color = Color(0xFF60706C), style = MaterialTheme.typography.bodySmall)
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Column {
        Text(label, color = Color(0xFF60706C), style = MaterialTheme.typography.labelMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}
