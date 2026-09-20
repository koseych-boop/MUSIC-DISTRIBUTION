package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ArtistBalanceEntity
import com.example.data.model.ArtistProfileEntity
import com.example.data.model.AudioAssetEntity
import com.example.data.model.AuditLogEntity
import com.example.data.model.CoverAssetEntity
import com.example.data.model.DistributionDeliveryEntity
import com.example.data.model.DistributionJobEntity
import com.example.data.model.DistributionWebhookEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.PayoutEntity
import com.example.data.model.ReleaseEntity
import com.example.data.model.ReleaseTrackEntity
import com.example.data.model.RightsDeclarationEntity
import com.example.data.model.RoyaltyReportEntity
import com.example.data.model.TakedownRequestEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReleaseDao {
    @Query("SELECT * FROM releases ORDER BY createdAt DESC")
    fun getAllReleases(): Flow<List<ReleaseEntity>>

    @Query("SELECT * FROM releases WHERE artistId = :artistId ORDER BY createdAt DESC")
    fun getReleasesByArtist(artistId: String): Flow<List<ReleaseEntity>>

    @Query("SELECT * FROM releases WHERE id = :id")
    fun getReleaseById(id: String): Flow<ReleaseEntity?>

    @Query("SELECT * FROM releases WHERE id = :id")
    suspend fun getReleaseByIdOnce(id: String): ReleaseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRelease(release: ReleaseEntity)

    @Update
    suspend fun updateRelease(release: ReleaseEntity)

    @Query("SELECT * FROM release_tracks WHERE releaseId = :releaseId ORDER BY trackNumber ASC")
    fun getTracksForRelease(releaseId: String): Flow<List<ReleaseTrackEntity>>

    @Query("SELECT * FROM release_tracks WHERE releaseId = :releaseId ORDER BY trackNumber ASC")
    suspend fun getTracksForReleaseOnce(releaseId: String): List<ReleaseTrackEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: ReleaseTrackEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(tracks: List<ReleaseTrackEntity>)

    @Query("DELETE FROM release_tracks WHERE id = :trackId")
    suspend fun deleteTrackById(trackId: String)

    @Query("SELECT * FROM audio_assets WHERE trackId = :trackId")
    fun getAudioAssetsForTrack(trackId: String): Flow<List<AudioAssetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAudioAsset(asset: AudioAssetEntity)

    @Query("SELECT * FROM cover_assets WHERE releaseId = :releaseId LIMIT 1")
    fun getCoverAssetForRelease(releaseId: String): Flow<CoverAssetEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoverAsset(asset: CoverAssetEntity)

    @Query("SELECT * FROM rights_declarations WHERE releaseId = :releaseId LIMIT 1")
    fun getRightsDeclaration(releaseId: String): Flow<RightsDeclarationEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRightsDeclaration(declaration: RightsDeclarationEntity)
}

@Dao
interface DistributionDao {
    @Query("SELECT * FROM distribution_jobs ORDER BY submittedAt DESC")
    fun getAllDistributionJobs(): Flow<List<DistributionJobEntity>>

    @Query("SELECT * FROM distribution_jobs WHERE releaseId = :releaseId ORDER BY submittedAt DESC")
    fun getJobsForRelease(releaseId: String): Flow<List<DistributionJobEntity>>

    @Query("SELECT * FROM distribution_jobs WHERE id = :jobId")
    suspend fun getJobByIdOnce(jobId: String): DistributionJobEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJob(job: DistributionJobEntity)

    @Update
    suspend fun updateJob(job: DistributionJobEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDelivery(delivery: DistributionDeliveryEntity)

    @Query("SELECT * FROM distribution_deliveries WHERE jobId = :jobId ORDER BY createdAt DESC")
    fun getDeliveriesForJob(jobId: String): Flow<List<DistributionDeliveryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWebhook(webhook: DistributionWebhookEntity)

    @Query("SELECT * FROM distribution_webhooks ORDER BY processedAt DESC")
    fun getAllWebhooks(): Flow<List<DistributionWebhookEntity>>
}

@Dao
interface RoyaltyDao {
    @Query("SELECT * FROM royalty_reports WHERE artistId = :artistId ORDER BY importedAt DESC")
    fun getReportsForArtist(artistId: String): Flow<List<RoyaltyReportEntity>>

    @Query("SELECT * FROM royalty_reports ORDER BY importedAt DESC")
    fun getAllReports(): Flow<List<RoyaltyReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: RoyaltyReportEntity)

    @Query("SELECT * FROM artist_balances WHERE artistId = :artistId LIMIT 1")
    fun getArtistBalance(artistId: String): Flow<ArtistBalanceEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertArtistBalance(balance: ArtistBalanceEntity)

    @Query("SELECT * FROM payouts WHERE artistId = :artistId ORDER BY requestedAt DESC")
    fun getPayoutsForArtist(artistId: String): Flow<List<PayoutEntity>>

    @Query("SELECT * FROM payouts ORDER BY requestedAt DESC")
    fun getAllPayouts(): Flow<List<PayoutEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayout(payout: PayoutEntity)

    @Update
    suspend fun updatePayout(payout: PayoutEntity)
}

@Dao
interface AuditDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLogEntity)

    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAllAuditLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTakedownRequest(request: TakedownRequestEntity)

    @Query("SELECT * FROM takedown_requests ORDER BY requestedAt DESC")
    fun getAllTakedownRequests(): Flow<List<TakedownRequestEntity>>

    @Update
    suspend fun updateTakedownRequest(request: TakedownRequestEntity)

    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY createdAt DESC")
    fun getNotifications(userId: String): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("SELECT * FROM artist_profiles WHERE userId = :userId LIMIT 1")
    fun getArtistProfile(userId: String): Flow<ArtistProfileEntity?>

    @Query("SELECT * FROM artist_profiles WHERE userId = :userId LIMIT 1")
    suspend fun getArtistProfileOnce(userId: String): ArtistProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtistProfile(profile: ArtistProfileEntity)

    @Update
    suspend fun updateArtistProfile(profile: ArtistProfileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserEntity?
}
