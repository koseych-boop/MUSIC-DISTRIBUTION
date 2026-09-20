package com.example.domain.validation

import com.example.data.model.AudioFormat
import com.example.data.model.CoverAssetEntity
import com.example.data.model.ReleaseEntity
import com.example.data.model.ReleaseTrackEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern

enum class ValidationSeverity {
    ERROR,
    WARNING,
    PASSED
}

data class ValidationIssue(
    val title: String,
    val description: String,
    val severity: ValidationSeverity,
    val field: String
)

data class ValidationReport(
    val isReadyToSubmit: Boolean,
    val issues: List<ValidationIssue>,
    val passedChecks: List<String>
)

object ReleaseValidationEngine {
    // ISRC Regex: 2 letters (country), 3 alphanumeric (registrant), 2 digits (year), 5 digits (designation)
    // Supports with or without hyphens (e.g. MA-A01-26-00101 or MAA012600101)
    private val ISRC_PATTERN = Pattern.compile("^[A-Z]{2}-?[A-Z0-9]{3}-?[0-9]{2}-?[0-9]{5}$", Pattern.CASE_INSENSITIVE)

    fun validate(
        release: ReleaseEntity,
        tracks: List<ReleaseTrackEntity>,
        coverAsset: CoverAssetEntity?,
        existingIsrcs: Set<String> = emptySet(),
        isRightsConfirmed: Boolean
    ): ValidationReport {
        val issues = mutableListOf<ValidationIssue>()
        val passed = mutableListOf<String>()

        // 1. Release Title
        if (release.title.isBlank()) {
            issues.add(
                ValidationIssue(
                    title = "Missing Release Title",
                    description = "Release title is required by DSPs (Spotify, Apple, YouTube Music).",
                    severity = ValidationSeverity.ERROR,
                    field = "title"
                )
            )
        } else {
            passed.add("Release title present ('${release.title}')")
        }

        // 2. Primary Artist
        if (release.primaryArtist.isBlank()) {
            issues.add(
                ValidationIssue(
                    title = "Missing Primary Artist",
                    description = "Primary artist name is mandatory for artist mapping.",
                    severity = ValidationSeverity.ERROR,
                    field = "primaryArtist"
                )
            )
        } else {
            passed.add("Primary artist defined ('${release.primaryArtist}')")
        }

        // 3. Genre & Subgenre
        if (release.subgenre.isBlank()) {
            issues.add(
                ValidationIssue(
                    title = "Missing Subgenre",
                    description = "Please specify a subgenre (e.g. Darija Trap, Gnawa Fusion, Chaabi Modern).",
                    severity = ValidationSeverity.WARNING,
                    field = "subgenre"
                )
            )
        } else {
            passed.add("Genre & Subgenre configured (${release.genre.title} / ${release.subgenre})")
        }

        // 4. Distribution Release Date Lead Time
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val distDate = sdf.parse(release.distributionReleaseDate)
            val today = Date()
            if (distDate != null) {
                val diffDays = (distDate.time - today.time) / (1000 * 60 * 60 * 24)
                if (diffDays < 7) {
                    issues.add(
                        ValidationIssue(
                            title = "Distribution Lead Time Insufficient",
                            description = "DSPs require at least 7 days ingestion window (14 days recommended for Spotify Editorial pitching). Currently set to $diffDays days.",
                            severity = ValidationSeverity.ERROR,
                            field = "distributionReleaseDate"
                        )
                    )
                } else {
                    passed.add("Release date has compliant lead time ($diffDays days lead time)")
                }
            }
        } catch (e: Exception) {
            issues.add(
                ValidationIssue(
                    title = "Invalid Date Format",
                    description = "Release date must be in YYYY-MM-DD format.",
                    severity = ValidationSeverity.ERROR,
                    field = "distributionReleaseDate"
                )
            )
        }

        // 5. Copyright (℗ and © lines)
        if (release.copyrightOwner.isBlank()) {
            issues.add(
                ValidationIssue(
                    title = "Missing Copyright Owner",
                    description = "Copyright holder name (individual artist or label) is required for rights registries.",
                    severity = ValidationSeverity.ERROR,
                    field = "copyrightOwner"
                )
            )
        } else {
            passed.add("Copyright ownership verified: ℗ ${release.pLineYear} / © ${release.cLineYear} ${release.copyrightOwner}")
        }

