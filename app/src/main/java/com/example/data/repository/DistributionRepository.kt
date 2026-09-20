package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.model.ArtistBalanceEntity
import com.example.data.model.ArtistProfileEntity
import com.example.data.model.AudioAssetEntity
import com.example.data.model.AuditLogEntity
import com.example.data.model.AudioFormat
import com.example.data.model.CoverAssetEntity
import com.example.data.model.DistributionDeliveryEntity
import com.example.data.model.DistributionJobEntity
import com.example.data.model.DistributionWebhookEntity
import com.example.data.model.DspPlatform
import com.example.data.model.DspStatus
import com.example.data.model.MoroccanCity
import com.example.data.model.MusicGenre
import com.example.data.model.NotificationEntity
import com.example.data.model.PayoutEntity
import com.example.data.model.PayoutMethod
import com.example.data.model.PayoutStatus
import com.example.data.model.ReleaseEntity
import com.example.data.model.ReleaseStatus
import com.example.data.model.ReleaseTrackEntity
import com.example.data.model.ReleaseType
import com.example.data.model.RightsDeclarationEntity
import com.example.data.model.RoyaltyReportEntity
import com.example.data.model.TakedownRequestEntity
import com.example.data.model.Territory
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.UUID

class DistributionRepository(context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val releaseDao = database.releaseDao()
    private val distributionDao = database.distributionDao()
    private val royaltyDao = database.royaltyDao()
    private val auditDao = database.auditDao()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedInitialDataIfNeeded()
        }
    }

    // Releases & Tracks
    val allReleases: Flow<List<ReleaseEntity>> = releaseDao.getAllReleases()
    fun getReleasesByArtist(artistId: String): Flow<List<ReleaseEntity>> = releaseDao.getReleasesByArtist(artistId)
    fun getReleaseById(id: String): Flow<ReleaseEntity?> = releaseDao.getReleaseById(id)
    suspend fun getReleaseByIdOnce(id: String): ReleaseEntity? = releaseDao.getReleaseByIdOnce(id)
    fun getTracksForRelease(releaseId: String): Flow<List<ReleaseTrackEntity>> = releaseDao.getTracksForRelease(releaseId)
    suspend fun getTracksForReleaseOnce(releaseId: String): List<ReleaseTrackEntity> = releaseDao.getTracksForReleaseOnce(releaseId)
    fun getCoverAsset(releaseId: String): Flow<CoverAssetEntity?> = releaseDao.getCoverAssetForRelease(releaseId)
    fun getRightsDeclaration(releaseId: String): Flow<RightsDeclarationEntity?> = releaseDao.getRightsDeclaration(releaseId)

    suspend fun createRelease(
        release: ReleaseEntity,
        tracks: List<ReleaseTrackEntity>,
        coverAsset: CoverAssetEntity,
        rightsDeclaration: RightsDeclarationEntity
    ) {
        releaseDao.insertRelease(release)
        releaseDao.insertTracks(tracks)
        releaseDao.insertCoverAsset(coverAsset)
        releaseDao.insertRightsDeclaration(rightsDeclaration)
        
        // Create audio asset records for each track
        tracks.forEach { track ->
            val audioAsset = AudioAssetEntity(
                trackId = track.id,
                bucketName = "audio_originals",
                filePath = "audio_originals/${release.id}/${track.id}.${track.audioFormat.name.lowercase()}",
                fileFormat = track.audioFormat,
                sampleRateHz = 48000,
                bitDepth = 24,
                isValidated = true
            )
            releaseDao.insertAudioAsset(audioAsset)
        }

        // Audit Log
        val auditLog = AuditLogEntity(
            actorId = release.artistId,
            actorRole = UserRole.ARTIST,
            action = "CREATE_RELEASE",
            targetType = "RELEASE",
            targetId = release.id,
            details = "Release '${release.title}' created with ${tracks.size} tracks and submitted for validation."
        )
        auditDao.insertAuditLog(auditLog)
    }

    suspend fun updateReleaseStatus(releaseId: String, newStatus: ReleaseStatus, adminNotes: String? = null, adminId: String = "ADMIN_SYSTEM") {
        val release = releaseDao.getReleaseByIdOnce(releaseId) ?: return
        val updated = release.copy(
            status = newStatus,
            adminNotes = adminNotes ?: release.adminNotes,
            updatedAt = System.currentTimeMillis()
        )
        releaseDao.updateRelease(updated)

        // Log audit
        auditDao.insertAuditLog(
            AuditLogEntity(
                actorId = adminId,
                actorRole = UserRole.ADMIN,
                action = "STATUS_CHANGE",
                targetType = "RELEASE",
                targetId = releaseId,
                details = "Status changed from ${release.status} to $newStatus. Notes: ${adminNotes ?: "None"}"
            )
        )

        // Notification to artist
        auditDao.insertNotification(
            NotificationEntity(
                userId = release.artistId,
                title = "Release Status: ${release.title}",
                message = "Your release status is now $newStatus. ${if (!adminNotes.isNullOrBlank()) "Admin note: $adminNotes" else ""}",
                type = "RELEASE_STATUS"
            )
        )
    }

    suspend fun updateReleaseDspStatus(
        releaseId: String,
        spotifyStatus: DspStatus? = null,
        spotifyUrl: String? = null,
        youtubeStatus: DspStatus? = null,
        youtubeArtTrackId: String? = null,
        youtubeAssetId: String? = null
    ) {
        val release = releaseDao.getReleaseByIdOnce(releaseId) ?: return
        val updated = release.copy(
            spotifyStatus = spotifyStatus ?: release.spotifyStatus,
            spotifyUrl = spotifyUrl ?: release.spotifyUrl,
            youtubeStatus = youtubeStatus ?: release.youtubeStatus,
            youtubeArtTrackId = youtubeArtTrackId ?: release.youtubeArtTrackId,
            youtubeAssetId = youtubeAssetId ?: release.youtubeAssetId,
            updatedAt = System.currentTimeMillis()
        )
        releaseDao.updateRelease(updated)
    }

    // Distribution Jobs
    val allJobs: Flow<List<DistributionJobEntity>> = distributionDao.getAllDistributionJobs()
    fun getJobsForRelease(releaseId: String): Flow<List<DistributionJobEntity>> = distributionDao.getJobsForRelease(releaseId)
    suspend fun insertJob(job: DistributionJobEntity) = distributionDao.insertJob(job)
    suspend fun updateJob(job: DistributionJobEntity) = distributionDao.updateJob(job)
    suspend fun insertDelivery(delivery: DistributionDeliveryEntity) = distributionDao.insertDelivery(delivery)
    fun getDeliveriesForJob(jobId: String): Flow<List<DistributionDeliveryEntity>> = distributionDao.getDeliveriesForJob(jobId)

    // Webhooks
    val allWebhooks: Flow<List<DistributionWebhookEntity>> = distributionDao.getAllWebhooks()
    suspend fun insertWebhook(webhook: DistributionWebhookEntity) = distributionDao.insertWebhook(webhook)

    // Royalties & Payouts
    fun getRoyaltyReports(artistId: String): Flow<List<RoyaltyReportEntity>> = royaltyDao.getReportsForArtist(artistId)
    fun getAllRoyaltyReports(): Flow<List<RoyaltyReportEntity>> = royaltyDao.getAllReports()
    fun getArtistBalance(artistId: String): Flow<ArtistBalanceEntity?> = royaltyDao.getArtistBalance(artistId)
    fun getPayouts(artistId: String): Flow<List<PayoutEntity>> = royaltyDao.getPayoutsForArtist(artistId)
    val allPayouts: Flow<List<PayoutEntity>> = royaltyDao.getAllPayouts()

    suspend fun requestPayout(
        artistId: String,
        artistName: String,
        amount: Double,
        method: PayoutMethod,
        accountDetails: String
    ): Boolean {
        val balance = royaltyDao.getArtistBalance(artistId).firstOrNull() ?: return false
        if (balance.availableBalance < amount || amount <= 0) return false

        val payout = PayoutEntity(
            artistId = artistId,
            artistName = artistName,
            amount = amount,
            currency = balance.currency,
            status = PayoutStatus.PENDING_REVIEW,
            paymentMethod = method,
            accountDetails = accountDetails
        )
        royaltyDao.insertPayout(payout)

        // Deduct from available balance, add to pending
        val newBalance = balance.copy(
            availableBalance = balance.availableBalance - amount,
            pendingBalance = balance.pendingBalance + amount
        )
        royaltyDao.upsertArtistBalance(newBalance)

        auditDao.insertAuditLog(
            AuditLogEntity(
                actorId = artistId,
                actorRole = UserRole.ARTIST,
                action = "REQUEST_PAYOUT",
                targetType = "PAYOUT",
                targetId = payout.id,
                details = "Requested payout of ${amount} ${balance.currency} via ${method.title}"
            )
        )
        return true
    }

    suspend fun processPayout(payoutId: String, approve: Boolean, adminNotes: String? = null) {
        val payouts = royaltyDao.getAllPayouts().firstOrNull() ?: return
        val payout = payouts.find { it.id == payoutId } ?: return
        val balance = royaltyDao.getArtistBalance(payout.artistId).firstOrNull() ?: return

        if (approve) {
            val updatedPayout = payout.copy(
                status = PayoutStatus.COMPLETED,
                processedAt = System.currentTimeMillis(),
                auditNotes = adminNotes ?: "Approved and wired to bank"
            )
            royaltyDao.updatePayout(updatedPayout)

            val updatedBalance = balance.copy(
                pendingBalance = maxOf(0.0, balance.pendingBalance - payout.amount),
                paidBalance = balance.paidBalance + payout.amount
            )
            royaltyDao.upsertArtistBalance(updatedBalance)

            auditDao.insertAuditLog(
                AuditLogEntity(
                    actorId = "ADMIN_SYSTEM",
                    actorRole = UserRole.ADMIN,
                    action = "APPROVE_PAYOUT",
                    targetType = "PAYOUT",
                    targetId = payoutId,
                    details = "Approved payout of ${payout.amount} ${payout.currency} to ${payout.artistName} via ${payout.paymentMethod.title}."
                )
            )

            auditDao.insertNotification(
                NotificationEntity(
                    userId = payout.artistId,
                    title = "Payout Processed",
                    message = "Your payout of ${payout.amount} ${payout.currency} has been transferred via ${payout.paymentMethod.title}.",
                    type = "PAYOUT"
                )
            )
        } else {
            val updatedPayout = payout.copy(
                status = PayoutStatus.REJECTED,
                processedAt = System.currentTimeMillis(),
                auditNotes = adminNotes ?: "Rejected by compliance"
            )
            royaltyDao.updatePayout(updatedPayout)

            // Revert funds back to available
            val updatedBalance = balance.copy(
                pendingBalance = maxOf(0.0, balance.pendingBalance - payout.amount),
                availableBalance = balance.availableBalance + payout.amount
            )
            royaltyDao.upsertArtistBalance(updatedBalance)

            auditDao.insertAuditLog(
                AuditLogEntity(
                    actorId = "ADMIN_SYSTEM",
                    actorRole = UserRole.ADMIN,
                    action = "REJECT_PAYOUT",
                    targetType = "PAYOUT",
                    targetId = payoutId,
                    details = "Rejected payout of ${payout.amount} ${payout.currency}. Reason: ${adminNotes ?: "Unspecified"}"
                )
            )
        }
    }

    // Takedowns & Audit
    val allAuditLogs: Flow<List<AuditLogEntity>> = auditDao.getAllAuditLogs()
    val allTakedownRequests: Flow<List<TakedownRequestEntity>> = auditDao.getAllTakedownRequests()

    suspend fun submitTakedownRequest(releaseId: String, releaseTitle: String, artistId: String, platform: String, reason: String) {
        val request = TakedownRequestEntity(
            releaseId = releaseId,
            releaseTitle = releaseTitle,
            artistId = artistId,
            platform = platform,
            reason = reason,
            status = "REQUESTED"
        )
        auditDao.insertTakedownRequest(request)
        auditDao.insertAuditLog(
            AuditLogEntity(
                actorId = artistId,
                actorRole = UserRole.ARTIST,
                action = "REQUEST_TAKEDOWN",
                targetType = "RELEASE",
                targetId = releaseId,
                details = "Takedown requested for '$releaseTitle' on platform $platform. Reason: $reason"
            )
        )
    }

    suspend fun processTakedown(requestId: String, approve: Boolean) {
        val requests = auditDao.getAllTakedownRequests().firstOrNull() ?: return
        val req = requests.find { it.id == requestId } ?: return
        val updated = req.copy(
            status = if (approve) "SENT_TO_PARTNER" else "REJECTED",
            completedAt = System.currentTimeMillis()
        )
        auditDao.updateTakedownRequest(updated)

        if (approve) {
            updateReleaseStatus(req.releaseId, ReleaseStatus.TAKEDOWN, "Takedown order sent to authorized partner.")
            updateReleaseDspStatus(
                req.releaseId,
                spotifyStatus = DspStatus.TAKEDOWN_REQUESTED,
                youtubeStatus = DspStatus.TAKEDOWN_REQUESTED
            )
        }

        auditDao.insertAuditLog(
            AuditLogEntity(
                actorId = "ADMIN_SYSTEM",
                actorRole = UserRole.ADMIN,
                action = if (approve) "APPROVE_TAKEDOWN" else "REJECT_TAKEDOWN",
                targetType = "TAKEDOWN",
                targetId = requestId,
                details = "Takedown request for '${req.releaseTitle}' ${if (approve) "approved & dispatched to DSP ingest" else "rejected"}."
            )
        )
    }

    // Notifications & Profile
    fun getNotifications(userId: String): Flow<List<NotificationEntity>> = auditDao.getNotifications(userId)
    fun getArtistProfile(userId: String): Flow<ArtistProfileEntity?> = auditDao.getArtistProfile(userId)
    suspend fun updateArtistProfile(profile: ArtistProfileEntity) = auditDao.updateArtistProfile(profile)

    // Seeding Realistic Moroccan Music Catalog
    private suspend fun seedInitialDataIfNeeded() {
        val currentReleases = releaseDao.getAllReleases().firstOrNull()
        if (!currentReleases.isNullOrEmpty()) return

        val defaultUserId = "artist_morocco_001"
        val defaultArtistId = "profile_morocco_001"

        // 1. User
        val user = UserEntity(
            id = defaultUserId,
            email = "youssef.oudwave@sawt.ma",
            username = "oudwave_official",
            role = UserRole.ARTIST
        )
        auditDao.insertUser(user)

        // 2. Artist Profile
        val profile = ArtistProfileEntity(
            id = defaultArtistId,
            userId = defaultUserId,
            artistName = "Youssef Bennani",
            stageName = "OudWave",
            bio = "Pioneering Moroccan Trap and contemporary Gnawa fusion from the vibrant underground scene of Casablanca. Blending ancient Guembri and Oud scales with 808 subs.",
            country = "Morocco",
            city = MoroccanCity.CASABLANCA,
            genre = MusicGenre.MOROCCAN_RAP,
            profilePhotoUri = null,
            spotifyArtistUrl = "https://open.spotify.com/artist/4Z8W4fKeB5Yx6tMorocco",
            youtubeChannelUrl = "https://youtube.com/@OudWaveOfficial",
            isVerified = true
        )
        auditDao.insertArtistProfile(profile)

        // 3. Balance
        val balance = ArtistBalanceEntity(
            id = "balance_001",
            artistId = defaultArtistId,
            pendingBalance = 2450.00,
            availableBalance = 18678.00,
            paidBalance = 35200.00,
            currency = "MAD"
        )
        royaltyDao.upsertArtistBalance(balance)

        // 4. Release 1: "NO SIGNAL" (LIVE on Spotify & YouTube Music)
        val release1Id = "rel_no_signal_001"
        val release1 = ReleaseEntity(
            id = release1Id,
            artistId = defaultArtistId,
            title = "NO SIGNAL",
            releaseType = ReleaseType.SINGLE,
            status = ReleaseStatus.LIVE,
            primaryArtist = "OudWave",
            featuringArtists = "El Ksar",
            version = "Original Mix",
            genre = MusicGenre.MOROCCAN_RAP,
            subgenre = "Darija Trap / 808",
            language = "Moroccan Arabic (Darija)",
            originalReleaseDate = "2026-08-15",
            distributionReleaseDate = "2026-08-25",
            copyrightOwner = "OudWave Records",
            pLineYear = 2026,
            cLineYear = 2026,
            labelName = "Sawt Independent",
            territory = Territory.WORLDWIDE,
            isExplicit = true,
            upcEan = "840123456789",
            coverImageUri = null,
            spotifyStatus = DspStatus.LIVE,
            spotifyReleaseId = "spot_rel_4892301",
            spotifyArtistId = "spot_art_991823",
            spotifyUrl = "https://open.spotify.com/album/4892301morocco",
            spotifyDeliveryDate = System.currentTimeMillis() - (25 * 86400000L),
            youtubeStatus = DspStatus.LIVE,
            youtubeAssetId = "yt_asset_morocco_9910",
            youtubeArtTrackId = "yt_art_track_381920",
            createdAt = System.currentTimeMillis() - (30 * 86400000L),
            updatedAt = System.currentTimeMillis() - (25 * 86400000L)
        )
        val track1 = ReleaseTrackEntity(
            id = "trk_no_signal_001",
            releaseId = release1Id,
            trackNumber = 1,
            title = "NO SIGNAL",
            version = "Original Mix",
            isrc = "MA-A01-26-00101",
            primaryArtist = "OudWave",
            featuringArtists = "El Ksar",
            composer = "Youssef Bennani",
            lyricist = "Youssef Bennani, Mehdi Ksar",
            producer = "Draganov Beatmaker",
            isExplicit = true,
            audioFormat = AudioFormat.WAV,
            durationSeconds = 194
        )
        val coverAsset1 = CoverAssetEntity(
            id = "cov_001",
            releaseId = release1Id,
            bucketName = "cover_art",
            filePath = "cover_art/$release1Id/artwork.jpg",
            resolutionWidth = 3000,
            resolutionHeight = 3000,
            aspectRatio = "1:1",
            fileSizeBytes = 4850000L,
            isValidated = true
        )
        val rights1 = RightsDeclarationEntity(
            id = "rgt_001",
            releaseId = release1Id,
            userId = defaultUserId,
            legalName = "Youssef Bennani",
            ipAddress = "105.158.12.84",
            isConfirmed = true
        )
        createRelease(release1, listOf(track1), coverAsset1, rights1)

        // 5. Release 2: "CHAABI REVOLUTION" (PENDING_REVIEW - Admin can review)
        val release2Id = "rel_chaabi_rev_002"
        val release2 = ReleaseEntity(
            id = release2Id,
            artistId = defaultArtistId,
            title = "CHAABI REVOLUTION",
            releaseType = ReleaseType.SINGLE,
            status = ReleaseStatus.PENDING_REVIEW,
            primaryArtist = "OudWave",
            featuringArtists = "Cheikha Zahra",
            version = "Radio Edit",
            genre = MusicGenre.CHAABI,
            subgenre = "Electronic Chaabi",
            language = "Moroccan Arabic (Darija)",
            originalReleaseDate = "2026-09-18",
            distributionReleaseDate = "2026-10-05",
            copyrightOwner = "Youssef Bennani",
            pLineYear = 2026,
            cLineYear = 2026,
            labelName = "Atlas Sounds",
            territory = Territory.WORLDWIDE,
            isExplicit = false,
            upcEan = "840987654321",
            coverImageUri = null,
            spotifyStatus = DspStatus.QUEUED,
            youtubeStatus = DspStatus.QUEUED,
            createdAt = System.currentTimeMillis() - (2 * 86400000L),
            updatedAt = System.currentTimeMillis() - (1 * 86400000L)
        )
        val track2 = ReleaseTrackEntity(
            id = "trk_chaabi_002",
            releaseId = release2Id,
            trackNumber = 1,
            title = "CHAABI REVOLUTION",
            version = "Radio Edit",
            isrc = "MA-A01-26-00102",
            primaryArtist = "OudWave",
            featuringArtists = "Cheikha Zahra",
            composer = "Youssef Bennani, Zahra Al Maghribia",
            lyricist = "Traditional, Youssef Bennani",
            producer = "K-Pulse Casablanca",
            isExplicit = false,
            audioFormat = AudioFormat.FLAC,
            durationSeconds = 228
        )
        val coverAsset2 = CoverAssetEntity(
            id = "cov_002",
            releaseId = release2Id,
            bucketName = "cover_art",
            filePath = "cover_art/$release2Id/artwork.jpg",
            resolutionWidth = 3000,
            resolutionHeight = 3000,
            aspectRatio = "1:1",
            fileSizeBytes = 5120000L,
            isValidated = true
        )
        val rights2 = RightsDeclarationEntity(
            id = "rgt_002",
            releaseId = release2Id,
            userId = defaultUserId,
            legalName = "Youssef Bennani",
            ipAddress = "105.158.12.84",
            isConfirmed = true
        )
        createRelease(release2, listOf(track2), coverAsset2, rights2)

        // 6. Release 3: "GNAWA ROOTS: ESSAOUIRA" (EP - 3 tracks, APPROVED in Distribution Queue)
        val release3Id = "rel_gnawa_ep_003"
        val release3 = ReleaseEntity(
            id = release3Id,
            artistId = defaultArtistId,
            title = "GNAWA ROOTS: ESSAOUIRA",
            releaseType = ReleaseType.EP,
            status = ReleaseStatus.APPROVED,
            primaryArtist = "OudWave",
            featuringArtists = "Maâlem Hamid",
            version = "Remastered Studio",
            genre = MusicGenre.GNAWA,
            subgenre = "Spiritual Fusion",
            language = "Moroccan Arabic (Darija)",
            originalReleaseDate = "2026-09-10",
            distributionReleaseDate = "2026-09-28",
            copyrightOwner = "OudWave & Maâlem Hamid",
            pLineYear = 2026,
            cLineYear = 2026,
            labelName = "Sawt Independent",
            territory = Territory.WORLDWIDE,
            isExplicit = false,
            upcEan = "840555666777",
            spotifyStatus = DspStatus.PROCESSING_DDEX,
            youtubeStatus = DspStatus.PROCESSING_DDEX,
            createdAt = System.currentTimeMillis() - (5 * 86400000L),
            updatedAt = System.currentTimeMillis() - (2 * 86400000L)
        )
        val tracks3 = listOf(
            ReleaseTrackEntity(
                id = "trk_gnw_01",
                releaseId = release3Id,
                trackNumber = 1,
                title = "Lalla Mira",
                version = "Studio",
                isrc = "MA-A01-26-00103",
                primaryArtist = "OudWave",
                featuringArtists = "Maâlem Hamid",
                composer = "Maâlem Hamid, Youssef Bennani",
                lyricist = "Traditional Gnawa Heritage",
                producer = "OudWave",
                isExplicit = false,
                audioFormat = AudioFormat.WAV,
                durationSeconds = 312
            ),
            ReleaseTrackEntity(
                id = "trk_gnw_02",
                releaseId = release3Id,
                trackNumber = 2,
                title = "Sidi Mimoun (Oud Solo)",
                version = "Acoustic",
                isrc = "MA-A01-26-00104",
                primaryArtist = "OudWave",
                composer = "Youssef Bennani",
                lyricist = "Instrumental",
                producer = "OudWave",
                isExplicit = false,
                audioFormat = AudioFormat.WAV,
                durationSeconds = 245
            ),
            ReleaseTrackEntity(
                id = "trk_gnw_03",
                releaseId = release3Id,
                trackNumber = 3,
                title = "Essaouira Sunset",
                version = "Ambient Fusion",
                isrc = "MA-A01-26-00105",
                primaryArtist = "OudWave",
                composer = "Youssef Bennani",
                lyricist = "Instrumental",
                producer = "OudWave",
                isExplicit = false,
                audioFormat = AudioFormat.FLAC,
                durationSeconds = 270
            )
        )
        val coverAsset3 = CoverAssetEntity(
            id = "cov_003",
            releaseId = release3Id,
            bucketName = "cover_art",
            filePath = "cover_art/$release3Id/artwork.jpg",
            resolutionWidth = 3000,
            resolutionHeight = 3000,
            aspectRatio = "1:1",
            fileSizeBytes = 4920000L,
            isValidated = true
        )
        val rights3 = RightsDeclarationEntity(
            id = "rgt_003",
            releaseId = release3Id,
            userId = defaultUserId,
            legalName = "Youssef Bennani",
            ipAddress = "105.158.12.84",
            isConfirmed = true
        )
        createRelease(release3, tracks3, coverAsset3, rights3)

        // 7. Seed Real Royalty Reports
        royaltyDao.insertReport(
            RoyaltyReportEntity(
                artistId = defaultArtistId,
                releaseId = release1Id,
                releaseTitle = "NO SIGNAL",
                platform = DspPlatform.SPOTIFY,
                period = "2026-08",
                streams = 184200L,
                grossRevenue = 15350.00,
                fees = 2456.00,
                netRevenue = 12894.00,
                currency = "MAD",
                reportSource = "FUGA / Authorized Ingestion Real Statement"
            )
        )
        royaltyDao.insertReport(
            RoyaltyReportEntity(
                artistId = defaultArtistId,
                releaseId = release1Id,
                releaseTitle = "NO SIGNAL",
                platform = DspPlatform.YOUTUBE_MUSIC,
                period = "2026-08",
                streams = 96400L,
                grossRevenue = 6885.00,
                fees = 1101.00,
                netRevenue = 5784.00,
                currency = "MAD",
                reportSource = "YouTube Partner Direct Royalty Feed"
            )
        )

        // 8. Seed Sample Completed Payout
        royaltyDao.insertPayout(
            PayoutEntity(
                id = "pay_001",
                artistId = defaultArtistId,
                artistName = "Youssef Bennani",
                amount = 35200.00,
                currency = "MAD",
                status = PayoutStatus.COMPLETED,
                paymentMethod = PayoutMethod.CIH_BANK,
                accountDetails = "CIH Bank Morocco - RIB: 230 780 1234567890123456 78",
                requestedAt = System.currentTimeMillis() - (45 * 86400000L),
                processedAt = System.currentTimeMillis() - (42 * 86400000L),
                auditNotes = "Wire confirmed by Casablanca Treasury Operations"
            )
        )

        // 9. Seed Distribution Job for "NO SIGNAL"
        val job1 = DistributionJobEntity(
            id = "job_spot_001",
            releaseId = release1Id,
            platform = DspPlatform.SPOTIFY,
            status = "DELIVERED",
            attempts = 1,
            maxAttempts = 3,
            externalReleaseId = "spot_batch_99210",
            submittedAt = System.currentTimeMillis() - (26 * 86400000L),
            deliveredAt = System.currentTimeMillis() - (25 * 86400000L),
            payloadSummary = "Ingestion manifest verified. Lossless WAV 24-bit audio verified. Live on DSP."
        )
        distributionDao.insertJob(job1)

        val job2 = DistributionJobEntity(
            id = "job_yt_002",
            releaseId = release1Id,
            platform = DspPlatform.YOUTUBE_MUSIC,
            status = "DELIVERED",
            attempts = 1,
            maxAttempts = 3,
            externalReleaseId = "yt_ddex_batch_77192",
            submittedAt = System.currentTimeMillis() - (26 * 86400000L),
            deliveredAt = System.currentTimeMillis() - (25 * 86400000L),
            payloadSummary = "DDEX ERN 4.2 generated. Art Track created: youtube_art_track_381920."
        )
        distributionDao.insertJob(job2)
    }
}
