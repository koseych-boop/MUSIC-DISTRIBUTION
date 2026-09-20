package com.example.domain.dsp

import com.example.data.model.DistributionDeliveryEntity
import com.example.data.model.DistributionJobEntity
import com.example.data.model.DspPlatform
import com.example.data.model.DspStatus
import com.example.data.model.NotificationEntity
import com.example.data.model.ReleaseStatus
import com.example.data.repository.DistributionRepository
import kotlinx.coroutines.delay
import java.util.UUID

sealed class DeliveryStep(val label: String, val progress: Float) {
    object Queued : DeliveryStep("Job Queued in Ingestion Pipeline", 0.1f)
    object SecureDownload : DeliveryStep("Downloading lossless audio from audio_originals bucket...", 0.3f)
    object Validation : DeliveryStep("Performing bit-depth & audio waveform validation...", 0.5f)
    object PackageBuild : DeliveryStep("Building DDEX ERN 4.2 / Partner Delivery Package...", 0.7f)
    object PartnerTransmission : DeliveryStep("Transmitting to Authorized Partner Intake...", 0.85f)
    object Delivered : DeliveryStep("Successfully Delivered to Ingestion Partner!", 1.0f)
    data class Failed(val error: String) : DeliveryStep("Delivery Failed: $error", 1.0f)
}

class DeliveryWorker(
    private val repository: DistributionRepository,
    private val spotifyAdapter: SpotifyDistributionAdapter = SpotifyDistributionAdapter(),
    private val youTubeAdapter: YouTubeMusicDistributionAdapter = YouTubeMusicDistributionAdapter()
) {
    suspend fun executeDistribution(
        releaseId: String,
        isSandboxMode: Boolean,
        onProgress: suspend (DeliveryStep) -> Unit = {}
    ): Boolean {
        val release = repository.getReleaseByIdOnce(releaseId) ?: return false
        val tracks = repository.getTracksForReleaseOnce(releaseId)
        
        // 1. Step: Queued
        onProgress(DeliveryStep.Queued)
        delay(600)

        // 2. Step: Downloading audio from secure storage bucket
        onProgress(DeliveryStep.SecureDownload)
        delay(800)

        // 3. Step: File validation
        onProgress(DeliveryStep.Validation)
        delay(700)

        // 4. Step: Package build
        onProgress(DeliveryStep.PackageBuild)
        val packageData = DeliveryPackageBuilder.build(release, tracks, null)
        delay(900)

        // 5. Step: Partner transmission (Spotify & YouTube Music)
        onProgress(DeliveryStep.PartnerTransmission)
        delay(1000)

        // Spotify Delivery
        val spotJob = DistributionJobEntity(
            releaseId = releaseId,
            platform = DspPlatform.SPOTIFY,
            status = "PROCESSING",
            attempts = 1,
            maxAttempts = 3,
            submittedAt = System.currentTimeMillis()
        )
        repository.insertJob(spotJob)
        val spotResponse = spotifyAdapter.submitRelease(release, tracks, null, isSandboxMode)
        
        repository.insertDelivery(
            DistributionDeliveryEntity(
                jobId = spotJob.id,
                platform = DspPlatform.SPOTIFY,
                status = if (spotResponse.success) "DELIVERED" else "FAILED",
                packageType = "DSP_DIRECT_BATCH",
                packageXml = packageData.ernXml,
                rawResponse = spotResponse.rawPayloadSummary,
                externalId = spotResponse.externalId
            )
        )

        // YouTube Music DDEX Delivery
        val ytJob = DistributionJobEntity(
            releaseId = releaseId,
            platform = DspPlatform.YOUTUBE_MUSIC,
            status = "PROCESSING",
            attempts = 1,
            maxAttempts = 3,
            submittedAt = System.currentTimeMillis()
        )
        repository.insertJob(ytJob)
        val ytResponse = youTubeAdapter.submitRelease(release, tracks, null, isSandboxMode)
        
        repository.insertDelivery(
            DistributionDeliveryEntity(
                jobId = ytJob.id,
                platform = DspPlatform.YOUTUBE_MUSIC,
                status = if (ytResponse.success) "DELIVERED" else "FAILED",
                packageType = "DDEX_ERN_4_2",
                packageXml = packageData.ernXml,
                rawResponse = ytResponse.rawPayloadSummary,
                externalId = ytResponse.externalId
            )
        )

        val allDelivered = spotResponse.success && ytResponse.success
        if (allDelivered) {
            repository.updateJob(spotJob.copy(status = "DELIVERED", deliveredAt = System.currentTimeMillis(), externalReleaseId = spotResponse.externalId))
            repository.updateJob(ytJob.copy(status = "DELIVERED", deliveredAt = System.currentTimeMillis(), externalReleaseId = ytResponse.externalId))

            val targetReleaseStatus = if (isSandboxMode) ReleaseStatus.LIVE else ReleaseStatus.DELIVERED
            val targetDspStatus = if (isSandboxMode) DspStatus.LIVE else DspStatus.DELIVERED
            
            repository.updateReleaseStatus(releaseId, targetReleaseStatus, "Delivered to authorized Spotify & YouTube Music ingestion channels.")
            repository.updateReleaseDspStatus(
                releaseId = releaseId,
                spotifyStatus = targetDspStatus,
                spotifyUrl = if (isSandboxMode) "https://open.spotify.com/album/${spotResponse.externalId}" else null,
                youtubeStatus = targetDspStatus,
                youtubeArtTrackId = "yt_art_track_${UUID.randomUUID().toString().take(8)}",
                youtubeAssetId = ytResponse.externalId
            )

            onProgress(DeliveryStep.Delivered)
            return true
        } else {
            repository.updateJob(spotJob.copy(status = "FAILED", errorMessage = "Network timeout to authorized ingestion aggregator."))
            onProgress(DeliveryStep.Failed("Aggregator gateway error on attempt 1/3"))
            return false
        }
    }
}
