package com.kmcounty.app.ui.onboarding

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
import androidx.core.content.ContextCompat
import com.kmcounty.app.R
import com.kmcounty.app.ui.MainActivity
import com.kmcounty.app.ui.theme.KMCountyTheme

/**
 * Atividade de onboarding obrigatória que explica como o app funciona
 * e guia o usuário através das permissões necessárias.
 */
class OnboardingActivity : AppCompatActivity() {

    private val viewModel: OnboardingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            KMCountyTheme {
                OnboardingScreen(
                    onFinishOnboarding = { finishOnboarding() },
                    onRequestAccessibilityPermission = { requestAccessibilityPermission() },
                    onRequestOverlayPermission = { requestOverlayPermission() }
                )
            }
        }
    }

    private fun finishOnboarding() {
        // Marcar onboarding como completo
        viewModel.markOnboardingCompleted()

        // Ir para tela principal
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun requestAccessibilityPermission() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(intent)
    }

    private fun requestOverlayPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
            data = Uri.parse("package:$packageName")
        }
        startActivity(intent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onFinishOnboarding: () -> Unit,
    onRequestAccessibilityPermission: () -> Unit,
    onRequestOverlayPermission: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("KM County - Configuração Inicial") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // Título principal
            Text(
                text = "Assistente R$/km — Atenção e Consentimento",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            // Explicação principal
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Como funciona:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Este aplicativo apenas LÊ informações visíveis na tela para calcular R$/km e R$/min e te ajudar a avaliar se a corrida vale a pena.\n\n" +
                              "O app NÃO automatiza aceitar ou recusar corridas. Todo processamento é feito LOCALMENTE no seu aparelho. " +
                              "Nenhum dado é enviado a servidores por padrão. Você pode desativar logs a qualquer momento e apagar todos os dados.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Justify
                    )
                }
            }

            // Permissões necessárias
            PermissionSection(
                title = "Permissão de Acessibilidade",
                description = "Permite que o app leia informações da tela dos apps de transporte (Uber, 99, etc.) para identificar pedidos de corrida e extrair valores.",
                buttonText = "Configurar Acessibilidade",
                onButtonClick = onRequestAccessibilityPermission
            )

            PermissionSection(
                title = "Permissão de Sobrepor Apps",
                description = "Permite mostrar um overlay discreto com os cálculos de R$/km quando um pedido é detectado.",
                buttonText = "Configurar Overlay",
                onButtonClick = onRequestOverlayPermission
            )

            // Avisos legais
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "⚠️ Avisos Importantes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )

                    Text(
                        text = "• Este app é uma ferramenta auxiliar - você é responsável por todas as decisões de corrida\n" +
                              "• Não use para decisões automatizadas sem supervisão humana\n" +
                              "• Recomendamos testar em corridas curtas primeiro\n" +
                              "• O app respeita sua privacidade e não coleta dados pessoais",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            // Botão de conclusão
            Button(
                onClick = onFinishOnboarding,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = true // TODO: Verificar se permissões foram concedidas
            ) {
                Text(
                    text = "Entendi e quero ativar",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // Link para política de privacidade
            TextButton(
                onClick = {
                    // TODO: Abrir política de privacidade
                }
            ) {
                Text("Ver Política de Privacidade")
            }
        }
    }
}

@Composable
private fun PermissionSection(
    title: String,
    description: String,
    buttonText: String,
    onButtonClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify
            )

            OutlinedButton(
                onClick = onButtonClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(buttonText)
            }
        }
    }
}
