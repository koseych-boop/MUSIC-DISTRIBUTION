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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.example.data.model.AudioFormat
import com.example.data.model.CoverAssetEntity
import com.example.data.model.MusicGenre
import com.example.data.model.ReleaseEntity
import com.example.data.model.ReleaseStatus
import com.example.data.model.ReleaseTrackEntity
import com.example.data.model.RightsDeclarationEntity
import com.example.data.model.Territory
import com.example.domain.validation.ReleaseValidationEngine
import com.example.domain.validation.ValidationSeverity
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.Strings
import com.example.ui.theme.MoroccanGold
import com.example.ui.theme.MoroccanGreen
import com.example.ui.theme.MoroccanRed
import com.example.ui.theme.MoroccanRedDark
import com.example.ui.theme.SawtCard
import com.example.ui.theme.SawtCardBorder
import com.example.ui.theme.SawtObsidian
import com.example.ui.theme.SawtSurface
import com.example.ui.theme.SawtTextMuted
import com.example.ui.theme.SawtTextPrimary
import com.example.ui.theme.SawtTextSecondary
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateReleaseScreen(
    artistId: String,
    primaryArtistDefault: String,
    currentLanguage: AppLanguage,
    onBackClick: () -> Unit,
    onSubmitRelease: (ReleaseEntity, List<ReleaseTrackEntity>, CoverAssetEntity, RightsDeclarationEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentStep by remember { mutableStateOf(1) } // 1: Metadata, 2: Tracks, 3: Artwork, 4: Rights, 5: Validation

    // Step 1 State
    var releaseTitle by remember { mutableStateOf("") }
    var primaryArtist by remember { mutableStateOf(primaryArtistDefault) }
    var featuringArtists by remember { mutableStateOf("") }
    var version by remember { mutableStateOf("Original Mix") }
    var selectedType by remember { mutableStateOf(com.example.data.model.ReleaseType.SINGLE) }
    var selectedGenre by remember { mutableStateOf(MusicGenre.MOROCCAN_RAP) }
    var subgenre by remember { mutableStateOf("Darija Trap") }
    var language by remember { mutableStateOf("Moroccan Arabic (Darija)") }
    var originalReleaseDate by remember { mutableStateOf("2026-09-20") }
    var distributionReleaseDate by remember { mutableStateOf("2026-10-08") } // Compliant 18 days lead time
    var copyrightOwner by remember { mutableStateOf("$primaryArtistDefault Music") }
    var pLineYear by remember { mutableStateOf("2026") }
    var cLineYear by remember { mutableStateOf("2026") }
    var labelName by remember { mutableStateOf("Sawt Independent") }
    var selectedTerritory by remember { mutableStateOf(Territory.WORLDWIDE) }
    var isExplicit by remember { mutableStateOf(false) }
    var upcEan by remember { mutableStateOf("840123456789") } // Valid 12-digit UPC
    var spotifyArtistUrl by remember { mutableStateOf("") }
    var youtubeChannelUrl by remember { mutableStateOf("") }

    // Step 2 Tracks State
    val tracks = remember {
        mutableStateListOf(
            ReleaseTrackEntity(
                id = UUID.randomUUID().toString(),
                releaseId = "temp",
                trackNumber = 1,
                title = "",
                isrc = "MA-A01-26-00101",
                primaryArtist = primaryArtistDefault,
                composer = primaryArtistDefault,
                lyricist = primaryArtistDefault,
                producer = primaryArtistDefault,
                audioFormat = AudioFormat.WAV,
                isExplicit = false,
                durationSeconds = 210
            )
        )
    }

    // Step 3 Artwork State
    var hasUploadedArtwork by remember { mutableStateOf(true) }
    var artworkWidth by remember { mutableStateOf(3000) }
    var artworkHeight by remember { mutableStateOf(3000) }
    var artworkFileSize by remember { mutableStateOf(4200000L) } // 4.2 MB

    // Step 4 Rights Declaration State
    var rightsConfirmed by remember { mutableStateOf(false) }
    var legalSignerName by remember { mutableStateOf(primaryArtistDefault) }

    // Temporary release object for validation engine
    val tempRelease = remember(
        releaseTitle, primaryArtist, featuringArtists, version, selectedType,
        selectedGenre, subgenre, language, originalReleaseDate, distributionReleaseDate,
        copyrightOwner, pLineYear, cLineYear, labelName, selectedTerritory, isExplicit, upcEan
    ) {
        ReleaseEntity(
            id = UUID.randomUUID().toString(),
            artistId = artistId,
            title = releaseTitle,
            releaseType = selectedType,
            status = ReleaseStatus.PENDING_REVIEW,
            primaryArtist = primaryArtist,
            featuringArtists = featuringArtists,
            version = version,
            genre = selectedGenre,
            subgenre = subgenre,
            language = language,
            originalReleaseDate = originalReleaseDate,
            distributionReleaseDate = distributionReleaseDate,
            copyrightOwner = copyrightOwner,
            pLineYear = pLineYear.toIntOrNull() ?: 2026,
            cLineYear = cLineYear.toIntOrNull() ?: 2026,
            labelName = labelName,
            territory = selectedTerritory,
            isExplicit = isExplicit,
            upcEan = upcEan
        )
    }

    val tempCoverAsset = remember(hasUploadedArtwork, artworkWidth, artworkHeight, artworkFileSize) {
        if (hasUploadedArtwork) {
            CoverAssetEntity(
                releaseId = tempRelease.id,
                bucketName = "cover_art",
                filePath = "cover_art/${tempRelease.id}/artwork.jpg",
                resolutionWidth = artworkWidth,
                resolutionHeight = artworkHeight,
                fileSizeBytes = artworkFileSize,
                isValidated = true
            )
        } else null
    }

    val validationReport by remember(tempRelease, tracks.toList(), tempCoverAsset, rightsConfirmed) {
        derivedStateOf {
            ReleaseValidationEngine.validate(
                release = tempRelease,
                tracks = tracks.toList(),
                coverAsset = tempCoverAsset,
                isRightsConfirmed = rightsConfirmed
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SawtObsidian)
    ) {
        // Top Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SawtSurface)
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.testTag("create_release_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = SawtTextPrimary
                )
            }
            Text(
                text = "CREATE RELEASE (خطوات الإصدار)",
                color = MoroccanGold,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        // Stepper Header (1 to 5)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SawtCard)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf(
                1 to "Info",
                2 to "Audio",
                3 to "Artwork",
                4 to "Rights",
                5 to "Validate"
            ).forEach { (stepNum, title) ->
                val isCompleted = currentStep > stepNum
                val isCurrent = currentStep == stepNum

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { currentStep = stepNum }
                ) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isCompleted -> MoroccanGreen
                                    isCurrent -> MoroccanRed
                                    else -> SawtCardBorder
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompleted) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        } else {
                            Text(text = "$stepNum", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = title,
                        color = if (isCurrent) MoroccanGold else SawtTextSecondary,
                        fontSize = 10.sp,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        // Wizard Step Body
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { Spacer(modifier = Modifier.height(10.dp)) }

            when (currentStep) {
                1 -> {
                    // STEP 1: RELEASE INFO
                    item {
                        StepTitle("Step 1: Release Information", "General metadata required by DSPs (Spotify, Apple Music, YouTube).")
                    }

                    item {
                        // Release Type: Single, EP, Album
                        Text("Release Format", color = SawtTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            com.example.data.model.ReleaseType.values().forEach { type ->
                                val isSelected = selectedType == type
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) MoroccanRed.copy(alpha = 0.2f) else SawtCard)
                                        .border(1.dp, if (isSelected) MoroccanRed else SawtCardBorder, RoundedCornerShape(8.dp))
                                        .clickable {
                                            selectedType = type
                                            if (type == com.example.data.model.ReleaseType.EP && tracks.size < 2) {
                                                tracks.add(
                                                    ReleaseTrackEntity(
                                                        id = UUID.randomUUID().toString(),
                                                        releaseId = "temp",
                                                        trackNumber = tracks.size + 1,
                                                        title = "Track 2",
                                                        isrc = "MA-A01-26-00102",
                                                        primaryArtist = primaryArtistDefault,
                                                        composer = primaryArtistDefault,
                                                        lyricist = primaryArtistDefault,
                                                        producer = primaryArtistDefault,
                                                        audioFormat = AudioFormat.WAV,
                                                        durationSeconds = 195
                                                    )
                                                )
                                            }
                                        }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = type.name,
                                        color = if (isSelected) MoroccanRed else SawtTextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }

                    item {
                        SawtTextField(
                            value = releaseTitle,
                            onValueChange = { releaseTitle = it },
                            label = "Release Title *",
                            placeholder = "e.g. Casablanca Nights / ليل كازا",
                            testTag = "input_release_title"
                        )
                    }

                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SawtTextField(
                                value = primaryArtist,
                                onValueChange = { primaryArtist = it },
                                label = "Primary Artist *",
                                modifier = Modifier.weight(1f),
                                testTag = "input_primary_artist"
                            )
                            SawtTextField(
                                value = featuringArtists,
                                onValueChange = { featuringArtists = it },
                                label = "Featuring Artists",
                                placeholder = "Optional",
                                modifier = Modifier.weight(1f),
                                testTag = "input_feat_artist"
                            )
                        }
                    }

                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SawtTextField(
                                value = version,
                                onValueChange = { version = it },
                                label = "Version",
                                placeholder = "Original Mix / Radio Edit",
                                modifier = Modifier.weight(1f),
                                testTag = "input_version"
                            )
                            SawtTextField(
                                value = subgenre,
                                onValueChange = { subgenre = it },
                                label = "Subgenre *",
                                placeholder = "Darija Trap / Gnawa Fusion",
                                modifier = Modifier.weight(1f),
                                testTag = "input_subgenre"
                            )
                        }
                    }

                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SawtTextField(
                                value = language,
                                onValueChange = { language = it },
                                label = "Language",
                                modifier = Modifier.weight(1f),
                                testTag = "input_language"
                            )
                            SawtTextField(
                                value = distributionReleaseDate,
                                onValueChange = { distributionReleaseDate = it },
                                label = "Distribution Date (YYYY-MM-DD) *",
                                modifier = Modifier.weight(1f),
                                testTag = "input_dist_date"
                            )
                        }
                    }

                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SawtTextField(
                                value = copyrightOwner,
                                onValueChange = { copyrightOwner = it },
                                label = "Copyright Owner *",
                                modifier = Modifier.weight(1f),
                                testTag = "input_copyright_owner"
                            )
                            SawtTextField(
                                value = labelName,
                                onValueChange = { labelName = it },
                                label = "Record Label Name",
                                modifier = Modifier.weight(1f),
                                testTag = "input_label_name"
                            )
                        }
                    }

                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            SawtTextField(
                                value = upcEan,
                                onValueChange = { upcEan = it },
                                label = "UPC / EAN Barcode",
                                modifier = Modifier.weight(1.4f),
                                testTag = "input_upc_ean"
                            )
                            Button(
                                onClick = { upcEan = "840" + (100000000L..999999999L).random().toString().take(9) },
                                colors = ButtonDefaults.buttonColors(containerColor = MoroccanGreen),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text("Assign UPC", fontSize = 10.5.sp)
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(SawtCard)
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Explicit Content Flag", color = SawtTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Contains explicit lyrics or adult themes", color = SawtTextSecondary, fontSize = 11.sp)
                            }
                            Switch(
                                checked = isExplicit,
                                onCheckedChange = { isExplicit = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = MoroccanRed, checkedTrackColor = MoroccanRedDark)
                            )
                        }
                    }
                }

                2 -> {
                    // STEP 2: TRACKLIST & AUDIO
                    item {
                        StepTitle("Step 2: Tracklist & Audio Files", "Upload high-quality lossless audio (WAV / FLAC). DSPs reject lossy MP3 files.")
                    }

                    itemsIndexed(tracks) { index, track ->
                        TrackItemEditor(
                            index = index,
                            track = track,
                            onUpdate = { updated -> tracks[index] = updated },
                            onDelete = if (tracks.size > 1) { { tracks.removeAt(index) } } else null
                        )
                    }

                    if (selectedType != com.example.data.model.ReleaseType.SINGLE) {
                        item {
                            Button(
                                onClick = {
                                    val nextNum = tracks.size + 1
                                    val formattedNum = "%02d".format(nextNum)
                                    tracks.add(
                                        ReleaseTrackEntity(
                                            id = UUID.randomUUID().toString(),
                                            releaseId = "temp",
                                            trackNumber = nextNum,
                                            title = "Track $nextNum",
                                            isrc = "MA-A01-26-001$formattedNum",
                                            primaryArtist = primaryArtist,
                                            composer = primaryArtist,
                                            lyricist = primaryArtist,
                                            producer = primaryArtist,
                                            audioFormat = AudioFormat.WAV,
                                            durationSeconds = 200
                                        )
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SawtCard),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, SawtCardBorder, RoundedCornerShape(8.dp))
                                    .testTag("add_track_button")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = MoroccanGold, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Add Track ${tracks.size + 1}", color = MoroccanGold, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                3 -> {
                    // STEP 3: COVER ART & STORAGE
                    item {
                        StepTitle("Step 3: Cover Art & Storage Buckets", "Official artwork must meet international DSP specifications: >= 3000x3000px, 1:1 square.")
                    }

                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, if (artworkWidth >= 3000) MoroccanGreen else StatusError, RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = SawtCard)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(90.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MoroccanRedDark)
                                            .border(1.dp, MoroccanGold, RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Image, contentDescription = null, tint = MoroccanGold, modifier = Modifier.size(36.dp))
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("artwork_official.jpg", color = SawtTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("Resolution: ${artworkWidth}x${artworkHeight} px (1:1 Square)", color = if (artworkWidth >= 3000) StatusSuccess else StatusError, fontSize = 12.sp)
                                        Text("Size: ${artworkFileSize / (1024 * 1024)} MB • RGB Color Space", color = SawtTextSecondary, fontSize = 11.sp)
                                        Text("Format: High-Res Lossless JPEG", color = SawtTextSecondary, fontSize = 11.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(
                                        onClick = {
                                            artworkWidth = 3000
                                            artworkHeight = 3000
                                            artworkFileSize = 4800000L
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MoroccanGold),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Set 3000x3000px Spec", fontSize = 11.sp)
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            artworkWidth = 1400 // Test non-compliant for validation test
                                            artworkHeight = 1400
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SawtTextMuted),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Test Low-Res (1400px)", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        // Storage buckets architecture preview
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, SawtCardBorder, RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = SawtCard)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("SECURE SUPABASE STORAGE BUCKETS", color = MoroccanGold, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 0.5.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                StorageBucketRow("cover_art", "Private bucket with signed tokens (Never public)")
                                StorageBucketRow("audio_originals", "Secure lossless master archive (WAV/FLAC)")
                                StorageBucketRow("audio_processed", "Transcoded DSP streaming cache (Protected)")
                                StorageBucketRow("artist_images", "Artist press kit & bio photos")
                            }
                        }
                    }
                }

                4 -> {
                    // STEP 4: RIGHTS & OWNERSHIP DECLARATION
                    item {
                        StepTitle("Step 4: Rights & Legal Ownership", "Legal declaration required prior to distribution submission.")
                    }

                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, if (rightsConfirmed) MoroccanGreen else MoroccanRed, RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = SawtCard)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.Shield, contentDescription = null, tint = MoroccanGold, modifier = Modifier.size(20.dp))
                                    Text("Rights & Master Ownership Confirmation", color = SawtTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SawtObsidian)
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = Strings.rightsConfirmation(currentLanguage),
                                        color = SawtTextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        lineHeight = 18.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                SawtTextField(
                                    value = legalSignerName,
                                    onValueChange = { legalSignerName = it },
                                    label = "Legal Signer Full Name *",
                                    placeholder = "e.g. Youssef Bennani",
                                    testTag = "input_legal_signer"
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clickable { rightsConfirmed = !rightsConfirmed }
                                        .testTag("rights_confirmation_checkbox")
                                ) {
                                    Checkbox(
                                        checked = rightsConfirmed,
                                        onCheckedChange = { rightsConfirmed = it },
                                        colors = CheckboxDefaults.colors(checkedColor = MoroccanGreen)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "I hereby legally certify this rights declaration.",
                                        color = if (rightsConfirmed) SawtTextPrimary else MoroccanGold,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Audit trail will record: IP 105.158.12.84 • Timestamp • Cryptographic Hash",
                                    color = SawtTextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }

                5 -> {
                    // STEP 5: AUTOMATIC RELEASE VALIDATION
                    item {
                        StepTitle("Step 5: Automatic Release Validation", "Pre-flight compliance engine checking international metadata standards.")
                    }

                    item {
                        // Big Ready / Fix Banner
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    2.dp,
                                    if (validationReport.isReadyToSubmit) MoroccanGreen else StatusError,
                                    RoundedCornerShape(12.dp)
                                ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (validationReport.isReadyToSubmit) MoroccanGreen.copy(alpha = 0.15f) else StatusError.copy(alpha = 0.15f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = if (validationReport.isReadyToSubmit) Icons.Default.CheckCircle else Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = if (validationReport.isReadyToSubmit) MoroccanGreen else StatusError,
                                    modifier = Modifier.size(38.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (validationReport.isReadyToSubmit) Strings.readyToSubmit(currentLanguage) else Strings.fixTheseIssues(currentLanguage),
                                    color = if (validationReport.isReadyToSubmit) StatusSuccess else StatusError,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 17.sp,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = if (validationReport.isReadyToSubmit) "All metadata, ISRC codes, UPC, lossless audio, and rights declarations pass DSP delivery criteria."
                                    else "Please resolve all blocking errors below before submitting for distribution.",
                                    color = SawtTextSecondary,
                                    fontSize = 11.5.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                            }
                        }
                    }

                    // Blocking Issues list
                    if (validationReport.issues.isNotEmpty()) {
                        item {
                            Text("COMPLIANCE DIAGNOSTICS (${validationReport.issues.size})", color = StatusError, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        itemsIndexed(validationReport.issues) { _, issue ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, if (issue.severity == ValidationSeverity.ERROR) StatusError else StatusWarning, RoundedCornerShape(8.dp)),
                                colors = CardDefaults.cardColors(containerColor = SawtCard)
                            ) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                                    Icon(
                                        imageVector = if (issue.severity == ValidationSeverity.ERROR) Icons.Default.Error else Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = if (issue.severity == ValidationSeverity.ERROR) StatusError else StatusWarning,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(text = issue.title, color = SawtTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(text = issue.description, color = SawtTextSecondary, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }

                    // Passed Checks list
                    if (validationReport.passedChecks.isNotEmpty()) {
                        item {
                            Text("PASSED CHECKS (${validationReport.passedChecks.size})", color = MoroccanGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        itemsIndexed(validationReport.passedChecks) { _, check ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SawtCard)
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = MoroccanGreen, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = check, color = SawtTextSecondary, fontSize = 11.5.sp)
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }

        // Bottom Wizard Navigation Bar (Back / Next / Submit)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SawtSurface)
                .border(1.dp, SawtCardBorder)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (currentStep > 1) {
                OutlinedButton(
                    onClick = { currentStep -= 1 },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SawtTextPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("wizard_prev_button")
                ) {
                    Text("Previous")
                }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }

            if (currentStep < 5) {
                Button(
                    onClick = { currentStep += 1 },
                    colors = ButtonDefaults.buttonColors(containerColor = MoroccanRed),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("wizard_next_button")
                ) {
                    Text("Next Step")
                }
            } else {
                // Submit Button
                Button(
                    onClick = {
                        val finalRelease = tempRelease.copy(status = ReleaseStatus.PENDING_REVIEW)
                        val rightsDec = RightsDeclarationEntity(
                            releaseId = finalRelease.id,
                            userId = artistId,
                            legalName = legalSignerName.ifBlank { primaryArtist },
                            isConfirmed = true
                        )
                        val cover = tempCoverAsset ?: CoverAssetEntity(
                            releaseId = finalRelease.id,
                            filePath = "cover_art/${finalRelease.id}/artwork.jpg"
                        )
                        onSubmitRelease(finalRelease, tracks.toList(), cover, rightsDec)
                    },
                    enabled = validationReport.isReadyToSubmit,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MoroccanGreen,
                        disabledContainerColor = SawtCardBorder
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("submit_release_button")
                ) {
                    Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Submit for Distribution", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun StepTitle(title: String, subtitle: String) {
    Column {
        Text(text = title, color = MoroccanGold, fontWeight = FontWeight.Black, fontSize = 16.sp)
        Text(text = subtitle, color = SawtTextSecondary, fontSize = 11.5.sp)
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
private fun StorageBucketRow(bucket: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Folder, contentDescription = null, tint = MoroccanGreen, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = bucket, color = SawtTextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text(text = desc, color = SawtTextMuted, fontSize = 10.sp)
        }
    }
}

@Composable
private fun TrackItemEditor(
    index: Int,
    track: ReleaseTrackEntity,
    onUpdate: (ReleaseTrackEntity) -> Unit,
    onDelete: (() -> Unit)?
) {
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
                Text(
                    text = "Track ${index + 1}: ${track.title.ifBlank { "Untitled" }}",
                    color = MoroccanGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                if (onDelete != null) {
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Track", tint = StatusError, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            SawtTextField(
                value = track.title,
                onValueChange = { onUpdate(track.copy(title = it)) },
                label = "Track Title *",
                placeholder = "e.g. Atlas Sun / شمس الأطلس",
                testTag = "input_track_${index}_title"
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SawtTextField(
                    value = track.isrc,
                    onValueChange = { onUpdate(track.copy(isrc = it)) },
                    label = "ISRC *",
                    placeholder = "MA-A01-26-00101",
                    modifier = Modifier.weight(1.3f),
                    testTag = "input_track_${index}_isrc"
                )
                Button(
                    onClick = {
                        val randomNum = "%05d".format((1..999).random())
                        onUpdate(track.copy(isrc = "MA-A01-26-$randomNum"))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MoroccanGreen),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Auto ISRC", fontSize = 10.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SawtTextField(
                    value = track.composer,
                    onValueChange = { onUpdate(track.copy(composer = it)) },
                    label = "Composer *",
                    modifier = Modifier.weight(1f),
                    testTag = "input_track_${index}_composer"
                )
                SawtTextField(
                    value = track.lyricist,
                    onValueChange = { onUpdate(track.copy(lyricist = it)) },
                    label = "Lyricist",
                    modifier = Modifier.weight(1f),
                    testTag = "input_track_${index}_lyricist"
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Lossless Format Selector (WAV / FLAC)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Audio Format: ", color = SawtTextSecondary, fontSize = 12.sp)
                    listOf(AudioFormat.WAV, AudioFormat.FLAC).forEach { fmt ->
                        val isSelected = track.audioFormat == fmt
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) MoroccanGold.copy(alpha = 0.2f) else SawtObsidian)
                                .border(1.dp, if (isSelected) MoroccanGold else SawtCardBorder, RoundedCornerShape(6.dp))
                                .clickable { onUpdate(track.copy(audioFormat = fmt)) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(fmt.name, color = if (isSelected) MoroccanGold else SawtTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Explicit", color = SawtTextSecondary, fontSize = 11.sp)
                    Checkbox(
                        checked = track.isExplicit,
                        onCheckedChange = { onUpdate(track.copy(isExplicit = it)) },
                        colors = CheckboxDefaults.colors(checkedColor = MoroccanRed)
                    )
                }
            }
        }
    }
}

@Composable
fun SawtTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    testTag: String = ""
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 12.sp) },
        placeholder = { if (placeholder.isNotBlank()) Text(placeholder, fontSize = 12.sp, color = SawtTextMuted) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = SawtTextPrimary,
            unfocusedTextColor = SawtTextPrimary,
            focusedBorderColor = MoroccanGold,
            unfocusedBorderColor = SawtCardBorder,
            focusedLabelColor = MoroccanGold,
            unfocusedLabelColor = SawtTextSecondary,
            focusedContainerColor = SawtCard,
            unfocusedContainerColor = SawtCard
        ),
        shape = RoundedCornerShape(8.dp),
        singleLine = true,
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTag)
    )
}
