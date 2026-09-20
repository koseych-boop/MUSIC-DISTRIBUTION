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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ArtistBalanceEntity
import com.example.data.model.PayoutEntity
import com.example.data.model.PayoutMethod
import com.example.data.model.PayoutStatus
import com.example.data.model.RoyaltyReportEntity
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.Strings
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
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.YouTubeRed

@Composable
fun RoyaltiesScreen(
    balance: ArtistBalanceEntity?,
    reports: List<RoyaltyReportEntity>,
    payouts: List<PayoutEntity>,
    currentLanguage: AppLanguage,
    onRequestPayout: (Double, PayoutMethod, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPayoutDialog by remember { mutableStateOf(false) }
    var payoutAmountInput by remember { mutableStateOf("5000") }
    var selectedMethod by remember { mutableStateOf(PayoutMethod.CIH_BANK) }
    var accountDetailsInput by remember { mutableStateOf("CIH Bank - RIB: 230 780 0011223344556677 88") }

    val available = balance?.availableBalance ?: 18678.00
    val pending = balance?.pendingBalance ?: 2450.00
    val paid = balance?.paidBalance ?: 35200.00

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SawtObsidian)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Title & Slogan
        item {
            Column {
                Text(
                    text = Strings.royalties(currentLanguage).uppercase(),
                    color = MoroccanGold,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Direct revenue from Spotify, YouTube Music, and Apple Music with real accounting.",
                    color = SawtTextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        // Balances Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BalanceCard(
                    title = "Available to Withdraw",
                    amount = "${"%,.2f".format(available)} MAD",
                    accentColor = MoroccanGreen,
                    subtitle = "Cleared for payout",
                    modifier = Modifier.weight(1f)
                )
                BalanceCard(
                    title = "Pending Clearance",
                    amount = "${"%,.2f".format(pending)} MAD",
                    accentColor = MoroccanGold,
                    subtitle = "Processing period",
                    modifier = Modifier.weight(1f)
                )
                BalanceCard(
                    title = "Lifetime Paid",
                    amount = "${"%,.2f".format(paid)} MAD",
                    accentColor = MoroccanRed,
                    subtitle = "Transferred to bank",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Request Payout Button CTA
        item {
            Button(
                onClick = { showPayoutDialog = true },
                enabled = available >= 500.0,
                colors = ButtonDefaults.buttonColors(containerColor = MoroccanGreen),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("request_payout_button")
            ) {
                Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Request Moroccan Payout (طلب تحويل الأرباح)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp
                )
            }
        }

        // Monthly Statements
        item {
            Text(
                text = "DSP REVENUE STATEMENTS (الكشوفات الشهرية)",
                color = MoroccanGold,
                fontWeight = FontWeight.Black,
                fontSize = 13.sp,
                letterSpacing = 1.sp
            )
        }

        if (reports.isEmpty()) {
            item {
                Text("No statements imported yet.", color = SawtTextMuted, fontSize = 12.sp)
            }
        } else {
            items(reports) { report ->
                StatementCard(report = report)
            }
        }

        // Payout History
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "PAYOUT HISTORY (سجل التحويلات البنكية)",
                color = MoroccanGold,
                fontWeight = FontWeight.Black,
                fontSize = 13.sp,
                letterSpacing = 1.sp
            )
        }

        if (payouts.isEmpty()) {
            item {
                Text("No payout requests yet.", color = SawtTextMuted, fontSize = 12.sp)
            }
        } else {
            items(payouts) { payout ->
                PayoutItemCard(payout = payout)
            }
        }

        item { Spacer(modifier = Modifier.height(40.dp)) }
    }

    // Payout Dialog
    if (showPayoutDialog) {
        AlertDialog(
            onDismissRequest = { showPayoutDialog = false },
            title = {
                Text("Request Bank Transfer / Payout", color = MoroccanGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Available balance: ${"%,.2f".format(available)} MAD. Minimum payout threshold is 500 MAD.",
                        color = SawtTextSecondary,
                        fontSize = 12.sp
                    )

                    SawtTextField(
                        value = payoutAmountInput,
                        onValueChange = { payoutAmountInput = it },
                        label = "Amount in MAD *",
                        placeholder = "5000",
                        testTag = "input_payout_amount"
                    )

                    Text("Select Payment Destination:", color = SawtTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)

                    // Method selector
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(
                            PayoutMethod.CIH_BANK to "CIH Bank (Morocco)",
                            PayoutMethod.ATTIJARIWAFA to "Attijariwafa Bank (Morocco)",
                            PayoutMethod.BANQUE_POPULAIRE to "Banque Populaire (Morocco)",
                            PayoutMethod.PAYPAL to "PayPal Worldwide"
                        ).forEach { (method, label) ->
                            val isSelected = selectedMethod == method
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) MoroccanGold.copy(alpha = 0.2f) else SawtObsidian)
                                    .border(1.dp, if (isSelected) MoroccanGold else SawtCardBorder, RoundedCornerShape(6.dp))
                                    .clickable {
                                        selectedMethod = method
                                        accountDetailsInput = when (method) {
                                            PayoutMethod.CIH_BANK -> "CIH Bank - RIB: 230 780 0011223344556677 88"
                                            PayoutMethod.ATTIJARIWAFA -> "Attijariwafa Bank - RIB: 007 780 9988776655443322 11"
                                            PayoutMethod.BANQUE_POPULAIRE -> "Banque Populaire - RIB: 190 780 5544332211009988 44"
                                            PayoutMethod.PAYPAL -> "paypal.oudwave@moroccomusic.ma"
                                            else -> "RIB / Account details"
                                        }
                                    }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (method == PayoutMethod.PAYPAL) Icons.Default.Payments else Icons.Default.AccountBalance,
                                    contentDescription = null,
                                    tint = if (isSelected) MoroccanGold else SawtTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = label,
                                    color = if (isSelected) MoroccanGold else SawtTextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    SawtTextField(
                        value = accountDetailsInput,
                        onValueChange = { accountDetailsInput = it },
                        label = "RIB (24-digit Bank Account) or PayPal Email *",
                        testTag = "input_payout_rib"
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = payoutAmountInput.toDoubleOrNull() ?: 0.0
                        if (amt > 0 && amt <= available) {
                            onRequestPayout(amt, selectedMethod, accountDetailsInput)
                            showPayoutDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MoroccanGreen),
                    modifier = Modifier.testTag("confirm_payout_button")
                ) {
                    Text("Submit Request")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showPayoutDialog = false }) {
                    Text("Cancel", color = SawtTextPrimary)
                }
            },
            containerColor = SawtCard
        )
    }
}

