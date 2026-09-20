package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DspStatus
import com.example.data.model.ReleaseEntity
import com.example.data.model.ReleaseStatus
import com.example.ui.theme.MoroccanGold
import com.example.ui.theme.MoroccanGreen
import com.example.ui.theme.MoroccanRed
import com.example.ui.theme.SawtCard
import com.example.ui.theme.SawtCardBorder
import com.example.ui.theme.SawtTextMuted
import com.example.ui.theme.SawtTextPrimary
import com.example.ui.theme.SawtTextSecondary
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusSuccess

enum class TimelineStageState {
    COMPLETED,
    ACTIVE,
    PENDING,
    ERROR
}

data class TimelineStage(
    val title: String,
    val subtitle: String,
    val state: TimelineStageState
)

@Composable
fun DistributionTimeline(
    release: ReleaseEntity,
    modifier: Modifier = Modifier
) {
    val stages = calculateTimelineStages(release)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
            .background(SawtCard)
            .border(1.dp, SawtCardBorder, androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "DISTRIBUTION PIPELINE LIFECYCLE",
                color = MoroccanGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
            Text(
                text = "Status: ${release.status.name}",
                color = SawtTextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        stages.forEachIndexed { index, stage ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Node + Vertical Line column
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(28.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(
                                when (stage.state) {
                                    TimelineStageState.COMPLETED -> MoroccanGreen
                                    TimelineStageState.ACTIVE -> MoroccanGold
                                    TimelineStageState.ERROR -> StatusError
                                    TimelineStageState.PENDING -> SawtCardBorder
                                }
                            )
                            .border(
                                1.dp,
                                when (stage.state) {
                                    TimelineStageState.COMPLETED -> MoroccanGreen
                                    TimelineStageState.ACTIVE -> MoroccanGold
                                    TimelineStageState.ERROR -> StatusError
                                    TimelineStageState.PENDING -> SawtTextMuted
                                },
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        when (stage.state) {
                            TimelineStageState.COMPLETED -> Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                            TimelineStageState.ACTIVE -> Icon(
                                imageVector = Icons.Default.HourglassTop,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(12.dp)
                            )
                            TimelineStageState.ERROR -> Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            TimelineStageState.PENDING -> Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(SawtTextMuted)
                            )
                        }
                    }

                    if (index < stages.lastIndex) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(28.dp)
                                .background(
                                    if (stage.state == TimelineStageState.COMPLETED) MoroccanGreen
                                    else SawtCardBorder
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.padding(bottom = if (index < stages.lastIndex) 14.dp else 0.dp)) {
                    Text(
                        text = stage.title,
                        color = when (stage.state) {
                            TimelineStageState.COMPLETED -> SawtTextPrimary
                            TimelineStageState.ACTIVE -> MoroccanGold
                            TimelineStageState.ERROR -> StatusError
                            TimelineStageState.PENDING -> SawtTextMuted
                        },
                        fontWeight = if (stage.state == TimelineStageState.ACTIVE) FontWeight.Bold else FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = stage.subtitle,
                        color = SawtTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

private fun calculateTimelineStages(release: ReleaseEntity): List<TimelineStage> {
    val status = release.status
    val spot = release.spotifyStatus
    val yt = release.youtubeStatus

    // 1. Upload stage
    val uploadState = TimelineStageState.COMPLETED
    val uploadSubtitle = "Original audio (WAV/FLAC) & cover art secured in Supabase storage"

    // 2. Validation stage
    val validationState = when (status) {
        ReleaseStatus.DRAFT, ReleaseStatus.VALIDATING -> TimelineStageState.ACTIVE
        ReleaseStatus.REJECTED -> TimelineStageState.ERROR
        else -> TimelineStageState.COMPLETED
    }
    val validationSubtitle = if (validationState == TimelineStageState.COMPLETED) "ISRC, UPC checksum, audio losslessness & rights verified" else "Running metadata and rights validation"

    // 3. Admin Review
    val adminReviewState = when (status) {
        ReleaseStatus.DRAFT, ReleaseStatus.VALIDATING -> TimelineStageState.PENDING
        ReleaseStatus.PENDING_REVIEW -> TimelineStageState.ACTIVE
        ReleaseStatus.REJECTED -> TimelineStageState.ERROR
        else -> TimelineStageState.COMPLETED
    }
    val adminSubtitle = when (adminReviewState) {
        TimelineStageState.ACTIVE -> "Under review by Moroccan distribution compliance officer"
        TimelineStageState.COMPLETED -> "Rights ownership & master recordings approved for delivery"
        TimelineStageState.ERROR -> "Release rejected by admin: ${release.adminNotes ?: "Metadata non-compliance"}"
        TimelineStageState.PENDING -> "Waiting for pre-flight validation completion"
    }

    // 4. Approved
    val approvedState = when (status) {
        ReleaseStatus.DRAFT, ReleaseStatus.VALIDATING, ReleaseStatus.PENDING_REVIEW, ReleaseStatus.REJECTED -> TimelineStageState.PENDING
        ReleaseStatus.APPROVED -> TimelineStageState.ACTIVE
        else -> TimelineStageState.COMPLETED
    }
    val approvedSubtitle = if (approvedState == TimelineStageState.COMPLETED || approvedState == TimelineStageState.ACTIVE) "Release cleared for authorized DSP ingest queues" else "Pending administrative clearance"

    // 5. Distribution Ingestion
    val distroState = when (status) {
        ReleaseStatus.QUEUED, ReleaseStatus.SUBMITTED -> TimelineStageState.ACTIVE
        ReleaseStatus.DELIVERED, ReleaseStatus.LIVE -> TimelineStageState.COMPLETED
        ReleaseStatus.FAILED -> TimelineStageState.ERROR
        else -> TimelineStageState.PENDING
    }
    val distroSubtitle = when (distroState) {
        TimelineStageState.ACTIVE -> "Packaging DDEX ERN 4.2 & transmitting to authorized partner"
        TimelineStageState.COMPLETED -> "Ingested by Spotify partner protocol & YouTube DDEX gateway"
        TimelineStageState.ERROR -> "Partner gateway failure: check logs and retry job"
        TimelineStageState.PENDING -> "Queued after approval"
    }

    // 6. Spotify / YouTube Music Partner Intake
    val dspState = when {
        spot == DspStatus.LIVE && yt == DspStatus.LIVE -> TimelineStageState.COMPLETED
        spot == DspStatus.DELIVERED || yt == DspStatus.DELIVERED || spot == DspStatus.PROCESSING_DDEX -> TimelineStageState.ACTIVE
        status == ReleaseStatus.FAILED -> TimelineStageState.ERROR
        else -> TimelineStageState.PENDING
    }
    val dspSubtitle = "Spotify: ${spot.name} | YouTube Music: ${yt.name} (Art Track created)"

    // 7. Live Release
    val liveState = if (status == ReleaseStatus.LIVE) TimelineStageState.COMPLETED else TimelineStageState.PENDING
    val liveSubtitle = if (liveState == TimelineStageState.COMPLETED) "Available globally on streaming services" else "Target distribution date: ${release.distributionReleaseDate}"

    return listOf(
        TimelineStage("1. Audio & Artwork Upload", uploadSubtitle, uploadState),
        TimelineStage("2. Automatic Release Validation", validationSubtitle, validationState),
        TimelineStage("3. Admin Compliance Review", adminSubtitle, adminReviewState),
        TimelineStage("4. Approved for Distribution", approvedSubtitle, approvedState),
        TimelineStage("5. DDEX Package & Queue", distroSubtitle, distroState),
        TimelineStage("6. Spotify & YouTube Delivery", dspSubtitle, dspState),
        TimelineStage("7. Live Worldwide", liveSubtitle, liveState)
    )
}
