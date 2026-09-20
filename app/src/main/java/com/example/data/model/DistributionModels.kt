package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.util.UUID

enum class UserRole {
    ARTIST,
    ADMIN
}

enum class ReleaseType {
    SINGLE,
    EP,
    ALBUM
}

enum class ReleaseStatus {
    DRAFT,
    VALIDATING,
    PENDING_REVIEW,
    APPROVED,
    REJECTED,
    QUEUED,
    SUBMITTED,
    DELIVERED,
    LIVE,
    FAILED,
    TAKEDOWN
}

enum class DspPlatform {
    SPOTIFY,
    YOUTUBE_MUSIC,
    APPLE_MUSIC,
    DEEZER,
    AMAZON_MUSIC,
    TIDAL
}

enum class DspStatus {
    NOT_SUBMITTED,
    QUEUED,
    VALIDATING,
    SUBMITTED,
    PROCESSING_DDEX,
    DELIVERED,
    LIVE,
    FAILED,
    TAKEDOWN_REQUESTED,
    REMOVED
}

enum class MoroccanCity(val displayName: String, val arabicName: String) {
    CASABLANCA("Casablanca", "الدار البيضاء"),
    RABAT("Rabat", "الرباط"),
    MARRAKECH("Marrakech", "مراكش"),
    TANGIER("Tangier", "طنجة"),
    FES("Fes", "فاس"),
    AGADIR("Agadir", "أكادير"),
    OUJDA("Oujda", "وجدة"),
    TETOUAN("Tetouan", "تطوان"),
    ESSAOUIRA("Essaouira", "الصويرة"),
    MEKNES("Meknes", "مكناس"),
    NADOR("Nador", "الناظور"),
    SAFI("Safi", "آسفي"),
    EL_JADIDA("El Jadida", "الجديدة"),
    LAAYOUNE("Laâyoune", "العيون")
}

enum class MusicGenre(val title: String) {
    MOROCCAN_RAP("Moroccan Rap / Trap"),
    CHAABI("Chaabi Marocain"),
    GNAWA("Gnawa / Fusion"),
    AMAZIGH("Amazigh / Atlas / Souss / Rif"),
    RAI("Raï Marocain"),
    ANDALUSIAN("Andalusian Classical"),
    POP_DARIJA("Pop Darija"),
    SAHRAWI("Sahrawi / Hassani"),
    ELECTRO_MOROCCAN("Moroccan Electro Fusion")
}

enum class AudioFormat {
    WAV,
    FLAC
}

enum class Territory(val label: String) {
    WORLDWIDE("Worldwide (All Territories)"),
    MENA("MENA (Middle East & North Africa)"),
    MOROCCO_ONLY("Morocco Only (MA)"),
    CUSTOM("Custom Territory Selection")
}

enum class PayoutStatus {
    REQUESTED,
    PENDING_REVIEW,
    APPROVED,
    COMPLETED,
    REJECTED
}

enum class PayoutMethod(val title: String) {
    CIH_BANK("CIH Bank (Morocco)"),
    ATTIJARIWAFA("Attijariwafa Bank"),
    BMCE_BANK("Bank of Africa (BMCE)"),
    BANQUE_POPULAIRE("Banque Populaire (Chaabi)"),
    PAYPAL("PayPal International")
}

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val email: String,
    val username: String,
    val role: UserRole = UserRole.ARTIST,
    val isEmailVerified: Boolean = true,
    val sessionToken: String = UUID.randomUUID().toString(),
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "artist_profiles")
data class ArtistProfileEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val artistName: String,
    val stageName: String,
    val bio: String,
    val country: String = "Morocco",
    val city: MoroccanCity = MoroccanCity.CASABLANCA,
    val genre: MusicGenre = MusicGenre.MOROCCAN_RAP,
    val profilePhotoUri: String? = null,
    val spotifyArtistUrl: String = "",
    val youtubeChannelUrl: String = "",
    val isVerified: Boolean = true
)

@Entity(tableName = "releases")
data class ReleaseEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val artistId: String,
    val title: String,
    val releaseType: ReleaseType = ReleaseType.SINGLE,
    val status: ReleaseStatus = ReleaseStatus.DRAFT,
    val primaryArtist: String,
    val featuringArtists: String = "",
    val version: String = "Original",
    val genre: MusicGenre = MusicGenre.MOROCCAN_RAP,
    val subgenre: String = "Trap / Darija",
    val language: String = "Moroccan Arabic (Darija)",
    val originalReleaseDate: String = "2026-09-20",
    val distributionReleaseDate: String = "2026-10-01",
    val copyrightOwner: String,
    val pLineYear: Int = 2026,
    val cLineYear: Int = 2026,
    val labelName: String = "Independent (Morocco)",
    val territory: Territory = Territory.WORLDWIDE,
    val isExplicit: Boolean = false,
    val upcEan: String = "",
    val coverImageUri: String? = null,
    
    // DSP statuses
    val spotifyStatus: DspStatus = DspStatus.NOT_SUBMITTED,
    val spotifyReleaseId: String? = null,
    val spotifyArtistId: String? = null,
    val spotifyUrl: String? = null,
    val spotifyDeliveryDate: Long? = null,
    val spotifyError: String? = null,
    
    val youtubeStatus: DspStatus = DspStatus.NOT_SUBMITTED,
    val youtubeAssetId: String? = null,
    val youtubeArtTrackId: String? = null,
    val youtubeError: String? = null,
    
    val adminNotes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "release_tracks")
