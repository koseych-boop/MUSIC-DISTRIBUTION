package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.UserRole
import com.example.ui.components.SawtTopAppBar
import com.example.ui.localization.Strings
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.ArtistDashboardScreen
import com.example.ui.screens.ArtistProfileScreen
import com.example.ui.screens.CreateReleaseScreen
import com.example.ui.screens.ReleaseDetailScreen
import com.example.ui.screens.RoyaltiesScreen
import com.example.ui.theme.MoroccanGold
import com.example.ui.theme.MoroccanRed
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SawtCardBorder
import com.example.ui.theme.SawtObsidian
import com.example.ui.theme.SawtSurface
import com.example.ui.theme.SawtTextMuted
import com.example.ui.theme.SawtTextPrimary
import com.example.ui.viewmodel.ArtistScreen
import com.example.ui.viewmodel.DistributionViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: DistributionViewModel = viewModel()
                SawtApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun SawtApp(viewModel: DistributionViewModel) {
    val currentRole by viewModel.currentRole.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val isSandboxMode by viewModel.isSandboxMode.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()
    val selectedRelease by viewModel.selectedRelease.collectAsState()

    val releases by viewModel.releases.collectAsState()
    val artistProfile by viewModel.artistProfile.collectAsState()
    val artistBalance by viewModel.artistBalance.collectAsState()
    val royaltyReports by viewModel.royaltyReports.collectAsState()
    val payouts by viewModel.payouts.collectAsState()
    val allPayouts by viewModel.allPayouts.collectAsState()
    val allJobs by viewModel.allJobs.collectAsState()
    val allWebhooks by viewModel.allWebhooks.collectAsState()
    val allTakedowns by viewModel.allTakedowns.collectAsState()
    val allAuditLogs by viewModel.allAuditLogs.collectAsState()

    // Handle Android system back press
    BackHandler(enabled = currentScreen != ArtistScreen.DASHBOARD) {
        viewModel.navigateTo(ArtistScreen.DASHBOARD)
    }

    // Support Moroccan Arabic RTL (Right-to-Left) direction
    val layoutDirection = if (currentLanguage.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Scaffold(
            topBar = {
                SawtTopAppBar(
                    currentRole = currentRole,
                    onRoleChange = { viewModel.setRole(it) },
                    currentLanguage = currentLanguage,
                    onLanguageChange = { viewModel.setLanguage(it) },
                    isSandboxMode = isSandboxMode,
                    onToggleSandbox = { viewModel.toggleSandbox() }
                )
            },
            bottomBar = {
                if (currentRole == UserRole.ARTIST && currentScreen != ArtistScreen.CREATE_RELEASE) {
                    NavigationBar(
                        containerColor = SawtSurface,
                        contentColor = MoroccanGold,
                        tonalElevation = 8.dp,
                        modifier = Modifier.testTag("artist_bottom_navigation")
                    ) {
                        NavigationBarItem(
                            selected = currentScreen == ArtistScreen.DASHBOARD || currentScreen == ArtistScreen.DETAIL,
                            onClick = { viewModel.navigateTo(ArtistScreen.DASHBOARD) },
                            icon = { Icon(Icons.Default.Album, contentDescription = null, modifier = Modifier.size(20.dp)) },
                            label = { Text(Strings.myReleases(currentLanguage), fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MoroccanRed,
                                selectedTextColor = MoroccanGold,
                                unselectedIconColor = SawtTextMuted,
                                unselectedTextColor = SawtTextMuted,
                                indicatorColor = MoroccanRed.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.testTag("nav_releases")
                        )

                        NavigationBarItem(
                            selected = currentScreen == ArtistScreen.ROYALTIES,
                            onClick = { viewModel.navigateTo(ArtistScreen.ROYALTIES) },
                            icon = { Icon(Icons.Default.MonetizationOn, contentDescription = null, modifier = Modifier.size(20.dp)) },
                            label = { Text(Strings.royalties(currentLanguage), fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MoroccanGold,
                                selectedTextColor = MoroccanGold,
                                unselectedIconColor = SawtTextMuted,
                                unselectedTextColor = SawtTextMuted,
                                indicatorColor = MoroccanGold.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.testTag("nav_royalties")
                        )

                        NavigationBarItem(
                            selected = currentScreen == ArtistScreen.PROFILE,
                            onClick = { viewModel.navigateTo(ArtistScreen.PROFILE) },
                            icon = { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(20.dp)) },
                            label = { Text("Profile", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MoroccanGold,
                                selectedTextColor = MoroccanGold,
                                unselectedIconColor = SawtTextMuted,
                                unselectedTextColor = SawtTextMuted,
                                indicatorColor = MoroccanGold.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.testTag("nav_profile")
                        )
                    }
                }
            },
            containerColor = SawtObsidian
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(SawtObsidian)
            ) {
                if (currentRole == UserRole.ADMIN) {
                    AdminDashboardScreen(
                        releases = releases,
                        jobs = allJobs,
                        webhooks = allWebhooks,
                        payouts = allPayouts,
                        takedowns = allTakedowns,
                        auditLogs = allAuditLogs,
                        isSandboxMode = isSandboxMode,
                        currentLanguage = currentLanguage,
                        deliveryWorker = viewModel.deliveryWorker,
                        onApproveRelease = { viewModel.approveRelease(it) },
                        onRejectRelease = { id, reason -> viewModel.rejectRelease(id, reason) },
                        onApprovePayout = { id, notes -> viewModel.processPayout(id, true, notes) },
                        onRejectPayout = { id, notes -> viewModel.processPayout(id, false, notes) },
                        onProcessTakedown = { id, approve -> viewModel.processTakedown(id, approve) },
                        onSimulateWebhook = { relId, plat, evt -> viewModel.simulatePartnerWebhook(relId, plat, evt) }
                    )
                } else {
                    when (currentScreen) {
                        ArtistScreen.DASHBOARD -> {
                            ArtistDashboardScreen(
                                artistProfile = artistProfile,
                                releases = releases,
                                artistBalance = artistBalance,
                                currentLanguage = currentLanguage,
                                onCreateReleaseClick = { viewModel.navigateTo(ArtistScreen.CREATE_RELEASE) },
                                onReleaseClick = { release -> viewModel.selectRelease(release) }
                            )
                        }

                        ArtistScreen.CREATE_RELEASE -> {
                            CreateReleaseScreen(
                                artistId = artistProfile?.id ?: "profile_morocco_001",
                                primaryArtistDefault = artistProfile?.stageName ?: "OudWave",
                                currentLanguage = currentLanguage,
                                onBackClick = { viewModel.navigateTo(ArtistScreen.DASHBOARD) },
                                onSubmitRelease = { release, tracks, cover, rights ->
                                    viewModel.createRelease(release, tracks, cover, rights)
                                }
                            )
                        }

                        ArtistScreen.DETAIL -> {
                            val releaseToDisplay = selectedRelease ?: releases.firstOrNull()
                            if (releaseToDisplay != null) {
                                val tracksForRelease by viewModel.repository.getTracksForRelease(releaseToDisplay.id).collectAsState(initial = emptyList())
                                val coverForRelease by viewModel.repository.getCoverAsset(releaseToDisplay.id).collectAsState(initial = null)
                                val rightsForRelease by viewModel.repository.getRightsDeclaration(releaseToDisplay.id).collectAsState(initial = null)

                                ReleaseDetailScreen(
                                    release = releaseToDisplay,
                                    tracks = tracksForRelease,
                                    coverAsset = coverForRelease,
                                    rightsDeclaration = rightsForRelease,
                                    auditLogs = allAuditLogs,
                                    currentLanguage = currentLanguage,
                                    onBackClick = { viewModel.navigateTo(ArtistScreen.DASHBOARD) },
                                    onRequestTakedown = { platform, reason ->
                                        viewModel.requestTakedown(releaseToDisplay.id, platform, reason)
                                    }
                                )
                            }
                        }

                        ArtistScreen.ROYALTIES -> {
                            RoyaltiesScreen(
                                balance = artistBalance,
                                reports = royaltyReports,
                                payouts = payouts,
                                currentLanguage = currentLanguage,
                                onRequestPayout = { amt, method, details ->
                                    viewModel.requestPayout(amt, method, details)
                                }
                            )
                        }

                        ArtistScreen.PROFILE -> {
                            ArtistProfileScreen(
                                profile = artistProfile,
                                currentLanguage = currentLanguage,
                                onSaveProfile = { viewModel.updateProfile(it) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}
