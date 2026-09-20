package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AuditLogEntity
import com.example.data.model.CoverAssetEntity
import com.example.data.model.DspStatus
import com.example.data.model.ReleaseEntity
import com.example.data.model.ReleaseTrackEntity
import com.example.data.model.RightsDeclarationEntity
import com.example.domain.dsp.DDEXGenerator
import com.example.ui.components.DistributionTimeline
import com.example.ui.components.DspStatusBadge
import com.example.ui.components.ReleaseStatusBadge
import com.example.ui.localization.AppLanguage
import com.example.ui.theme.MoroccanGold
import com.example.ui.theme.MoroccanGreen
import com.example.ui.theme.MoroccanGreenDark
import com.example.ui.theme.MoroccanRed
import com.example.ui.theme.MoroccanRedDark
import com.example.ui.theme.SawtCard
import com.example.ui.theme.SawtCardBorder
import com.example.ui.theme.SawtObsidian
import com.example.ui.theme.SawtSurface
import com.example.ui.theme.SawtTextMuted
import com.example.ui.theme.SawtTextPrimary
import com.example.ui.theme.SawtTextSecondary
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.YouTubeRed

@Composable
fun ReleaseDetailScreen(
    release: ReleaseEntity,
    tracks: List<ReleaseTrackEntity>,
    coverAsset: CoverAssetEntity?,
    rightsDeclaration: RightsDeclarationEntity?,
    auditLogs: List<AuditLogEntity>,
    currentLanguage: AppLanguage,
    onBackClick: () -> Unit,
    onRequestTakedown: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDdexDialog by remember { mutableStateOf(false) }
    var showTakedownDialog by remember { mutableStateOf(false) }
    var takedownReason by remember { mutableStateOf("") }
    var takedownPlatform by remember { mutableStateOf("ALL") }

    val ddexXml = remember(release, tracks, coverAsset) {
        DDEXGenerator.generateErnXml(release, tracks, coverAsset)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SawtObsidian)
    ) {
        // Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SawtSurface)
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick, modifier = Modifier.testTag("detail_back_button")) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = SawtTextPrimary)
            }
            Text(
                text = "RELEASE DETAILS (${release.title})",
                color = MoroccanGold,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                maxLines = 1
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(6.dp)) }

            // Header Hero
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, SawtCardBorder, RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = SawtCard)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Brush.linearGradient(listOf(MoroccanRedDark, MoroccanGreenDark)))
                                    .border(1.dp, MoroccanGold, RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.MusicNote, contentDescription = null, tint = MoroccanGold, modifier = Modifier.size(32.dp))
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = release.title, color = SawtTextPrimary, fontWeight = FontWeight.Black, fontSize = 17.sp)
                                    ReleaseStatusBadge(status = release.status)
                                }
                                Text(
                                    text = "${release.primaryArtist} ${if (release.featuringArtists.isNotBlank()) "feat. ${release.featuringArtists}" else ""}",
                                    color = SawtTextSecondary,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${release.releaseType.name} • ${release.genre.title} • UPC: ${release.upcEan}",
                                    color = SawtTextMuted,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "℗ ${release.pLineYear} / © ${release.cLineYear} ${release.copyrightOwner}",
                                    color = SawtTextMuted,
                                    fontSize = 10.5.sp
                                )
                            }
                        }
                    }
                }
            }

            // Visual 7-Stage Timeline
            item {
                DistributionTimeline(release = release)
            }

            // DSP Delivery Status Details
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, SawtCardBorder, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = SawtCard)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("DSP PARTNER INGESTION STATUS", color = MoroccanGold, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 0.5.sp)
                        Spacer(modifier = Modifier.height(10.dp))

                        // Spotify Card
                        DspDetailCard(
                            platform = "Spotify Direct Partner Ingestion",
                            status = release.spotifyStatus,
                            color = SpotifyGreen,
                            externalId = release.spotifyReleaseId ?: "spot_batch_pending",
                            details = "Lossless WAV/FLAC audio verified. Ready for playlist pitching.",
                            url = release.spotifyUrl
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // YouTube Music Card
                        DspDetailCard(
                            platform = "YouTube Music DDEX Partner Delivery",
                            status = release.youtubeStatus,
                            color = YouTubeRed,
                            externalId = release.youtubeAssetId ?: "yt_asset_pending",
                            details = "DDEX ERN 4.2 Art Track feed generated. Audio fingerprint mapped.",
                            url = if (release.youtubeStatus == DspStatus.LIVE) "https://music.youtube.com" else null
                        )
                    }
                }
            }

            // Tracklist Table
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, SawtCardBorder, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = SawtCard)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("TRACKLIST & SOUND RECORDINGS (${tracks.size})", color = MoroccanGold, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 0.5.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        tracks.forEach { track ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(SawtObsidian),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = "${track.trackNumber}", color = MoroccanGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(text = track.title, color = SawtTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(text = "ISRC: ${track.isrc} • ${track.audioFormat.name} (Lossless)", color = SawtTextMuted, fontSize = 10.5.sp)
                                    }
                                }
                                Text(
                                    text = "${track.durationSeconds / 60}:${"%02d".format(track.durationSeconds % 60)}",
                                    color = SawtTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            // Actions & Inspector Bar (View DDEX, Storage Buckets, Request Takedown)
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { showDdexDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = SawtCard),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, MoroccanGold, RoundedCornerShape(8.dp))
                            .testTag("inspect_ddex_button")
                    ) {
                        Icon(Icons.Default.Code, contentDescription = null, tint = MoroccanGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("DDEX ERN 4.2", color = MoroccanGold, fontSize = 11.5.sp)
                    }

                    Button(
                        onClick = { showTakedownDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MoroccanRed.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, MoroccanRed, RoundedCornerShape(8.dp))
                            .testTag("request_takedown_button")
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = MoroccanRed, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Takedown", color = MoroccanRed, fontSize = 11.5.sp)
                    }
                }
            }

            // Audit Trail for this release
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, SawtCardBorder, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = SawtCard)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.History, contentDescription = null, tint = MoroccanGold, modifier = Modifier.size(16.dp))
                            Text("AUDIT LOG & PROVENANCE", color = MoroccanGold, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 0.5.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        val releaseLogs = auditLogs.filter { it.targetId == release.id }
                        if (releaseLogs.isEmpty()) {
                            Text("Release created and signed with SHA-256 verification.", color = SawtTextMuted, fontSize = 11.sp)
                        } else {
                            releaseLogs.forEach { log ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 4.dp)
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(MoroccanGreen)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(text = "${log.action} (${log.actorRole.name})", color = SawtTextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        Text(text = log.details, color = SawtTextSecondary, fontSize = 10.5.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(40.dp)) }
        }
    }

    // DDEX XML Modal Dialog
    if (showDdexDialog) {
        AlertDialog(
            onDismissRequest = { showDdexDialog = false },
            title = {
                Text("DDEX ERN 4.2 Electronic Release Notification", color = MoroccanGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SawtObsidian)
                        .padding(10.dp)
                ) {
                    item {
                        Text(
                            text = ddexXml,
                            color = StatusSuccess,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showDdexDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MoroccanGreen)
                ) {
                    Text("Close")
                }
            },
            containerColor = SawtCard
        )
    }

    // Takedown Confirmation Dialog
    if (showTakedownDialog) {
        AlertDialog(
            onDismissRequest = { showTakedownDialog = false },
            title = {
                Text("Request Distribution Takedown", color = StatusError, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "This will issue legal de-indexing and takedown instructions to Spotify, Apple Music, and YouTube Music. Ingestion de-listing usually takes 24-48 hours.",
                        color = SawtTextSecondary,
                        fontSize = 12.sp
                    )
                    SawtTextField(
                        value = takedownReason,
                        onValueChange = { takedownReason = it },
                        label = "Takedown Reason *",
                        placeholder = "e.g. Master rights renegotiation / Relicensing"
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onRequestTakedown(takedownPlatform, takedownReason.ifBlank { "Artist voluntary withdrawal" })
                        showTakedownDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusError)
                ) {
                    Text("Confirm Takedown")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showTakedownDialog = false }) {
                    Text("Cancel", color = SawtTextPrimary)
                }
            },
            containerColor = SawtCard
        )
    }
}

@Composable
private fun DspDetailCard(
    platform: String,
    status: DspStatus,
    color: Color,
    externalId: String,
    details: String,
    url: String?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = SawtObsidian)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = platform, color = color, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                DspStatusBadge(platform = if (platform.contains("Spotify")) "Spotify" else "YouTube", status = status)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "External Asset ID: $externalId", color = SawtTextPrimary, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
            Text(text = details, color = SawtTextMuted, fontSize = 10.sp)

            if (!url.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.OpenInNew, contentDescription = null, tint = MoroccanGold, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = url, color = MoroccanGold, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
