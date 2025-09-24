package com.kmcounty.ridepricing.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kmcounty.ridepricing.R

/**
 * Main screen of the application
 * 
 * Shows the current status of services and ride information
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    uiState: MainUiState,
    onNavigateToSettings: () -> Unit,
    onNavigateToPrivacyPolicy: () -> Unit,
    onToggleService: () -> Unit,
    onToggleOverlay: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Header
        AppHeader()

        // Service Status
        ServiceStatusCard(
            uiState = uiState,
            onToggleService = onToggleService,
            onToggleOverlay = onToggleOverlay
        )

        // Current Ride Info
        if (uiState.currentRideInfo != null) {
            CurrentRideCard(rideInfo = uiState.currentRideInfo)
        } else {
            NoRideDetectedCard()
        }

        // Quick Actions
        QuickActionsCard(
            onNavigateToSettings = onNavigateToSettings,
            onNavigateToPrivacyPolicy = onNavigateToPrivacyPolicy
        )

        // Error Message
        if (uiState.errorMessage != null) {
            ErrorCard(message = uiState.errorMessage)
        }
    }
}

@Composable
private fun AppHeader() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Speed,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = stringResource(R.string.app_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ServiceStatusCard(
    uiState: MainUiState,
    onToggleService: () -> Unit,
    onToggleOverlay: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.service_status_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Overall Status Indicator
            val (statusText, statusColor, statusIcon) = when {
                uiState.isFullyConfigured && uiState.isServiceEnabled && uiState.isOverlayEnabled -> {
                    Triple(
                        stringResource(R.string.status_active),
                        MaterialTheme.colorScheme.primary,
                        Icons.Default.CheckCircle
                    )
                }
                uiState.isFullyConfigured -> {
                    Triple(
                        stringResource(R.string.status_ready),
                        MaterialTheme.colorScheme.secondary,
                        Icons.Default.PlayArrow
                    )
                }
                else -> {
                    Triple(
                        stringResource(R.string.status_setup_needed),
                        MaterialTheme.colorScheme.error,
                        Icons.Default.Warning
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = statusIcon,
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = statusText,
                    color = statusColor,
                    fontWeight = FontWeight.Medium
                )
            }

            Divider()

            // Individual Service Status
            ServiceStatusRow(
                title = stringResource(R.string.accessibility_service),
                isEnabled = uiState.isServiceEnabled,
                hasPermission = uiState.hasAccessibilityPermission,
                onToggle = onToggleService
            )

            ServiceStatusRow(
                title = stringResource(R.string.overlay_service),
                isEnabled = uiState.isOverlayEnabled,
                hasPermission = uiState.hasOverlayPermission,
                onToggle = onToggleOverlay
            )
        }
    }
}

@Composable
private fun ServiceStatusRow(
    title: String,
    isEnabled: Boolean,
    hasPermission: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = when {
                    !hasPermission -> stringResource(R.string.permission_needed)
                    isEnabled -> stringResource(R.string.service_active)
                    else -> stringResource(R.string.service_inactive)
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Switch(
            checked = isEnabled,
            onCheckedChange = { onToggle() },
            enabled = hasPermission
        )
    }
}

@Composable
private fun CurrentRideCard(rideInfo: RideInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = getPriceColor(rideInfo.pricePerKm)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.current_ride),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Icon(
                    imageVector = Icons.Default.DirectionsCar,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PriceMetric(
                    label = stringResource(R.string.price_per_km),
                    value = "R$ %.2f".format(rideInfo.pricePerKm),
                    modifier = Modifier.weight(1f)
                )

                if (rideInfo.pricePerMinute != null) {
                    PriceMetric(
                        label = stringResource(R.string.price_per_minute),
                        value = "R$ %.2f".format(rideInfo.pricePerMinute),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Divider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                InfoMetric(
                    label = stringResource(R.string.total_price),
                    value = "R$ %.2f".format(rideInfo.totalPrice)
                )
                
                InfoMetric(
                    label = stringResource(R.string.distance),
                    value = "%.1f km".format(rideInfo.distance)
                )

                if (rideInfo.estimatedTime != null) {
                    InfoMetric(
                        label = stringResource(R.string.estimated_time),
                        value = "${rideInfo.estimatedTime} min"
                    )
                }
            }

            if (rideInfo.confidence < 0.8f) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.low_confidence_warning),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun NoRideDetectedCard() {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = stringResource(R.string.no_ride_detected),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = stringResource(R.string.no_ride_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun QuickActionsCard(
    onNavigateToSettings: () -> Unit,
    onNavigateToPrivacyPolicy: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.quick_actions),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateToSettings,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.settings))
                }

                OutlinedButton(
                    onClick = onNavigateToPrivacyPolicy,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.PrivacyTip,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.privacy))
                }
            }
        }
    }
}

@Composable
private fun ErrorCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun PriceMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun InfoMetric(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun getPriceColor(pricePerKm: Float) = when {
    pricePerKm >= 1.50f -> MaterialTheme.colorScheme.primaryContainer
    pricePerKm >= 0.80f -> MaterialTheme.colorScheme.secondaryContainer
    else -> MaterialTheme.colorScheme.errorContainer
}
