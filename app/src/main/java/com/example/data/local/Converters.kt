package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.AudioFormat
import com.example.data.model.DspPlatform
import com.example.data.model.DspStatus
import com.example.data.model.MoroccanCity
import com.example.data.model.MusicGenre
import com.example.data.model.PayoutMethod
import com.example.data.model.PayoutStatus
import com.example.data.model.ReleaseStatus
import com.example.data.model.ReleaseType
import com.example.data.model.Territory
import com.example.data.model.UserRole

class Converters {
    @TypeConverter
    fun fromUserRole(value: UserRole): String = value.name
    @TypeConverter
    fun toUserRole(value: String): UserRole = try { UserRole.valueOf(value) } catch (e: Exception) { UserRole.ARTIST }

    @TypeConverter
    fun fromReleaseType(value: ReleaseType): String = value.name
    @TypeConverter
    fun toReleaseType(value: String): ReleaseType = try { ReleaseType.valueOf(value) } catch (e: Exception) { ReleaseType.SINGLE }

    @TypeConverter
    fun fromReleaseStatus(value: ReleaseStatus): String = value.name
    @TypeConverter
    fun toReleaseStatus(value: String): ReleaseStatus = try { ReleaseStatus.valueOf(value) } catch (e: Exception) { ReleaseStatus.DRAFT }

    @TypeConverter
    fun fromDspPlatform(value: DspPlatform): String = value.name
    @TypeConverter
    fun toDspPlatform(value: String): DspPlatform = try { DspPlatform.valueOf(value) } catch (e: Exception) { DspPlatform.SPOTIFY }

    @TypeConverter
    fun fromDspStatus(value: DspStatus): String = value.name
    @TypeConverter
    fun toDspStatus(value: String): DspStatus = try { DspStatus.valueOf(value) } catch (e: Exception) { DspStatus.NOT_SUBMITTED }

    @TypeConverter
    fun fromMoroccanCity(value: MoroccanCity): String = value.name
    @TypeConverter
    fun toMoroccanCity(value: String): MoroccanCity = try { MoroccanCity.valueOf(value) } catch (e: Exception) { MoroccanCity.CASABLANCA }

    @TypeConverter
    fun fromMusicGenre(value: MusicGenre): String = value.name
    @TypeConverter
    fun toMusicGenre(value: String): MusicGenre = try { MusicGenre.valueOf(value) } catch (e: Exception) { MusicGenre.MOROCCAN_RAP }

    @TypeConverter
    fun fromAudioFormat(value: AudioFormat): String = value.name
    @TypeConverter
    fun toAudioFormat(value: String): AudioFormat = try { AudioFormat.valueOf(value) } catch (e: Exception) { AudioFormat.WAV }

    @TypeConverter
    fun fromTerritory(value: Territory): String = value.name
    @TypeConverter
    fun toTerritory(value: String): Territory = try { Territory.valueOf(value) } catch (e: Exception) { Territory.WORLDWIDE }

    @TypeConverter
    fun fromPayoutStatus(value: PayoutStatus): String = value.name
    @TypeConverter
    fun toPayoutStatus(value: String): PayoutStatus = try { PayoutStatus.valueOf(value) } catch (e: Exception) { PayoutStatus.REQUESTED }

    @TypeConverter
    fun fromPayoutMethod(value: PayoutMethod): String = value.name
    @TypeConverter
    fun toPayoutMethod(value: String): PayoutMethod = try { PayoutMethod.valueOf(value) } catch (e: Exception) { PayoutMethod.CIH_BANK }
}
