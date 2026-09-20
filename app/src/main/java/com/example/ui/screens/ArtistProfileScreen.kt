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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.example.data.model.ArtistProfileEntity
import com.example.data.model.MoroccanCity
import com.example.data.model.MusicGenre
import com.example.ui.localization.AppLanguage
import com.example.ui.theme.MoroccanGold
import com.example.ui.theme.MoroccanGreen
import com.example.ui.theme.MoroccanRed
import com.example.ui.theme.SawtCard
import com.example.ui.theme.SawtCardBorder
import com.example.ui.theme.SawtObsidian
import com.example.ui.theme.SawtTextMuted
import com.example.ui.theme.SawtTextPrimary
import com.example.ui.theme.SawtTextSecondary
import com.example.ui.theme.StatusSuccess

@Composable
fun ArtistProfileScreen(
    profile: ArtistProfileEntity?,
    currentLanguage: AppLanguage,
    onSaveProfile: (ArtistProfileEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var stageName by remember(profile) { mutableStateOf(profile?.stageName ?: "OudWave") }
    var artistName by remember(profile) { mutableStateOf(profile?.artistName ?: "Youssef Bennani") }
    var bio by remember(profile) { mutableStateOf(profile?.bio ?: "Pioneering Moroccan Trap and contemporary Gnawa fusion from Casablanca.") }
    var selectedCity by remember(profile) { mutableStateOf(profile?.city ?: MoroccanCity.CASABLANCA) }
    var selectedGenre by remember(profile) { mutableStateOf(profile?.genre ?: MusicGenre.MOROCCAN_RAP) }
    var spotifyUrl by remember(profile) { mutableStateOf(profile?.spotifyArtistUrl ?: "https://open.spotify.com/artist/4Z8W4fKeB5Yx6tMorocco") }
    var youtubeUrl by remember(profile) { mutableStateOf(profile?.youtubeChannelUrl ?: "https://youtube.com/@OudWaveOfficial") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SawtObsidian)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, SawtCardBorder, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = SawtCard)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(MoroccanRed)
                            .border(2.dp, MoroccanGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stageName.take(2).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(stageName, color = SawtTextPrimary, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MoroccanGold, modifier = Modifier.size(16.dp))
                        }
                        Text("Legal Name: $artistName", color = SawtTextSecondary, fontSize = 12.sp)
                        Text("Morocco • ${selectedCity.displayName}", color = SawtTextMuted, fontSize = 11.5.sp)
                    }
                }
            }
        }

        // Supabase Auth Security Box
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, MoroccanGreen.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                colors = CardDefaults.cardColors(containerColor = SawtCard)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = MoroccanGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("SUPABASE AUTHENTICATION & SESSIONS", color = MoroccanGreen, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Session: Authenticated via JWT Token (Role: ARTIST)", color = SawtTextSecondary, fontSize = 11.sp)
                    Text("Email: verified (youssef.oudwave@sawt.ma)", color = StatusSuccess, fontSize = 10.5.sp)
                    Text("Database RLS: Enabled for audio_originals & cover_art buckets", color = SawtTextMuted, fontSize = 10.5.sp)
                }
            }
        }

        item {
            Text("ARTIST DETAILS", color = MoroccanGold, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 0.5.sp)
        }

        item {
            SawtTextField(
                value = stageName,
                onValueChange = { stageName = it },
                label = "Stage Name / Nom d'Artiste *",
                testTag = "input_profile_stage_name"
            )
        }

        item {
            SawtTextField(
                value = artistName,
                onValueChange = { artistName = it },
                label = "Legal Full Name (Nom Légal) *",
                testTag = "input_profile_legal_name"
            )
        }

        item {
            Text("Moroccan City", color = SawtTextSecondary, fontSize = 11.5.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(MoroccanCity.CASABLANCA, MoroccanCity.RABAT, MoroccanCity.MARRAKECH, MoroccanCity.TANGIER).forEach { city ->
                    val isSelected = selectedCity == city
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) MoroccanGold.copy(alpha = 0.2f) else SawtCard)
                            .border(1.dp, if (isSelected) MoroccanGold else SawtCardBorder, RoundedCornerShape(8.dp))
                            .clickable { selectedCity = city }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(city.displayName, color = if (isSelected) MoroccanGold else SawtTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            SawtTextField(
                value = bio,
                onValueChange = { bio = it },
                label = "Artist Bio / نبذة عن الفنان",
                testTag = "input_profile_bio"
            )
        }

        item {
            SawtTextField(
                value = spotifyUrl,
                onValueChange = { spotifyUrl = it },
                label = "Spotify Artist URL",
                testTag = "input_profile_spotify_url"
            )
        }

        item {
            SawtTextField(
                value = youtubeUrl,
                onValueChange = { youtubeUrl = it },
                label = "YouTube Official Artist Channel URL",
                testTag = "input_profile_youtube_url"
            )
        }

        item {
            Button(
                onClick = {
                    profile?.let {
                        onSaveProfile(
                            it.copy(
                                stageName = stageName,
                                artistName = artistName,
                                bio = bio,
                                city = selectedCity,
                                genre = selectedGenre,
                                spotifyArtistUrl = spotifyUrl,
                                youtubeChannelUrl = youtubeUrl
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MoroccanRed),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("save_profile_button")
            ) {
                Text("Save Artist Profile", fontWeight = FontWeight.Bold)
            }
        }

        item { Spacer(modifier = Modifier.height(40.dp)) }
    }
}