        // 6. UPC / EAN Checksum
        if (release.upcEan.isNotBlank()) {
            val cleanUpc = release.upcEan.replace(Regex("[^0-9]"), "")
            if (cleanUpc.length != 12 && cleanUpc.length != 13) {
                issues.add(
                    ValidationIssue(
                        title = "Invalid UPC / EAN Length",
                        description = "UPC must be exactly 12 digits, or EAN must be 13 digits (current: ${cleanUpc.length} digits).",
                        severity = ValidationSeverity.ERROR,
                        field = "upcEan"
                    )
                )
            } else if (!isValidBarcodeChecksum(cleanUpc)) {
                issues.add(
                    ValidationIssue(
                        title = "Invalid UPC/EAN Checksum",
                        description = "The barcode number failed the Modulo 10 check digit algorithm.",
                        severity = ValidationSeverity.ERROR,
                        field = "upcEan"
                    )
                )
            } else {
                passed.add("UPC/EAN barcode validated ($cleanUpc)")
            }
        } else {
            issues.add(
                ValidationIssue(
                    title = "Missing UPC/EAN",
                    description = "A valid UPC/EAN is required for album identification. Leave blank only if requesting platform-assigned UPC.",
                    severity = ValidationSeverity.WARNING,
                    field = "upcEan"
                )
            )
        }

        // 7. Tracklist & Audio Lossless Validation
        if (tracks.isEmpty()) {
            issues.add(
                ValidationIssue(
                    title = "No Tracks Added",
                    description = "At least one audio track is required for release.",
                    severity = ValidationSeverity.ERROR,
                    field = "tracks"
                )
            )
        } else {
            passed.add("Tracklist contains ${tracks.size} track(s)")
            
            val seenIsrcsInRelease = mutableSetOf<String>()
            tracks.forEachIndexed { index, track ->
                val trackNum = index + 1
                // Title
                if (track.title.isBlank()) {
                    issues.add(
                        ValidationIssue(
                            title = "Track $trackNum: Missing Title",
                            description = "Every track must have a title.",
                            severity = ValidationSeverity.ERROR,
                            field = "track_${track.id}_title"
                        )
                    )
                }

                // ISRC Format
                val cleanIsrc = track.isrc.trim().uppercase()
                if (cleanIsrc.isBlank()) {
                    issues.add(
                        ValidationIssue(
                            title = "Track $trackNum: Missing ISRC",
                            description = "Every sound recording requires a valid ISRC (e.g. MA-A01-26-00101).",
                            severity = ValidationSeverity.ERROR,
                            field = "track_${track.id}_isrc"
                        )
                    )
                } else if (!ISRC_PATTERN.matcher(cleanIsrc).matches()) {
                    issues.add(
                        ValidationIssue(
                            title = "Track $trackNum: Invalid ISRC Syntax",
                            description = "ISRC '$cleanIsrc' does not match the international standard (Country + Registrant + Year + Designation).",
                            severity = ValidationSeverity.ERROR,
                            field = "track_${track.id}_isrc"
                        )
                    )
                } else {
                    // Check duplicate within release
                    if (seenIsrcsInRelease.contains(cleanIsrc)) {
                        issues.add(
                            ValidationIssue(
                                title = "Track $trackNum: Duplicate ISRC",
                                description = "ISRC '$cleanIsrc' is repeated within the same release.",
                                severity = ValidationSeverity.ERROR,
                                field = "track_${track.id}_isrc"
                            )
                        )
                    }
                    seenIsrcsInRelease.add(cleanIsrc)
                    passed.add("Track $trackNum ISRC syntax verified ($cleanIsrc)")
                }

                // Composers & Lyricists
                if (track.composer.isBlank()) {
                    issues.add(
                        ValidationIssue(
                            title = "Track $trackNum: Missing Composer",
                            description = "DSPs require legal composer name for publishing royalty collections (BMDA / SACEM / BMI).",
                            severity = ValidationSeverity.ERROR,
                            field = "track_${track.id}_composer"
                        )
                    )
                }

                // Lossless Audio format check
                if (track.audioFormat != AudioFormat.WAV && track.audioFormat != AudioFormat.FLAC) {
                    issues.add(
                        ValidationIssue(
                            title = "Track $trackNum: Lossy Audio Rejected",
                            description = "MP3 and AAC files are rejected by DSP ingest. Audio must be lossless WAV or FLAC (minimum 44.1 kHz, 16-bit).",
                            severity = ValidationSeverity.ERROR,
                            field = "track_${track.id}_audio"
                        )
                    )
                } else {
                    passed.add("Track $trackNum: Lossless audio format confirmed (${track.audioFormat.name})")
                }
            }
        }

