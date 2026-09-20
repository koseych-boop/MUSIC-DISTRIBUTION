package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ArtistBalanceEntity
import com.example.data.model.ArtistProfileEntity
import com.example.data.model.ReleaseEntity
import com.example.data.model.ReleaseStatus
import com.example.ui.components.DspStatusBadge
import com.example.ui.components.ReleaseStatusBadge
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.Strings
import com.example.ui.theme.MoroccanGold
import com.example.ui.theme.MoroccanGreen
import com.example.ui.theme.MoroccanGreenDark
import com.example.ui.theme.MoroccanRed
import com.example.ui.theme.MoroccanRedDark
import com.example.ui.theme.SawtCard
import com.example.ui.theme.SawtCardBorder
import com.example.ui.theme.SawtCardHover
import com.example.ui.theme.SawtObsidian
import com.example.ui.theme.SawtSurface
import com.example.ui.theme.SawtTextMuted
import com.example.ui.theme.SawtTextPrimary
import com.example.ui.theme.SawtTextSecondary
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.YouTubeRed

@Composable
fun ArtistDashboardScreen(
    artistProfile: ArtistProfileEntity?,
    releases: List<ReleaseEntity>,
    artistBalance: ArtistBalanceEntity?,
    currentLanguage: AppLanguage,
    onCreateReleaseClick: () -> Unit,
    onReleaseClick: (ReleaseEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("ALL") }

    val filteredReleases = when (selectedFilter) {
        "LIVE" -> releases.filter { it.status == ReleaseStatus.LIVE }
        "IN_REVIEW" -> releases.filter { it.status == ReleaseStatus.PENDING_REVIEW || it.status == ReleaseStatus.VALIDATING }
        "DELIVERED" -> releases.filter { it.status == ReleaseStatus.DELIVERED || it.status == ReleaseStatus.APPROVED }
        else -> releases
    }

    val totalStreams = 280600L
    val activeReleasesCount = releases.count { it.status == ReleaseStatus.LIVE }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SawtObsidian)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))

            // Artist Hero Card with Authentic Moroccan Studio Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, SawtCardBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = SawtCard)
            ) {
                Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
                    // Moroccan studio hero image
                    Image(
                        painter = painterResource(id = R.drawable.img_moroccan_hero),
                        contentDescription = "Moroccan Music Studio",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Black.copy(alpha = 0.2f),
                                        SawtObsidian.copy(alpha = 0.95f)
                                    )
                                )
                            )
                    )

                    // Overlay Content
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(MoroccanRed)
                                    .border(2.dp, MoroccanGold, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = (artistProfile?.stageName?.take(2) ?: "OW").uppercase(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp
                                )
                            }

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = artistProfile?.stageName ?: "OudWave",
                                        color = Color.White,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Verified Moroccan Artist",
                                        tint = MoroccanGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Text(
                                    text = "${artistProfile?.city?.displayName ?: "Casablanca"}, Morocco • ${artistProfile?.genre?.title ?: "Moroccan Rap"}",
                                    color = SawtTextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // Stats Row: Live Releases, Total Streams, Available Royalties
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Live Releases",
                    value = activeReleasesCount.toString(),
                    icon = Icons.Default.Album,
                    accentColor = MoroccanGreen,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Total Streams",
                    value = "280.6K",
                    icon = Icons.Default.GraphicEq,
                    accentColor = MoroccanGold,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Available Royalties",
                    value = "${artistBalance?.availableBalance?.toInt() ?: 18678} DH",
                    icon = Icons.Default.MonetizationOn,
                    accentColor = MoroccanRed,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Action Banner: Create New Release
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(MoroccanRedDark, SawtCard)
                        )
                    )
                    .border(1.dp, MoroccanRed.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = Strings.createRelease(currentLanguage),
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Distribute Single, EP, or Album to Spotify & YouTube Music with automated DDEX validation.",
                            color = SawtTextSecondary,
                            fontSize = 11.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = onCreateReleaseClick,
                        colors = ButtonDefaults.buttonColors(containerColor = MoroccanRed),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("create_release_hero_button")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "New Release", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Filter Tabs
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = Strings.myReleases(currentLanguage).uppercase(),
                    color = MoroccanGold,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "${filteredReleases.size} releases",
                    color = SawtTextMuted,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "ALL" to "All",
                    "LIVE" to "Live on DSP",
                    "IN_REVIEW" to "In Review",
                    "DELIVERED" to "Delivered"
                ).forEach { (key, label) ->
                    val isSelected = selectedFilter == key
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) MoroccanGold.copy(alpha = 0.2f) else SawtCard)
                            .border(
                                1.dp,
                                if (isSelected) MoroccanGold else SawtCardBorder,
                                RoundedCornerShape(16.dp)
                            )
                            .clickable { selectedFilter = key }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("filter_tab_$key")
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) MoroccanGold else SawtTextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // List of Releases
        if (filteredReleases.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No releases found in this category.",
                        color = SawtTextMuted,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            items(filteredReleases) { release ->
                ReleaseItemCard(
                    release = release,
                    onClick = { onReleaseClick(release) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
fun ReleaseItemCard(
    release: ReleaseEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, SawtCardBorder, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag("release_card_${release.id}"),
        colors = CardDefaults.cardColors(containerColor = SawtCard)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Artwork square thumbnail
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(MoroccanRedDark, MoroccanGreenDark)
                            )
                        )
                        .border(1.dp, SawtCardBorder, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = "Cover Artwork",
                        tint = MoroccanGold,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = release.title,
                            color = SawtTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            maxLines = 1
                        )
                        ReleaseStatusBadge(status = release.status)
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "${release.primaryArtist} ${if (release.featuringArtists.isNotBlank()) "feat. ${release.featuringArtists}" else ""}",
                        color = SawtTextSecondary,
                        fontSize = 12.sp,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${release.releaseType.name} • ${release.genre.title} • Released: ${release.distributionReleaseDate}",
                        color = SawtTextMuted,
                        fontSize = 10.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // DSP Status Chips (Spotify & YouTube Music)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DspStatusBadge(
                    platform = "Spotify",
                    status = release.spotifyStatus,
                    modifier = Modifier.weight(1f)
                )
                DspStatusBadge(
                    platform = "YouTube Music",
                    status = release.youtubeStatus,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, SawtCardBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = SawtCard)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                color = SawtTextPrimary,
                fontWeight = FontWeight.Black,
                fontSize = 15.sp
            )
            Text(
                text = title,
                color = SawtTextMuted,
                fontSize = 9.5.sp,
                maxLines = 1
            )
        }
    }
}
