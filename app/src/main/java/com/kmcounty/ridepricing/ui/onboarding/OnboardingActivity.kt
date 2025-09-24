package com.kmcounty.ridepricing.ui.onboarding

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kmcounty.ridepricing.R
import com.kmcounty.ridepricing.ui.theme.RidePricingTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Onboarding activity that guides users through initial setup
 */
@AndroidEntryPoint
class OnboardingActivity : ComponentActivity() {

    private val viewModel: OnboardingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RidePricingTheme {
                OnboardingScreen(
                    viewModel = viewModel,
                    onComplete = { 
                        setResult(RESULT_OK)
                        finish()
                    },
                    onCancel = {
                        setResult(RESULT_CANCELED)
                        finish()
                    },
                    onOpenAccessibilitySettings = { openAccessibilitySettings() },
                    onOpenOverlaySettings = { openOverlaySettings() }
                )
            }
        }
    }

    private fun openAccessibilitySettings() {
        try {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
        } catch (e: Exception) {
            // Fallback to general settings
            val intent = Intent(Settings.ACTION_SETTINGS)
            startActivity(intent)
        }
    }

    private fun openOverlaySettings() {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
            }
        } catch (e: Exception) {
            // Fallback to general settings
            val intent = Intent(Settings.ACTION_SETTINGS)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkPermissions()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onComplete: () -> Unit,
    onCancel: () -> Unit,
    onOpenAccessibilitySettings: () -> Unit,
    onOpenOverlaySettings: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { 4 })
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState.canComplete) {
        if (uiState.canComplete && pagerState.currentPage == 3) {
            // Auto-advance to completion when all permissions are granted
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Progress indicator
        LinearProgressIndicator(
            progress = { (pagerState.currentPage + 1) / 4f },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Pager content
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> WelcomePage()
                1 -> PrivacyPage()
                2 -> PermissionsPage(
                    uiState = uiState,
                    onOpenAccessibilitySettings = onOpenAccessibilitySettings,
                    onOpenOverlaySettings = onOpenOverlaySettings
                )
                3 -> CompletePage(canComplete = uiState.canComplete)
            }
        }

        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (pagerState.currentPage > 0) {
                TextButton(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    }
                ) {
                    Text(stringResource(R.string.back))
                }
            } else {
                TextButton(onClick = onCancel) {
                    Text(stringResource(R.string.cancel))
                }
            }

            if (pagerState.currentPage < 3) {
                Button(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                ) {
                    Text(stringResource(R.string.next))
                }
            } else {
                Button(
                    onClick = onComplete,
                    enabled = uiState.canComplete
                ) {
                    Text(stringResource(R.string.complete))
                }
            }
        }
    }
}

@Composable
private fun WelcomePage() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Speed,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.onboarding_welcome_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.onboarding_welcome_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FeatureItem(
                    icon = Icons.Default.Visibility,
                    title = stringResource(R.string.feature_read_only),
                    description = stringResource(R.string.feature_read_only_desc)
                )
                
                FeatureItem(
                    icon = Icons.Default.Security,
                    title = stringResource(R.string.feature_privacy),
                    description = stringResource(R.string.feature_privacy_desc)
                )
                
                FeatureItem(
                    icon = Icons.Default.Calculate,
                    title = stringResource(R.string.feature_calculation),
                    description = stringResource(R.string.feature_calculation_desc)
                )
            }
        }
    }
}

@Composable
private fun PrivacyPage() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.PrivacyTip,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.onboarding_privacy_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.privacy_commitment),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )

                PrivacyPoint(stringResource(R.string.privacy_point_1))
                PrivacyPoint(stringResource(R.string.privacy_point_2))
                PrivacyPoint(stringResource(R.string.privacy_point_3))
                PrivacyPoint(stringResource(R.string.privacy_point_4))
                PrivacyPoint(stringResource(R.string.privacy_point_5))
            }
        }
    }
}

@Composable
private fun PermissionsPage(
    uiState: OnboardingUiState,
    onOpenAccessibilitySettings: () -> Unit,
    onOpenOverlaySettings: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Security,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.onboarding_permissions_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.onboarding_permissions_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Accessibility Permission
        PermissionCard(
            title = stringResource(R.string.accessibility_permission),
            description = stringResource(R.string.accessibility_permission_desc),
            isGranted = uiState.hasAccessibilityPermission,
            buttonText = stringResource(R.string.enable_accessibility),
            onButtonClick = onOpenAccessibilitySettings
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Overlay Permission
        PermissionCard(
            title = stringResource(R.string.overlay_permission),
            description = stringResource(R.string.overlay_permission_desc),
            isGranted = uiState.hasOverlayPermission,
            buttonText = stringResource(R.string.enable_overlay),
            onButtonClick = onOpenOverlaySettings
        )
    }
}

@Composable
private fun CompletePage(canComplete: Boolean) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (canComplete) Icons.Default.CheckCircle else Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = if (canComplete) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (canComplete) 
                stringResource(R.string.onboarding_complete_title) 
            else 
                stringResource(R.string.onboarding_incomplete_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (canComplete) 
                stringResource(R.string.onboarding_complete_subtitle) 
            else 
                stringResource(R.string.onboarding_incomplete_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (canComplete) {
            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.ready_to_start),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.ready_to_start_desc),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun FeatureItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PrivacyPoint(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "•",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun PermissionCard(
    title: String,
    description: String,
    isGranted: Boolean,
    buttonText: String,
    onButtonClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isGranted) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = if (isGranted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (isGranted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (!isGranted) {
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onButtonClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(buttonText)
                }
            }
        }
    }
}
