package com.kmcounty.app.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.kmcounty.core.RideDetector
import com.kmcounty.core.model.ParsedRide
import com.kmcounty.app.overlay.OverlayManager
import com.kmcounty.app.data.logging.RideLogRepository
import com.kmcounty.app.data.preferences.UserPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Serviço de Acessibilidade principal do KM County.
 *
 * Este serviço monitora mudanças na tela para detectar telas de pedido de corrida
 * em aplicativos de transporte como Uber, 99, etc.
 *
 * IMPORTANTE: Este serviço APENAS LÊ informações da tela. NUNCA executa ações
 * automáticas nos outros aplicativos.
 */
class RideAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "RideAccessibilityService"
        private const val RIDE_REQUEST_TIMEOUT_MS = 30000L // 30 segundos
    }

    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())
    private lateinit var rideDetector: RideDetector
    private lateinit var overlayManager: OverlayManager
    private lateinit var userPreferences: UserPreferences
    private lateinit var rideLogRepository: RideLogRepository

    private var lastRideDetectionTime = 0L
    private var currentRideData: ParsedRide? = null
    private var currentScreenTexts: List<String> = emptyList()

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "RideAccessibilityService criado")

        rideDetector = RideDetector()
        overlayManager = OverlayManager(this)
        userPreferences = UserPreferences(this)
        rideLogRepository = RideLogRepository(this)
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "RideAccessibilityService conectado")

        // Configurar o serviço de acessibilidade
        val info = AccessibilityServiceInfo().apply {
            // Eventos que queremos monitorar
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or
                        AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED or
                        AccessibilityEvent.TYPE_VIEW_SCROLLED

            // Tipos de feedback
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC

            // Flags de configuração
            flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or
                   AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS or
                   AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS

            // Timeout para notificações
            notificationTimeout = 100

            // Pacotes que queremos monitorar (apps de transporte conhecidos)
            packageNames = arrayOf(
                "com.ubercab.driver",           // Uber Driver
                "com.uber.driver",              // Uber Driver (alternativo)
                "com.cabify.driver",            // Cabify Driver
                "com.taxibeat.driver",          // TaxiBeat Driver
                "com.nine.ninetynine.driver",   // 99 Driver
                "com.didi.passenger",           // DiDi
                "com.inDriver.driver",          // inDriver
                // Adicionar mais conforme necessário
            )
        }

        serviceInfo = info
        Log.d(TAG, "Serviço configurado com ${packageNames?.size ?: 0} pacotes monitorados")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val packageName = event.packageName?.toString() ?: return
        val eventType = event.eventType

        // Log detalhado apenas em modo debug
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "Evento recebido: $eventType de $packageName")
        }

        // Processar evento baseado no tipo
        when (eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                handleWindowStateChanged(event, packageName)
            }
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                handleWindowContentChanged(event, packageName)
            }
            AccessibilityEvent.TYPE_VIEW_SCROLLED -> {
                handleViewScrolled(event, packageName)
            }
        }
    }

    private fun handleWindowStateChanged(event: AccessibilityEvent, packageName: String) {
        // Verificar se é uma nova janela que pode conter pedido de corrida
        val windowTitle = event.text?.joinToString(" ") ?: ""
        val className = event.className?.toString() ?: ""

        Log.d(TAG, "Nova janela: $windowTitle (classe: $className)")

        // Verificar se pode ser uma tela de pedido baseada no título/classe
        if (isPotentialRideRequestWindow(windowTitle, className)) {
            // Obter conteúdo atual da tela
            serviceScope.launch {
                analyzeCurrentScreen(packageName)
            }
        }
    }

    private fun handleWindowContentChanged(event: AccessibilityEvent, packageName: String) {
        // Conteúdo da janela mudou - pode ser atualização de pedido
        val currentTime = System.currentTimeMillis()

        // Evitar processamento excessivo (limitar a 2 análises por segundo)
        if (currentTime - lastRideDetectionTime < 500) {
            return
        }

        lastRideDetectionTime = currentTime

        serviceScope.launch {
            analyzeCurrentScreen(packageName)
        }
    }

    private fun handleViewScrolled(event: AccessibilityEvent, packageName: String) {
        // Scroll pode indicar carregamento de novo conteúdo
        // Análise mais leve para não sobrecarregar
        val currentTime = System.currentTimeMillis()

        if (currentTime - lastRideDetectionTime < 2000) { // 2 segundos
            return
        }

        serviceScope.launch {
            quickScreenAnalysis(packageName)
        }
    }

    /**
     * Análise completa da tela atual para detectar pedidos de corrida
     */
    private suspend fun analyzeCurrentScreen(packageName: String) {
        try {
            val rootNode = rootInActiveWindow ?: return

            // Extrair todo o texto visível da tela
            val screenTexts = extractTextFromNode(rootNode)

            if (screenTexts.isEmpty()) {
                Log.d(TAG, "Nenhum texto encontrado na tela")
                return
            }

            // Usar o detector para analisar os textos
            val rideData = rideDetector.detectRide(screenTexts)

            // Armazenar textos atuais para possível uso posterior
            currentScreenTexts = screenTexts

            if (rideData != null) {
                Log.i(TAG, "Pedido de corrida detectado: R$ ${rideData.price} para ${rideData.distance}km")

                currentRideData = rideData
                overlayManager.showRideOverlay(rideData)

                // Logging opt-in: salvar dados se usuário permitiu
                serviceScope.launch {
                    try {
                        val loggingEnabled = userPreferences.isLoggingEnabled().first()
                        if (loggingEnabled) {
                            rideLogRepository.insertLog(
                                ride = rideData,
                                appPackage = packageName,
                                rawText = screenTexts.joinToString(" | ")
                            )
                            Log.d(TAG, "Dados de corrida salvos em log (opt-in)")
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Erro ao salvar log opt-in", e)
                    }
                }

                // Agendar ocultação automática após timeout
                scheduleOverlayHide()
            } else {
                // Se não há mais pedido ativo, ocultar overlay
                if (currentRideData != null) {
                    Log.d(TAG, "Pedido de corrida não mais detectado, ocultando overlay")
                    currentRideData = null
                    overlayManager.hideOverlay()
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "Erro ao analisar tela", e)
        }
    }

    /**
     * Análise rápida da tela (usada para eventos de scroll)
     */
    private suspend fun quickScreenAnalysis(packageName: String) {
        try {
            val rootNode = rootInActiveWindow ?: return
            val screenTexts = extractTextFromNode(rootNode)

            // Verificação rápida se ainda há pedido ativo
            val hasActiveRide = rideDetector.hasActiveRide(screenTexts)

            if (!hasActiveRide && currentRideData != null) {
                Log.d(TAG, "Pedido não mais ativo (análise rápida)")
                currentRideData = null
                overlayManager.hideOverlay()
            }

        } catch (e: Exception) {
            Log.e(TAG, "Erro na análise rápida", e)
        }
    }

    /**
     * Extrai texto recursivamente de um nó de acessibilidade
     */
    private fun extractTextFromNode(node: AccessibilityNodeInfo): List<String> {
        val texts = mutableListOf<String>()

        // Adicionar texto do nó atual
        node.text?.toString()?.let { text ->
            if (text.isNotBlank() && text.length > 1) { // Ignorar textos muito curtos
                texts.add(text)
            }
        }

        // Adicionar textos dos filhos (recursivamente, mas limitar profundidade)
        for (i in 0 until node.childCount) {
            try {
                node.getChild(i)?.let { child ->
                    texts.addAll(extractTextFromNode(child))
                    child.recycle()
                }
            } catch (e: Exception) {
                // Ignorar erros ao acessar filhos
                Log.w(TAG, "Erro ao acessar filho do nó", e)
            }
        }

        return texts
    }

    /**
     * Verifica se a janela atual pode conter um pedido de corrida
     */
    private fun isPotentialRideRequestWindow(windowTitle: String, className: String): Boolean {
        val lowerTitle = windowTitle.lowercase()
        val lowerClass = className.lowercase()

        // Palavras-chave que indicam possível tela de pedido
        val keywords = listOf(
            "corrida", "ride", "viagem", "trip", "pedido", "request",
            "passageiro", "passenger", "destino", "destination",
            "valor", "price", "preço", "fare"
        )

        return keywords.any { keyword ->
            lowerTitle.contains(keyword) || lowerClass.contains(keyword)
        }
    }

    /**
     * Agenda a ocultação automática do overlay após timeout
     */
    private fun scheduleOverlayHide() {
        serviceScope.launch {
            kotlinx.coroutines.delay(RIDE_REQUEST_TIMEOUT_MS)
            // Verificar se ainda é o mesmo pedido
            if (currentRideData != null) {
                Log.d(TAG, "Timeout do overlay expirado, ocultando")
                currentRideData = null
                overlayManager.hideOverlay()
            }
        }
    }

    override fun onInterrupt() {
        Log.w(TAG, "Serviço de acessibilidade interrompido")
        // Limpar estado
        currentRideData = null
        overlayManager.hideOverlay()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "RideAccessibilityService destruído")

        // Limpar recursos
        serviceScope.cancel()
        overlayManager.destroy()
    }
}
