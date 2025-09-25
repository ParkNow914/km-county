package com.kmcounty.app.overlay

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.kmcounty.app.R
import com.kmcounty.core.model.ParsedRide
import kotlinx.coroutines.*

/**
 * Gerenciador do overlay flutuante que mostra informações de R$/km e R$/min
 *
 * Responsável por criar, mostrar, atualizar e remover o overlay do sistema
 * usando WindowManager com TYPE_APPLICATION_OVERLAY.
 */
class OverlayManager(private val context: Context) {

    companion object {
        private const val TAG = "OverlayManager"
        private const val OVERLAY_HIDE_DELAY_MS = 30000L // 30 segundos
    }

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    private var overlayView: View? = null
    private var hideJob: Job? = null

    /**
     * Mostra o overlay com informações do pedido de corrida
     */
    fun showRideOverlay(rideData: ParsedRide) {
        scope.launch {
            try {
                // Cancelar ocultação anterior se existir
                hideJob?.cancel()

                // Remover overlay existente se houver
                hideOverlay()

                // Criar novo overlay
                createOverlayView(rideData)

                // Agendar ocultação automática
                scheduleAutoHide()

            } catch (e: Exception) {
                android.util.Log.e(TAG, "Erro ao mostrar overlay", e)
            }
        }
    }

    /**
     * Oculta o overlay atual
     */
    fun hideOverlay() {
        scope.launch {
            try {
                overlayView?.let { view ->
                    windowManager.removeView(view)
                    overlayView = null
                }

                hideJob?.cancel()
                hideJob = null

            } catch (e: Exception) {
                android.util.Log.e(TAG, "Erro ao ocultar overlay", e)
            }
        }
    }

    /**
     * Cria a view do overlay
     */
    private fun createOverlayView(rideData: ParsedRide) {
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.overlay_ride_info, null)

        // Configurar textos
        view.findViewById<TextView>(R.id.overlay_price_per_km)?.text =
            "R$/km: R$ %.2f".format(rideData.pricePerKm)

        view.findViewById<TextView>(R.id.overlay_price_per_minute)?.text =
            if (rideData.time > 0) "R$/min: R$ %.2f".format(rideData.pricePerMinute) else ""

        view.findViewById<TextView>(R.id.overlay_confidence)?.text =
            "Confiança: %.0f%%".format(rideData.confidence * 100)

        // Configurar cor de fundo baseada na rentabilidade
        val backgroundColor = when (rideData.getRecommendedColor()) {
            ParsedRide.RideColor.GREEN -> android.graphics.Color.parseColor("#4CAF50")  // Verde
            ParsedRide.RideColor.ORANGE -> android.graphics.Color.parseColor("#FF9800") // Laranja
            ParsedRide.RideColor.RED -> android.graphics.Color.parseColor("#F44336")    // Vermelho
        }
        view.setBackgroundColor(backgroundColor)

        // Configurar clique para expandir/recolher
        view.setOnClickListener {
            toggleExpandedView(view, rideData)
        }

        // Parâmetros da janela
        val layoutParams = WindowManager.LayoutParams().apply {
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            }
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                   WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                   WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
            format = PixelFormat.TRANSLUCENT
            gravity = Gravity.TOP or Gravity.END
            x = 16 // Margem da direita
            y = 100 // Margem do topo
        }

        // Adicionar à tela
        windowManager.addView(view, layoutParams)
        overlayView = view
    }

    /**
     * Alterna entre visualização compacta e expandida
     */
    private fun toggleExpandedView(view: View, rideData: ParsedRide) {
        val expandedView = view.findViewById<View>(R.id.overlay_expanded_content)
        val isExpanded = expandedView?.visibility == View.VISIBLE

        if (isExpanded) {
            // Recolher
            expandedView?.visibility = View.GONE
            view.findViewById<TextView>(R.id.overlay_price_per_km)?.visibility = View.VISIBLE
        } else {
            // Expandir
            expandedView?.visibility = View.VISIBLE
            view.findViewById<TextView>(R.id.overlay_price_per_km)?.visibility = View.GONE

            // Preencher dados expandidos
            expandedView?.findViewById<TextView>(R.id.expanded_price_total)?.text =
                "Valor Total: R$ %.2f".format(rideData.price)

            expandedView?.findViewById<TextView>(R.id.expanded_distance)?.text =
                "Distância: %.1f km".format(rideData.distance)

            expandedView?.findViewById<TextView>(R.id.expanded_time)?.text =
                if (rideData.time > 0) "Tempo: %d min".format(rideData.time) else "Tempo: N/A"

            expandedView?.findViewById<TextView>(R.id.expanded_source)?.text =
                "Fonte: ${rideData.source}"
        }
    }

    /**
     * Agenda a ocultação automática do overlay
     */
    private fun scheduleAutoHide() {
        hideJob = scope.launch {
            delay(OVERLAY_HIDE_DELAY_MS)
            hideOverlay()
        }
    }

    /**
     * Verifica se o overlay está atualmente visível
     */
    fun isOverlayVisible(): Boolean = overlayView != null

    /**
     * Destrói o gerenciador e limpa recursos
     */
    fun destroy() {
        scope.cancel()
        hideOverlay()
    }
}
