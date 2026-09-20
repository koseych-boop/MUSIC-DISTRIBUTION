package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
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
import com.example.ui.theme.StatusWarning

@Composable
fun ZelligePatternBanner(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(18.dp)
    ) {
        val width = size.width
        val height = size.height
        val step = 32f
        var x = 0f
        
        while (x < width) {
            val starPath = Path().apply {
                moveTo(x + step / 2, 0f)
                lineTo(x + step, height / 2)
                lineTo(x + step / 2, height)
                lineTo(x, height / 2)
                close()
            }
            drawPath(
                path = starPath,
                color = if ((x / step).toInt() % 2 == 0) MoroccanRed.copy(alpha = 0.35f) else MoroccanGreen.copy(alpha = 0.35f)
            )

            // Inner gold diamond
            val innerPath = Path().apply {
                moveTo(x + step / 2, height * 0.25f)
                lineTo(x + step * 0.75f, height / 2)
                lineTo(x + step / 2, height * 0.75f)
                lineTo(x + step * 0.25f, height / 2)
                close()
            }
            drawPath(
                path = innerPath,
                color = MoroccanGold.copy(alpha = 0.45f)
            )

            x += step
        }
    }
}

@Composable
fun SawtTopAppBar(
    currentRole: UserRole,
    onRoleChange: (UserRole) -> Unit,
    currentLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    isSandboxMode: Boolean,
    onToggleSandbox: () -> Unit,
    modifier: Modifier = Modifier
) {
    var langMenuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(SawtSurface)
    ) {
        ZelligePatternBanner()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Logo & Brand Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.testTag("brand_logo_row")
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(MoroccanRed, MoroccanGreen)
                            )
                        )
                        .border(1.5.dp, MoroccanGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = "Sawt Icon",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "SAWT",
                            color = MoroccanRed,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = " | صوت",
                            color = MoroccanGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                    Text(
                        text = Strings.appSlogan(currentLanguage),
                        color = SawtTextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1
                    )
                }
            }

            // Right side controls: Sandbox toggle, Language, Role Switch
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Sandbox / Demo Indicator pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSandboxMode) StatusWarning.copy(alpha = 0.2f) else MoroccanGreen.copy(alpha = 0.2f))
                        .border(
                            1.dp,
                            if (isSandboxMode) StatusWarning else MoroccanGreen,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { onToggleSandbox() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("sandbox_toggle_button")
                ) {
                    Text(
                        text = if (isSandboxMode) "SANDBOX" else "PRODUCTION",
                        color = if (isSandboxMode) StatusWarning else MoroccanGreen,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Language selector
                Box {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(SawtCard)
                            .border(1.dp, SawtCardBorder, CircleShape)
                            .clickable { langMenuExpanded = true }
                            .testTag("language_selector_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentLanguage.code.uppercase(),
                            color = MoroccanGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    DropdownMenu(
                        expanded = langMenuExpanded,
                        onDismissRequest = { langMenuExpanded = false },
                        modifier = Modifier.background(SawtCard)
                    ) {
                        AppLanguage.values().forEach { lang ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = lang.displayName,
                                        color = if (lang == currentLanguage) MoroccanGold else SawtTextPrimary,
                                        fontWeight = if (lang == currentLanguage) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    onLanguageChange(lang)
                                    langMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                // Role Switcher Button (Artist Portal <-> Admin Distribution)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (currentRole == UserRole.ADMIN) MoroccanRed.copy(alpha = 0.25f)
                            else MoroccanGreen.copy(alpha = 0.25f)
                        )
                        .border(
                            1.dp,
                            if (currentRole == UserRole.ADMIN) MoroccanRed else MoroccanGreen,
                            RoundedCornerShape(16.dp)
                        )
                        .clickable {
                            val newRole = if (currentRole == UserRole.ARTIST) UserRole.ADMIN else UserRole.ARTIST
                            onRoleChange(newRole)
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("role_switcher_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (currentRole == UserRole.ADMIN) Icons.Default.AdminPanelSettings else Icons.Default.Person,
                            contentDescription = "Switch Mode",
                            tint = if (currentRole == UserRole.ADMIN) MoroccanRed else MoroccanGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = if (currentRole == UserRole.ADMIN) "ADMIN" else "ARTIST",
                            color = SawtTextPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(SawtCardBorder)
        )
    }
}
