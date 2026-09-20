package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.ArtistBalanceEntity
import com.example.data.model.ArtistProfileEntity
import com.example.data.model.AuditLogEntity
import com.example.data.model.CoverAssetEntity
import com.example.data.model.DistributionDeliveryEntity
import com.example.data.model.DistributionJobEntity
import com.example.data.model.DistributionWebhookEntity
import com.example.data.model.DspPlatform
import com.example.data.model.DspStatus
import com.example.data.model.PayoutEntity
import com.example.data.model.PayoutMethod
import com.example.data.model.ReleaseEntity
import com.example.data.model.ReleaseStatus
import com.example.data.model.ReleaseTrackEntity
import com.example.data.model.RightsDeclarationEntity
import com.example.data.model.RoyaltyReportEntity
import com.example.data.model.TakedownRequestEntity
import com.example.data.model.UserRole
import com.example.data.repository.DistributionRepository
import com.example.domain.dsp.DeliveryWorker
import com.example.domain.dsp.WebhookVerifier
import com.example.ui.localization.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class ArtistScreen {
    DASHBOARD,
    CREATE_RELEASE,
    DETAIL,
    ROYALTIES,
    PROFILE
}

class DistributionViewModel(application: Application) : AndroidViewModel(application) {
    val repository = DistributionRepository(application)
    val deliveryWorker = DeliveryWorker(repository)

    // User & Platform State
    private val _currentRole = MutableStateFlow(UserRole.ARTIST)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    private val _currentLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    private val _isSandboxMode = MutableStateFlow(true)
    val isSandboxMode: StateFlow<Boolean> = _isSandboxMode.asStateFlow()

    private val _currentScreen = MutableStateFlow(ArtistScreen.DASHBOARD)
    val currentScreen: StateFlow<ArtistScreen> = _currentScreen.asStateFlow()

    private val _selectedRelease = MutableStateFlow<ReleaseEntity?>(null)
    val selectedRelease: StateFlow<ReleaseEntity?> = _selectedRelease.asStateFlow()

    // Data streams from Room
    val releases: StateFlow<List<ReleaseEntity>> = repository.allReleases.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val artistProfile: StateFlow<ArtistProfileEntity?> = repository.getArtistProfile("artist_morocco_001").stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    val artistBalance: StateFlow<ArtistBalanceEntity?> = repository.getArtistBalance("profile_morocco_001").stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    val royaltyReports: StateFlow<List<RoyaltyReportEntity>> = repository.getRoyaltyReports("profile_morocco_001").stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val payouts: StateFlow<List<PayoutEntity>> = repository.getPayouts("profile_morocco_001").stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val allPayouts: StateFlow<List<PayoutEntity>> = repository.allPayouts.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val allJobs: StateFlow<List<DistributionJobEntity>> = repository.allJobs.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val allWebhooks: StateFlow<List<DistributionWebhookEntity>> = repository.allWebhooks.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val allTakedowns: StateFlow<List<TakedownRequestEntity>> = repository.allTakedownRequests.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val allAuditLogs: StateFlow<List<AuditLogEntity>> = repository.allAuditLogs.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Navigation & Role Controls
    fun setRole(role: UserRole) {
        _currentRole.value = role
    }

    fun setLanguage(language: AppLanguage) {
        _currentLanguage.value = language
    }

    fun toggleSandbox() {
        _isSandboxMode.value = !_isSandboxMode.value
    }

    fun navigateTo(screen: ArtistScreen) {
        _currentScreen.value = screen
    }

    fun selectRelease(release: ReleaseEntity) {
        _selectedRelease.value = release
        _currentScreen.value = ArtistScreen.DETAIL
    }

    // Artist Actions
    fun createRelease(
        release: ReleaseEntity,
        tracks: List<ReleaseTrackEntity>,
        coverAsset: CoverAssetEntity,
        rightsDeclaration: RightsDeclarationEntity
    ) {
        viewModelScope.launch {
            repository.createRelease(release, tracks, coverAsset, rightsDeclaration)
            _selectedRelease.value = release
            _currentScreen.value = ArtistScreen.DETAIL
        }
    }

    fun requestPayout(amount: Double, method: PayoutMethod, accountDetails: String) {
        viewModelScope.launch {
            repository.requestPayout(
                artistId = "profile_morocco_001",
                artistName = artistProfile.value?.stageName ?: "OudWave",
                amount = amount,
                method = method,
                accountDetails = accountDetails
            )
        }
    }

    fun requestTakedown(releaseId: String, platform: String, reason: String) {
        viewModelScope.launch {
            val title = releases.value.find { it.id == releaseId }?.title ?: "Release"
            repository.submitTakedownRequest(
                releaseId = releaseId,
                releaseTitle = title,
                artistId = "profile_morocco_001",
                platform = platform,
                reason = reason
            )
        }
    }

    fun updateProfile(profile: ArtistProfileEntity) {
        viewModelScope.launch {
            repository.updateArtistProfile(profile)
        }
    }

    // Admin Actions
    fun approveRelease(releaseId: String) {
        viewModelScope.launch {
            repository.updateReleaseStatus(
                releaseId = releaseId,
                newStatus = ReleaseStatus.APPROVED,
                adminNotes = "Compliance check passed. Cleared for DSP distribution queue."
            )
        }
    }

    fun rejectRelease(releaseId: String, reason: String) {
        viewModelScope.launch {
            repository.updateReleaseStatus(
                releaseId = releaseId,
                newStatus = ReleaseStatus.REJECTED,
                adminNotes = reason
            )
        }
    }

    fun processPayout(payoutId: String, approve: Boolean, notes: String?) {
        viewModelScope.launch {
            repository.processPayout(payoutId, approve, notes)
        }
    }

    fun processTakedown(requestId: String, approve: Boolean) {
        viewModelScope.launch {
            repository.processTakedown(requestId, approve)
        }
    }

    fun simulatePartnerWebhook(releaseId: String, platformName: String, eventType: String) {
        viewModelScope.launch {
            val payload = """
                {"event": "$eventType", "release_id": "$releaseId", "partner": "$platformName", "timestamp": ${System.currentTimeMillis()}}
            """.trimIndent()
            val secretKey = "sawt_live_partner_secret_key"
            val hmac = WebhookVerifier.computeHmacSha256(payload, secretKey)
            val isValid = WebhookVerifier.verifySignature(payload, secretKey, "sha256=$hmac")

            val webhook = DistributionWebhookEntity(
                platform = if (platformName == "SPOTIFY") DspPlatform.SPOTIFY else DspPlatform.YOUTUBE_MUSIC,
                eventType = eventType,
                payloadJson = payload,
                signatureHeader = "sha256=$hmac",
                isVerified = isValid
            )
            repository.insertWebhook(webhook)

            if (isValid) {
                if (eventType == "RELEASE_LIVE") {
                    repository.updateReleaseDspStatus(
                        releaseId = releaseId,
                        spotifyStatus = DspStatus.LIVE,
                        youtubeStatus = DspStatus.LIVE
                    )
                    repository.updateReleaseStatus(releaseId, ReleaseStatus.LIVE, "Confirmed live via partner webhook.")
                }
            }
        }
    }
}