@Composable
private fun BalanceCard(
    title: String,
    amount: String,
    accentColor: Color,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, SawtCardBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = SawtCard)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = title, color = SawtTextMuted, fontSize = 10.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = amount, color = SawtTextPrimary, fontWeight = FontWeight.Black, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, color = accentColor, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun StatementCard(report: RoyaltyReportEntity) {
    val isSpotify = report.platform == com.example.data.model.DspPlatform.SPOTIFY
    val platformColor = if (isSpotify) SpotifyGreen else YouTubeRed

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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(platformColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${report.platform.name} • Period: ${report.period}",
                        color = platformColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
                Text(
                    text = "Net: ${"%,.2f".format(report.netRevenue)} ${report.currency}",
                    color = StatusSuccess,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Release: ${report.releaseTitle}",
                color = SawtTextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Streams: ${"%,d".format(report.streams)}",
                    color = SawtTextSecondary,
                    fontSize = 11.sp
                )
                Text(
                    text = "Gross: ${"%,.2f".format(report.grossRevenue)} MAD (Fee: ${"%,.2f".format(report.fees)})",
                    color = SawtTextMuted,
                    fontSize = 10.5.sp
                )
            }
        }
    }
}

@Composable
private fun PayoutItemCard(payout: PayoutEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, SawtCardBorder, RoundedCornerShape(10.dp)),
        colors = CardDefaults.cardColors(containerColor = SawtCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "${"%,.2f".format(payout.amount)} ${payout.currency}",
                    color = SawtTextPrimary,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp
                )
                Text(
                    text = "${payout.paymentMethod.title} • ${payout.accountDetails}",
                    color = SawtTextSecondary,
                    fontSize = 11.sp,
                    maxLines = 1
                )
                if (!payout.auditNotes.isNullOrBlank()) {
                    Text(
                        text = "Note: ${payout.auditNotes}",
                        color = SawtTextMuted,
                        fontSize = 10.sp
                    )
                }
            }

            val (badgeBg, badgeText) = when (payout.status) {
                PayoutStatus.COMPLETED -> StatusSuccess to "PAID"
                PayoutStatus.APPROVED -> MoroccanGreen to "APPROVED"
                PayoutStatus.PENDING_REVIEW -> StatusWarning to "PENDING"
                PayoutStatus.REQUESTED -> MoroccanGold to "REQUESTED"
                PayoutStatus.REJECTED -> StatusError to "REJECTED"
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(badgeBg.copy(alpha = 0.2f))
                    .border(1.dp, badgeBg, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(text = badgeText, color = badgeBg, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
