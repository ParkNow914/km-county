package com.kmcounty.app.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kmcounty.app.R
import com.kmcounty.app.ui.onboarding.OnboardingActivity
import com.kmcounty.app.ui.settings.SettingsActivity
import com.kmcounty.app.ui.theme.KMCountyTheme
import com.kmcounty.app.ui.main.viewmodel.MainViewModel

/**
 * Atividade principal do aplicativo KM County
 * Mostra status dos serviços e permite acesso às configurações
 */
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Verificar se onboarding foi completado
        checkOnboardingStatus()

        setContent {
            KMCountyTheme {
                MainScreen(
                    viewModel = viewModel,
                    onSettingsClick = { openSettings() },
                    onAccessibilitySettingsClick = { openAccessibilitySettings() },
                    onOverlaySettingsClick = { openOverlaySettings() }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Atualizar status dos serviços quando voltar à tela
        viewModel.checkServiceStatus()
    }

    private fun checkOnboardingStatus() {
        // Se onboarding não foi completado, redirecionar
        if (!viewModel.isOnboardingCompleted()) {
            val intent = Intent(this, OnboardingActivity::class.java)
            startActivity(intent)
            finish()
            return
        }
    }

    private fun openSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    private fun openAccessibilitySettings() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(intent)
    }

    private fun openOverlaySettings() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
            data = Uri.parse("package:$packageName")
        }
        startActivity(intent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel(),
    onSettingsClick: () -> Unit,
    onAccessibilitySettingsClick: () -> Unit,
    onOverlaySettingsClick: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Estados do ViewModel
    val accessibilityEnabled by viewModel.accessibilityEnabled.collectAsState(initial = false)
    val overlayEnabled by viewModel.overlayEnabled.collectAsState(initial = false)
    val isServiceRunning by viewModel.isServiceRunning.collectAsState(initial = false)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("KM County") },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Text("⚙️") // TODO: Usar ícone adequado
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // Status dos Serviços
            ServiceStatusSection(
                accessibilityEnabled = accessibilityEnabled,
                overlayEnabled = overlayEnabled,
                isServiceRunning = isServiceRunning,
                onAccessibilitySettingsClick = onAccessibilitySettingsClick,
                onOverlaySettingsClick = onOverlaySettingsClick
            )

            // Como Usar
            UsageInstructionsSection()

            // Status Atual
            CurrentStatusSection(
                accessibilityEnabled = accessibilityEnabled,
                overlayEnabled = overlayEnabled,
                isServiceRunning = isServiceRunning
            )

            // Links Úteis
            UsefulLinksSection()

        }
    }
}

@Composable
private fun ServiceStatusSection(
    accessibilityEnabled: Boolean,
    overlayEnabled: Boolean,
    isServiceRunning: Boolean,
    onAccessibilitySettingsClick: () -> Unit,
    onOverlaySettingsClick: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Status dos Serviços",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Acessibilidade
            ServiceStatusItem(
                title = "Acessibilidade",
                description = "Permite detectar pedidos de corrida",
                isEnabled = accessibilityEnabled,
                onSettingsClick = onAccessibilitySettingsClick
            )

            // Overlay
            ServiceStatusItem(
                title = "Sobrepor Apps",
                description = "Permite mostrar cálculos flutuantes",
                isEnabled = overlayEnabled,
                onSettingsClick = onOverlaySettingsClick
            )

            // Serviço de Detecção
            ServiceStatusItem(
                title = "Detecção Ativa",
                description = "Serviço monitorando em segundo plano",
                isEnabled = isServiceRunning,
                onSettingsClick = null
            )
        }
    }
}

@Composable
private fun ServiceStatusItem(
    title: String,
    description: String,
    isEnabled: Boolean,
    onSettingsClick: (() -> Unit)?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isEnabled) "🟢" else "🔴",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 24.dp)
            )
        }

        if (onSettingsClick != null && !isEnabled) {
            OutlinedButton(onClick = onSettingsClick) {
                Text("Configurar")
            }
        }
    }
}

@Composable
private fun UsageInstructionsSection() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Como Usar",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "1. Certifique-se de que ambos os serviços estão ativos (marcados em verde)",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "2. Abra seu app de transporte preferido (Uber, 99, etc.)",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "3. Quando aparecer um pedido de corrida, o KM County automaticamente mostrará um overlay com R$/km e R$/min",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "4. Use as cores como guia para decidir aceitar ou recusar:",
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ColorGuideItem("🟢 Verde", "Rentável")
                ColorGuideItem("🟡 Laranja", "Médio")
                ColorGuideItem("🔴 Vermelho", "Pouco rentável")
            }
        }
    }
}

@Composable
private fun ColorGuideItem(colorText: String, description: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = colorText,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CurrentStatusSection(
    accessibilityEnabled: Boolean,
    overlayEnabled: Boolean,
    isServiceRunning: Boolean
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Status Atual",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            when {
                !accessibilityEnabled && !overlayEnabled -> {
                    Text(
                        text = "❌ Serviços necessários não estão ativos",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "Configure as permissões necessárias para começar a usar o app.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                !accessibilityEnabled -> {
                    Text(
                        text = "⚠️ Acessibilidade não está ativa",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "Ative o serviço de acessibilidade para detectar pedidos de corrida.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                !overlayEnabled -> {
                    Text(
                        text = "⚠️ Overlay não está ativo",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "Permita sobrepor outros apps para ver os cálculos flutuantes.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                isServiceRunning -> {
                    Text(
                        text = "✅ Tudo funcionando!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "O KM County está monitorando em segundo plano. Abra seu app de transporte.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                else -> {
                    Text(
                        text = "⏳ Aguardando ativação dos serviços",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "Os serviços serão ativados automaticamente quando você conceder as permissões.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun UsefulLinksSection() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Links Úteis",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            OutlinedButton(
                onClick = { /* TODO: Abrir política de privacidade */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Política de Privacidade")
            }

            OutlinedButton(
                onClick = { /* TODO: Abrir repositório */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Código Fonte (GitHub)")
            }

            OutlinedButton(
                onClick = { /* TODO: Abrir configurações */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Configurações Avançadas")
            }
        }
    }
}