data class ReleaseTrackEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val releaseId: String,
    val trackNumber: Int = 1,
    val discNumber: Int = 1,
    val title: String,
    val version: String = "Original",
    val isrc: String,
    val primaryArtist: String,
    val featuringArtists: String = "",
    val composer: String,
    val lyricist: String,
    val producer: String,
    val isExplicit: Boolean = false,
    val audioFileUri: String? = null,
    val audioFormat: AudioFormat = AudioFormat.WAV,
    val durationSeconds: Int = 210,
    val previewStartSeconds: Int = 30
)

@Entity(tableName = "audio_assets")
data class AudioAssetEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val trackId: String,
    val bucketName: String = "audio_originals", // audio_originals, audio_processed
    val filePath: String,
    val fileFormat: AudioFormat = AudioFormat.WAV,
    val sampleRateHz: Int = 48000,
    val bitDepth: Int = 24,
    val fileSizeBytes: Long = 48234500L,
    val signedUrlToken: String = UUID.randomUUID().toString(),
    val isValidated: Boolean = true
)

@Entity(tableName = "cover_assets")
data class CoverAssetEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val releaseId: String,
    val bucketName: String = "cover_art",
    val filePath: String,
    val resolutionWidth: Int = 3000,
    val resolutionHeight: Int = 3000,
    val aspectRatio: String = "1:1",
    val fileSizeBytes: Long = 4210000L,
    val colorSpace: String = "sRGB",
    val isValidated: Boolean = true
)

@Entity(tableName = "rights_declarations")
data class RightsDeclarationEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val releaseId: String,
    val userId: String,
    val legalName: String,
    val declarationText: String = "I confirm that I own or control the rights necessary to distribute this recording and artwork.",
    val isConfirmed: Boolean = true,
    val confirmedAt: Long = System.currentTimeMillis(),
    val ipAddress: String = "105.158.12.84", // Moroccan ISP IP format
    val auditHash: String = UUID.randomUUID().toString().replace("-", "")
)

@Entity(tableName = "distribution_jobs")
data class DistributionJobEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val releaseId: String,
    val platform: DspPlatform,
    val status: String = "QUEUED", // QUEUED, PROCESSING, DELIVERED, FAILED, RETRYING
    val attempts: Int = 1,
    val maxAttempts: Int = 3,
    val externalReleaseId: String? = null,
    val submittedAt: Long = System.currentTimeMillis(),
    val deliveredAt: Long? = null,
    val errorMessage: String? = null,
    val payloadSummary: String = ""
)

@Entity(tableName = "distribution_deliveries")
data class DistributionDeliveryEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val jobId: String,
    val platform: DspPlatform,
    val status: String,
    val packageType: String = "DDEX_ERN_4_2",
    val packageXml: String,
    val rawResponse: String? = null,
    val externalId: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "distribution_webhooks")
data class DistributionWebhookEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val platform: DspPlatform,
    val eventType: String,
    val payloadJson: String,
    val signatureHeader: String,
    val isVerified: Boolean = true,
    val processedAt: Long = System.currentTimeMillis(),
    val status: String = "PROCESSED"
)

@Entity(tableName = "royalty_reports")
data class RoyaltyReportEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val artistId: String,
    val releaseId: String,
    val releaseTitle: String,
    val platform: DspPlatform,
    val period: String, // e.g. "2026-08"
    val streams: Long,
    val grossRevenue: Double,
    val fees: Double,
    val netRevenue: Double,
    val currency: String = "MAD",
    val reportSource: String = "FUGA / Authorized Aggregator Real Report",
    val importedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "artist_balances")
data class ArtistBalanceEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val artistId: String,
    val pendingBalance: Double = 0.0,
    val availableBalance: Double = 0.0,
    val paidBalance: Double = 0.0,
    val currency: String = "MAD"
)

@Entity(tableName = "payouts")
data class PayoutEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val artistId: String,
    val artistName: String,
    val amount: Double,
    val currency: String = "MAD",
    val status: PayoutStatus = PayoutStatus.REQUESTED,
    val paymentMethod: PayoutMethod = PayoutMethod.CIH_BANK,
    val accountDetails: String,
    val requestedAt: Long = System.currentTimeMillis(),
    val processedAt: Long? = null,
    val auditNotes: String? = null
)

@Entity(tableName = "takedown_requests")
data class TakedownRequestEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val releaseId: String,
    val releaseTitle: String,
    val artistId: String,
    val platform: String = "ALL", // ALL, SPOTIFY, YOUTUBE_MUSIC
    val reason: String,
    val status: String = "REQUESTED", // REQUESTED, SENT_TO_PARTNER, COMPLETED, REJECTED
    val requestedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val actorId: String,
    val actorRole: UserRole,
    val action: String,
    val targetType: String,
    val targetId: String,
    val details: String,
    val ipAddress: String = "105.158.12.84",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val title: String,
    val message: String,
    val type: String = "RELEASE_STATUS",
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