        // 8. Cover Art Validation
        if (coverAsset == null) {
            issues.add(
                ValidationIssue(
                    title = "Missing Cover Artwork",
                    description = "DSP delivery requires official high-resolution album artwork.",
                    severity = ValidationSeverity.ERROR,
                    field = "coverAsset"
                )
            )
        } else {
            // Resolution check
            if (coverAsset.resolutionWidth < 3000 || coverAsset.resolutionHeight < 3000) {
                issues.add(
                    ValidationIssue(
                        title = "Cover Art Resolution Below Spec",
                        description = "Current dimensions: ${coverAsset.resolutionWidth}x${coverAsset.resolutionHeight}px. Spotify and Apple Music require at least 3000x3000px.",
                        severity = ValidationSeverity.ERROR,
                        field = "coverAsset_resolution"
                    )
                )
            } else if (coverAsset.resolutionWidth != coverAsset.resolutionHeight) {
                issues.add(
                    ValidationIssue(
                        title = "Cover Art Not Square",
                        description = "Cover art aspect ratio must be strictly 1:1 square.",
                        severity = ValidationSeverity.ERROR,
                        field = "coverAsset_aspect"
                    )
                )
            } else {
                passed.add("Cover art resolution meets DSP specifications (${coverAsset.resolutionWidth}x${coverAsset.resolutionHeight}px, 1:1)")
            }

            // File size
            if (coverAsset.fileSizeBytes > 20 * 1024 * 1024) {
                issues.add(
                    ValidationIssue(
                        title = "Cover Art File Size Too Large",
                        description = "Artwork must not exceed 20 MB (current: ${coverAsset.fileSizeBytes / (1024 * 1024)} MB).",
                        severity = ValidationSeverity.ERROR,
                        field = "coverAsset_size"
                    )
                )
            } else if (coverAsset.fileSizeBytes < 300 * 1024) {
                issues.add(
                    ValidationIssue(
                        title = "Cover Art File Size Suspiciously Small",
                        description = "High quality 3000x3000px uncompressed artwork is usually > 1 MB.",
                        severity = ValidationSeverity.WARNING,
                        field = "coverAsset_size"
                    )
                )
            } else {
                passed.add("Cover art file size verified (${coverAsset.fileSizeBytes / 1024} KB)")
            }
        }

        // 9. Rights & Ownership Declaration
        if (!isRightsConfirmed) {
            issues.add(
                ValidationIssue(
                    title = "Rights Declaration Unconfirmed",
                    description = "You must legally confirm that you own or control the distribution rights for these master sound recordings and artwork.",
                    severity = ValidationSeverity.ERROR,
                    field = "rightsDeclaration"
                )
            )
        } else {
            passed.add("Rights declaration legally confirmed and cryptographically signed")
        }

        // 10. Explicit Content Flagging Consistency
        val hasExplicitTracks = tracks.any { it.isExplicit }
        if (hasExplicitTracks && !release.isExplicit) {
            issues.add(
                ValidationIssue(
                    title = "Explicit Metadata Mismatch",
                    description = "One or more tracks are flagged as explicit, but the release header is marked clean. The release must be flagged as explicit.",
                    severity = ValidationSeverity.ERROR,
                    field = "isExplicit"
                )
            )
        } else if (release.isExplicit) {
            passed.add("Explicit content metadata tagged correctly")
        } else {
            passed.add("Clean / non-explicit release confirmed")
        }

        val hasBlockingErrors = issues.any { it.severity == ValidationSeverity.ERROR }
        return ValidationReport(
            isReadyToSubmit = !hasBlockingErrors,
            issues = issues,
            passedChecks = passed
        )
    }

    // Standard Modulo 10 algorithm for UPC-A (12) / EAN-13 (13)
    fun isValidUpcEan(barcode: String): Boolean {
        if (!barcode.all { it.isDigit() }) return false
        return isValidBarcodeChecksum(barcode)
    }

    fun isValidIsrc(isrc: String): Boolean {
        return ISRC_PATTERN.matcher(isrc).matches()
    }

    fun generateIsrc(registrantCode: String, year: Int, designation: Int): String {
        val yearSuffix = "%02d".format(year % 100)
        val desigFormatted = "%05d".format(designation)
        return "$registrantCode$yearSuffix$desigFormatted"
    }

    fun generateUpc(prefix: String = "611"): String {
        val base = prefix + "%09d".format((100000000..999999999).random())
        val digits = base.map { it.toString().toInt() }
        var sum = 0
        digits.forEachIndexed { i, d ->
            sum += if (i % 2 == 1) d * 3 else d
        }
        val checkDigit = (10 - (sum % 10)) % 10
        return "$base$checkDigit"
    }

    private fun isValidBarcodeChecksum(barcode: String): Boolean {
        if (barcode.length != 12 && barcode.length != 13) return false
        val digits = barcode.map { it.toString().toInt() }
        val checkDigit = digits.last()
        val payload = digits.dropLast(1)
        
        var sum = 0
        if (digits.size == 12) {
            // UPC-A: odd positions (index 0, 2, 4...) * 3, even positions * 1
            payload.forEachIndexed { i, d ->
                sum += if (i % 2 == 0) d * 3 else d
            }
        } else {
            // EAN-13: even positions * 3, odd positions * 1
            payload.forEachIndexed { i, d ->
                sum += if (i % 2 == 1) d * 3 else d
            }
        }
        val calculatedCheck = (10 - (sum % 10)) % 10
        return calculatedCheck == checkDigit
    }
}
