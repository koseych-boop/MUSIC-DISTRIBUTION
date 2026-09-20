package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
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

@Database(
    entities = [
        UserEntity::class,
        ArtistProfileEntity::class,
        ReleaseEntity::class,
        ReleaseTrackEntity::class,
        AudioAssetEntity::class,
        CoverAssetEntity::class,
        RightsDeclarationEntity::class,
        DistributionJobEntity::class,
        DistributionDeliveryEntity::class,
        DistributionWebhookEntity::class,
        RoyaltyReportEntity::class,
        ArtistBalanceEntity::class,
        PayoutEntity::class,
        TakedownRequestEntity::class,
        AuditLogEntity::class,
        NotificationEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun releaseDao(): ReleaseDao
    abstract fun distributionDao(): DistributionDao
    abstract fun royaltyDao(): RoyaltyDao
    abstract fun auditDao(): AuditDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sawt_distribution_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
