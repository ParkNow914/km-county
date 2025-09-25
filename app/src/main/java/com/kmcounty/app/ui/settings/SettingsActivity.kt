package com.kmcounty.app.ui.settings

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kmcounty.app.R
import com.kmcounty.app.ui.theme.KMCountyTheme
import com.kmcounty.app.ui.settings.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

/**
 * Tela de configurações do aplicativo
 * Permite personalizar thresholds, modo conservador, analytics, etc.
 */
class SettingsActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            KMCountyTheme {
                SettingsScreen(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel()) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Estados das configurações
    val greenThreshold by viewModel.greenThreshold.collectAsState(initial = 1.50)
    val orangeThreshold by viewModel.orangeThreshold.collectAsState(initial = 0.80)
    val conservativeMode by viewModel.conservativeMode.collectAsState(initial = true)
    val analyticsEnabled by viewModel.analyticsEnabled.collectAsState(initial = true)
    val crashReportingEnabled by viewModel.crashReportingEnabled.collectAsState(initial = true)
    val loggingEnabled by viewModel.loggingEnabled.collectAsState(initial = false)
    val ocrEnabled by viewModel.ocrEnabled.collectAsState(initial = false)
    val ocrConfidenceThreshold by viewModel.ocrConfidenceThreshold.collectAsState(initial = 0.7f)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurações") }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // Thresholds de Rentabilidade
            ThresholdsSection(
                greenThreshold = greenThreshold,
                orangeThreshold = orangeThreshold,
                onGreenThresholdChange = { viewModel.setGreenThreshold(it) },
                onOrangeThresholdChange = { viewModel.setOrangeThreshold(it) }
            )

            // Modo de Detecção
            DetectionSection(
                conservativeMode = conservativeMode,
                ocrEnabled = ocrEnabled,
                ocrConfidenceThreshold = ocrConfidenceThreshold,
                onConservativeModeChange = { viewModel.setConservativeMode(it) },
                onOcrEnabledChange = { viewModel.setOcrEnabled(it) },
                onOcrConfidenceThresholdChange = { viewModel.setOcrConfidenceThreshold(it) }
            )

            // Privacidade e Analytics
            PrivacySection(
                analyticsEnabled = analyticsEnabled,
                crashReportingEnabled = crashReportingEnabled,
                loggingEnabled = loggingEnabled,
                onAnalyticsEnabledChange = { viewModel.setAnalyticsEnabled(it) },
                onCrashReportingEnabledChange = { viewModel.setCrashReportingEnabled(it) },
                onLoggingEnabledChange = { viewModel.setLoggingEnabled(it) },
                onClearAllData = {
                    scope.launch {
                        viewModel.clearAllData()
                        snackbarHostState.showSnackbar(
                            message = "Todos os dados foram apagados",
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            )

            // Sobre e Suporte
            AboutSection()

        }
    }
}

@Composable
private fun ThresholdsSection(
    greenThreshold: Double,
    orangeThreshold: Double,
    onGreenThresholdChange: (Double) -> Unit,
    onOrangeThresholdChange: (Double) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Thresholds de Rentabilidade",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Configure os valores de referência para as cores indicativas:",
                style = MaterialTheme.typography.bodyMedium
            )

            // Verde
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("🟢 Verde (R$/km ≥)", modifier = Modifier.weight(1f))
                OutlinedTextField(
                    value = greenThreshold.toString(),
                    onValueChange = { value ->
                        value.toDoubleOrNull()?.let { onGreenThresholdChange(it) }
                    },
                    modifier = Modifier.width(100.dp),
                    singleLine = true
                )
            }

            // Laranja
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("🟡 Laranja (R$/km ≥)", modifier = Modifier.weight(1f))
                OutlinedTextField(
                    value = orangeThreshold.toString(),
                    onValueChange = { value ->
                        value.toDoubleOrNull()?.let { onOrangeThresholdChange(it) }
                    },
                    modifier = Modifier.width(100.dp),
                    singleLine = true
                )
            }

            Text(
                text = "🔴 Vermelho: Abaixo do threshold laranja",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DetectionSection(
    conservativeMode: Boolean,
    ocrEnabled: Boolean,
    ocrConfidenceThreshold: Float,
    onConservativeModeChange: (Boolean) -> Unit,
    onOcrEnabledChange: (Boolean) -> Unit,
    onOcrConfidenceThresholdChange: (Float) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Modo de Detecção",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Modo Conservador
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Modo Conservador",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Reduz falsos positivos, pode perder algumas detecções",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = conservativeMode,
                    onCheckedChange = onConservativeModeChange
                )
            }

            // OCR Fallback
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "OCR Fallback",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Usa câmera para detectar quando accessibility falha",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = ocrEnabled,
                    onCheckedChange = onOcrEnabledChange
                )
            }

            // Threshold de confiança OCR
            if (ocrEnabled) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Confiança OCR Mínima", modifier = Modifier.weight(1f))
                    Text(
                        text = "${(ocrConfidenceThreshold * 100).toInt()}%",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Slider(
                        value = ocrConfidenceThreshold,
                        onValueChange = onOcrConfidenceThresholdChange,
                        valueRange = 0.5f..0.9f,
                        modifier = Modifier.width(120.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PrivacySection(
    analyticsEnabled: Boolean,
    crashReportingEnabled: Boolean,
    loggingEnabled: Boolean,
    onAnalyticsEnabledChange: (Boolean) -> Unit,
    onCrashReportingEnabledChange: (Boolean) -> Unit,
    onLoggingEnabledChange: (Boolean) -> Unit,
    onClearAllData: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Privacidade e Dados",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Analytics
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Analytics (Google Firebase)",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Dados anônimos sobre uso do app",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = analyticsEnabled,
                    onCheckedChange = onAnalyticsEnabledChange
                )
            }

            // Crash Reporting
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Relatórios de Erro",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Ajuda a melhorar o app identificando crashes",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = crashReportingEnabled,
                    onCheckedChange = onCrashReportingEnabledChange
                )
            }

            // Logging
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Logs Locais",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Salva dados técnicos para diagnóstico (opt-in)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = loggingEnabled,
                    onCheckedChange = onLoggingEnabledChange
                )
            }

            // Apagar dados
            OutlinedButton(
                onClick = onClearAllData,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Apagar Todos os Dados")
            }

            Text(
                text = "Remove todas as configurações e dados locais (conforme LGPD)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun AboutSection() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Sobre o App",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "KM County v1.0.0",
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = "Aplicativo open-source para auxiliar motoristas de aplicativo no cálculo de rentabilidade de corridas.",
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = { /* TODO: Abrir política de privacidade */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Privacidade")
                }

                OutlinedButton(
                    onClick = { /* TODO: Abrir repositório */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Código Fonte")
                }
            }
        }
    }
}
