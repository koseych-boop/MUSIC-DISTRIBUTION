package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.data.model.ReleaseStatus
import com.example.ui.theme.MoroccanGold
import com.example.ui.theme.MoroccanGreen
import com.example.ui.theme.MoroccanRed
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusInfo
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.YouTubeRed

@Composable
fun ReleaseStatusBadge(status: ReleaseStatus, modifier: Modifier = Modifier) {
    val (bgColor, borderColor, textColor, label) = when (status) {
        ReleaseStatus.LIVE -> Quad(StatusSuccess.copy(alpha = 0.2f), StatusSuccess, StatusSuccess, "LIVE")
        ReleaseStatus.DELIVERED -> Quad(StatusInfo.copy(alpha = 0.2f), StatusInfo, StatusInfo, "DELIVERED")
        ReleaseStatus.APPROVED -> Quad(MoroccanGreen.copy(alpha = 0.2f), MoroccanGreen, MoroccanGreen, "APPROVED")
        ReleaseStatus.PENDING_REVIEW -> Quad(StatusWarning.copy(alpha = 0.2f), StatusWarning, StatusWarning, "PENDING REVIEW")
        ReleaseStatus.QUEUED -> Quad(MoroccanGold.copy(alpha = 0.2f), MoroccanGold, MoroccanGold, "IN QUEUE")
        ReleaseStatus.SUBMITTED -> Quad(StatusInfo.copy(alpha = 0.2f), StatusInfo, StatusInfo, "SUBMITTED")
        ReleaseStatus.VALIDATING -> Quad(MoroccanGold.copy(alpha = 0.2f), MoroccanGold, MoroccanGold, "VALIDATING")
        ReleaseStatus.DRAFT -> Quad(Color.Gray.copy(alpha = 0.2f), Color.Gray, Color.LightGray, "DRAFT")
        ReleaseStatus.FAILED -> Quad(StatusError.copy(alpha = 0.2f), StatusError, StatusError, "FAILED")
        ReleaseStatus.REJECTED -> Quad(StatusError.copy(alpha = 0.2f), StatusError, StatusError, "REJECTED")
        ReleaseStatus.TAKEDOWN -> Quad(MoroccanRed.copy(alpha = 0.2f), MoroccanRed, MoroccanRed, "TAKEDOWN")
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun DspStatusBadge(
    platform: String,
    status: DspStatus,
    modifier: Modifier = Modifier
) {
    val isSpotify = platform.contains("Spotify", ignoreCase = true)
    val accentColor = if (isSpotify) SpotifyGreen else YouTubeRed

    val statusText = when (status) {
        DspStatus.LIVE -> "✓ Live"
        DspStatus.DELIVERED -> "✓ Delivered"
        DspStatus.SUBMITTED -> "Submitted"
        DspStatus.PROCESSING_DDEX -> "DDEX Ingest"
        DspStatus.QUEUED -> "Queued"
        DspStatus.VALIDATING -> "Validating"
        DspStatus.FAILED -> "⚠ Ingest Failed"
        DspStatus.TAKEDOWN_REQUESTED -> "Takedown Pending"
        DspStatus.REMOVED -> "Removed"
        DspStatus.NOT_SUBMITTED -> "Not Distributed"
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(accentColor.copy(alpha = 0.12f))
            .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .padding(horizontal = 7.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(if (status == DspStatus.LIVE || status == DspStatus.DELIVERED) accentColor else Color.Gray)
        )
        Text(
            text = "$platform: $statusText",
            color = if (status == DspStatus.LIVE || status == DspStatus.DELIVERED) Color.White else Color.LightGray,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
