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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AuditLogEntity
import com.example.data.model.DistributionJobEntity
import com.example.data.model.DistributionWebhookEntity
import com.example.data.model.DspPlatform
import com.example.data.model.PayoutEntity
import com.example.data.model.PayoutStatus
import com.example.data.model.ReleaseEntity
import com.example.data.model.ReleaseStatus
import com.example.data.model.TakedownRequestEntity
import com.example.domain.dsp.DeliveryStep
import com.example.domain.dsp.DeliveryWorker
import com.example.domain.dsp.WebhookVerifier
import com.example.ui.components.ReleaseStatusBadge
import com.example.ui.localization.AppLanguage
import com.example.ui.theme.MoroccanGold
import com.example.ui.theme.MoroccanGreen
import com.example.ui.theme.MoroccanRed
import com.example.ui.theme.SawtCard
import com.example.ui.theme.SawtCardBorder
import com.example.ui.theme.SawtObsidian
import com.example.ui.theme.SawtSurface
import com.example.ui.theme.SawtTextMuted
import com.example.ui.theme.SawtTextPrimary
import com.example.ui.theme.SawtTextSecondary
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusInfo
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning
import kotlinx.coroutines.launch

@Composable
fun AdminDashboardScreen(
    releases: List<ReleaseEntity>,
    jobs: List<DistributionJobEntity>,
    webhooks: List<DistributionWebhookEntity>,
    payouts: List<PayoutEntity>,
    takedowns: List<TakedownRequestEntity>,
    auditLogs: List<AuditLogEntity>,
    isSandboxMode: Boolean,
    currentLanguage: AppLanguage,
    deliveryWorker: DeliveryWorker,
    onApproveRelease: (String) -> Unit,
    onRejectRelease: (String, String) -> Unit,
    onApprovePayout: (String, String?) -> Unit,
    onRejectPayout: (String, String?) -> Unit,
    onProcessTakedown: (String, Boolean) -> Unit,
    onSimulateWebhook: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    // Delivery Worker State
    var isWorkerRunning by remember { mutableStateOf(false) }
    var currentStepLabel by remember { mutableStateOf("") }
    var workerProgress by remember { mutableStateOf(0f) }
    var activeDeliveryReleaseId by remember { mutableStateOf<String?>(null) }

    // Rejection Dialog State
    var showRejectDialog by remember { mutableStateOf(false) }
    var rejectingReleaseId by remember { mutableStateOf<String?>(null) }
    var rejectReasonInput by remember { mutableStateOf("") }

    // Webhook Dialog State
    var showWebhookDialog by remember { mutableStateOf(false) }
    var webhookPlatform by remember { mutableStateOf("SPOTIFY") }
    var webhookEvent by remember { mutableStateOf("RELEASE_LIVE") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SawtObsidian)
    ) {
        // Admin Banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SawtSurface)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = MoroccanRed, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("INTERNAL DISTRIBUTION OPS", color = MoroccanRed, fontWeight = FontWeight.Black, fontSize = 14.sp, letterSpacing = 1.sp)
                    Text("Sawt Moroccan Direct Ingestion Gateway", color = SawtTextMuted, fontSize = 10.sp)
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(MoroccanGreen.copy(alpha = 0.2f))
                    .border(1.dp, MoroccanGreen, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("DDEX ERN 4.2 READY", color = MoroccanGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Delivery Worker Status Banner if active
        if (isWorkerRunning) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .border(1.dp, MoroccanGold, RoundedCornerShape(8.dp)),
                colors = CardDefaults.cardColors(containerColor = SawtCard)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("DELIVERY WORKER EXECUTING", color = MoroccanGold, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Text("${(workerProgress * 100).toInt()}%", color = SawtTextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = currentStepLabel, color = SawtTextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { workerProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = MoroccanGold,
                        trackColor = SawtCardBorder
                    )
                }
            }
        }

        // Admin Tabs:
        // 0: Release Review, 1: Distribution Queue, 2: Payout Approvals, 3: Takedowns, 4: Webhook Ingest
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = SawtSurface,
            contentColor = MoroccanGold,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = MoroccanGold
                )
            }
        ) {
            val pendingReviewCount = releases.count { it.status == ReleaseStatus.PENDING_REVIEW }
            val pendingPayoutCount = payouts.count { it.status == PayoutStatus.PENDING_REVIEW }

            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Releases (${pendingReviewCount})", fontSize = 11.5.sp, fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("admin_tab_releases")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Queue & Jobs (${jobs.size})", fontSize = 11.5.sp, fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("admin_tab_queue")
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Payouts (${pendingPayoutCount})", fontSize = 11.5.sp, fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("admin_tab_payouts")
            )
            Tab(
                selected = selectedTab == 3,
                onClick = { selectedTab = 3 },
                text = { Text("Takedowns (${takedowns.size})", fontSize = 11.5.sp, fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("admin_tab_takedowns")
            )
            Tab(
                selected = selectedTab == 4,
                onClick = { selectedTab = 4 },
                text = { Text("Webhooks (${webhooks.size})", fontSize = 11.5.sp, fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("admin_tab_webhooks")
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            when (selectedTab) {
                0 -> {
                    // TAB 0: RELEASES REVIEW & ACTIONS
                    items(releases) { release ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, SawtCardBorder, RoundedCornerShape(10.dp)),
                            colors = CardDefaults.cardColors(containerColor = SawtCard)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(release.title, color = SawtTextPrimary, fontWeight = FontWeight.Black, fontSize = 15.sp)
                                        Text("${release.primaryArtist} • ${release.genre.title} • UPC: ${release.upcEan}", color = SawtTextSecondary, fontSize = 11.sp)
                                    }
                                    ReleaseStatusBadge(status = release.status)
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Target Date: ${release.distributionReleaseDate} • Territory: ${release.territory.label}", color = SawtTextMuted, fontSize = 10.5.sp)

                                if (!release.adminNotes.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Note: ${release.adminNotes}", color = MoroccanGold, fontSize = 10.5.sp)
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Admin Action Buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (release.status == ReleaseStatus.PENDING_REVIEW) {
                                        Button(
                                            onClick = { onApproveRelease(release.id) },
                                            colors = ButtonDefaults.buttonColors(containerColor = MoroccanGreen),
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("admin_approve_${release.id}")
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Approve", fontSize = 11.sp)
                                        }

                                        Button(
                                            onClick = {
                                                rejectingReleaseId = release.id
                                                showRejectDialog = true
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = MoroccanRed.copy(alpha = 0.2f)),
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .border(1.dp, MoroccanRed, RoundedCornerShape(6.dp))
                                                .testTag("admin_reject_${release.id}")
                                        ) {
                                            Icon(Icons.Default.Close, contentDescription = null, tint = MoroccanRed, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Reject", color = MoroccanRed, fontSize = 11.sp)
                                        }
                                    }

                                    if (release.status == ReleaseStatus.APPROVED || release.status == ReleaseStatus.QUEUED) {
                                        Button(
                                            onClick = {
                                                activeDeliveryReleaseId = release.id
                                                isWorkerRunning = true
                                                coroutineScope.launch {
                                                    deliveryWorker.executeDistribution(
                                                        releaseId = release.id,
                                                        isSandboxMode = isSandboxMode,
                                                        onProgress = { step ->
                                                            currentStepLabel = step.label
                                                            workerProgress = step.progress
                                                        }
                                                    )
                                                    isWorkerRunning = false
                                                }
                                            },
                                            enabled = !isWorkerRunning,
                                            colors = ButtonDefaults.buttonColors(containerColor = MoroccanGold),
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("admin_deliver_${release.id}")
                                        ) {
                                            Icon(Icons.Default.Send, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Trigger Delivery Worker to Spotify & YouTube", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // TAB 1: DISTRIBUTION QUEUE & JOBS
                    items(jobs) { job ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, SawtCardBorder, RoundedCornerShape(8.dp)),
                            colors = CardDefaults.cardColors(containerColor = SawtCard)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Platform: ${job.platform.name}", color = MoroccanGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (job.status == "DELIVERED") StatusSuccess.copy(alpha = 0.2f) else StatusWarning.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(job.status, color = if (job.status == "DELIVERED") StatusSuccess else StatusWarning, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Job ID: ${job.id}", color = SawtTextPrimary, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                                Text("Attempt: ${job.attempts}/${job.maxAttempts} • External: ${job.externalReleaseId ?: "Awaiting intake"}", color = SawtTextSecondary, fontSize = 10.sp)
                                if (!job.payloadSummary.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(job.payloadSummary, color = SawtTextMuted, fontSize = 9.5.sp)
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // TAB 2: PAYOUT APPROVALS
                    val pendingPayouts = payouts.filter { it.status == PayoutStatus.PENDING_REVIEW }
                    if (pendingPayouts.isEmpty()) {
                        item {
                            Text("No pending payout requests requiring treasury approval.", color = SawtTextMuted, fontSize = 12.sp)
                        }
                    } else {
                        items(pendingPayouts) { payout ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(1.dp, SawtCardBorder, RoundedCornerShape(10.dp)),
                                colors = CardDefaults.cardColors(containerColor = SawtCard)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(payout.artistName, color = SawtTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Text("${payout.paymentMethod.title} • ${payout.accountDetails}", color = SawtTextSecondary, fontSize = 11.sp)
                                        }
                                        Text("${"%,.2f".format(payout.amount)} ${payout.currency}", color = StatusSuccess, fontWeight = FontWeight.Black, fontSize = 15.sp)
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = { onApprovePayout(payout.id, "Bank wire executed by Casablanca Operations") },
                                            colors = ButtonDefaults.buttonColors(containerColor = MoroccanGreen),
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Approve & Wire", fontSize = 11.sp)
                                        }
                                        Button(
                                            onClick = { onRejectPayout(payout.id, "Invalid RIB account holder name mismatch") },
                                            colors = ButtonDefaults.buttonColors(containerColor = MoroccanRed.copy(alpha = 0.2f)),
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .border(1.dp, MoroccanRed, RoundedCornerShape(6.dp))
                                        ) {
                                            Text("Reject", color = MoroccanRed, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                3 -> {
                    // TAB 3: TAKEDOWN REQUESTS
                    if (takedowns.isEmpty()) {
                        item {
                            Text("No pending takedown orders.", color = SawtTextMuted, fontSize = 12.sp)
                        }
                    } else {
                        items(takedowns) { req ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, MoroccanRed.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                                colors = CardDefaults.cardColors(containerColor = SawtCard)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Release: ${req.releaseTitle}", color = SawtTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Target DSP: ${req.platform} • Reason: ${req.reason}", color = SawtTextSecondary, fontSize = 11.sp)
                                    Text("Status: ${req.status}", color = StatusWarning, fontSize = 10.5.sp)

                                    if (req.status == "REQUESTED") {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Button(
                                                onClick = { onProcessTakedown(req.id, true) },
                                                colors = ButtonDefaults.buttonColors(containerColor = MoroccanRed),
                                                shape = RoundedCornerShape(6.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Approve Takedown", fontSize = 11.sp)
                                            }
                                            OutlinedButton(
                                                onClick = { onProcessTakedown(req.id, false) },
                                                shape = RoundedCornerShape(6.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Deny", color = SawtTextPrimary, fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                4 -> {
                    // TAB 4: EXTERNAL WEBHOOKS & INGEST SIMULATOR
                    item {
                        Button(
                            onClick = { showWebhookDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MoroccanGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("simulate_webhook_button")
                        ) {
                            Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Simulate Inbound Partner Webhook (HMAC-SHA256)", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    items(webhooks) { hook ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, SawtCardBorder, RoundedCornerShape(8.dp)),
                            colors = CardDefaults.cardColors(containerColor = SawtCard)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(hook.platform.name, color = MoroccanGold, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    Text(if (hook.isVerified) "✓ HMAC Verified" else "⚠ Unverified", color = if (hook.isVerified) StatusSuccess else StatusError, fontSize = 10.sp)
                                }
                                Text("Event: ${hook.eventType}", color = SawtTextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                                Text(hook.payloadJson, color = SawtTextMuted, fontFamily = FontFamily.Monospace, fontSize = 9.sp)
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(40.dp)) }
        }
    }

    // Rejection Note Dialog
    if (showRejectDialog) {
        AlertDialog(
            onDismissRequest = { showRejectDialog = false },
            title = { Text("Reject Release Compliance", color = StatusError, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Provide a specific correction reason for the artist:", color = SawtTextSecondary, fontSize = 12.sp)
                    SawtTextField(
                        value = rejectReasonInput,
                        onValueChange = { rejectReasonInput = it },
                        label = "Correction Required *",
                        placeholder = "e.g. Artwork contains blurry unauthorized brand logo",
                        testTag = "input_reject_reason"
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        rejectingReleaseId?.let { onRejectRelease(it, rejectReasonInput.ifBlank { "Metadata correction needed" }) }
                        showRejectDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusError)
                ) {
                    Text("Confirm Rejection")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showRejectDialog = false }) {
                    Text("Cancel", color = SawtTextPrimary)
                }
            },
            containerColor = SawtCard
        )
    }

    // Webhook Simulator Dialog
    if (showWebhookDialog) {
        val targetRelease = releases.firstOrNull()
        AlertDialog(
            onDismissRequest = { showWebhookDialog = false },
            title = { Text("Simulate Partner Webhook", color = MoroccanGold, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Simulates an authenticated inbound HTTP POST from Spotify or YouTube Music Partner Ingest.", color = SawtTextSecondary, fontSize = 11.5.sp)
                    Text("Event: $webhookEvent on release '${targetRelease?.title ?: "NO SIGNAL"}'", color = SawtTextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        targetRelease?.let {
                            onSimulateWebhook(it.id, webhookPlatform, webhookEvent)
                        }
                        showWebhookDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MoroccanGreen)
                ) {
                    Text("Dispatch Webhook")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showWebhookDialog = false }) {
                    Text("Cancel", color = SawtTextPrimary)
                }
            },
            containerColor = SawtCard
        )
    }
}
