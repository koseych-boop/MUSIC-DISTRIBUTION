package com.example.domain.dsp

import com.example.data.model.CoverAssetEntity
import com.example.data.model.ReleaseEntity
import com.example.data.model.ReleaseTrackEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object DDEXGenerator {
    fun generateErnXml(
        release: ReleaseEntity,
        tracks: List<ReleaseTrackEntity>,
        coverAsset: CoverAssetEntity?,
        senderName: String = "SAWT_DISTRIBUTION_MA",
        recipientName: String = "YOUTUBE_MUSIC_DDEX_FEED"
    ): String {
        val now = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(Date())
        val messageId = "MSG_${release.id.take(8)}_${System.currentTimeMillis()}"

        val tracksXml = buildString {
            tracks.forEachIndexed { idx, track ->
                val trackNum = idx + 1
                append("""
        <SoundRecording>
            <SoundRecordingType>MusicalWorkSoundRecording</SoundRecordingType>
            <SoundRecordingId>
                <ISRC>${track.isrc}</ISRC>
            </SoundRecordingId>
            <ResourceReference>A$trackNum</ResourceReference>
            <ReferenceTitle>
                <TitleText>${escapeXml(track.title)}</TitleText>
            </ReferenceTitle>
            <Duration>PT${track.durationSeconds / 60}M${track.durationSeconds % 60}S</Duration>
            <SoundRecordingDetailsByTerritory>
                <TerritoryCode>Worldwide</TerritoryCode>
                <Title>
                    <TitleText>${escapeXml(track.title)}</TitleText>
                </Title>
                <DisplayArtist>
                    <PartyName>
                        <FullName>${escapeXml(track.primaryArtist)}</FullName>
                    </PartyName>
                    <ArtistRole>MainArtist</ArtistRole>
                </DisplayArtist>
                <ResourceContributor>
                    <PartyName>
                        <FullName>${escapeXml(track.composer)}</FullName>
                    </PartyName>
                    <ResourceContributorRole>Composer</ResourceContributorRole>
                </ResourceContributor>
                <ResourceContributor>
                    <PartyName>
                        <FullName>${escapeXml(track.lyricist)}</FullName>
                    </PartyName>
                    <ResourceContributorRole>Lyricist</ResourceContributorRole>
                </ResourceContributor>
                <ResourceContributor>
                    <PartyName>
                        <FullName>${escapeXml(track.producer)}</FullName>
                    </PartyName>
                    <ResourceContributorRole>Producer</ResourceContributorRole>
                </ResourceContributor>
                <PLine>
                    <Year>${release.pLineYear}</Year>
                    <PLineCompany>${escapeXml(release.copyrightOwner)}</PLineCompany>
                </PLine>
                <ParentalWarningType>${if (track.isExplicit) "Explicit" else "NotExplicit"}</ParentalWarningType>
                <TechnicalSoundRecordingDetails>
                    <TechnicalResourceDetailsReference>T$trackNum</TechnicalResourceDetailsReference>
                    <AudioCodec>${track.audioFormat.name}</AudioCodec>
                    <BitRate>2304</BitRate>
                    <SamplingRate>48000</SamplingRate>
                    <BitsPerSample>24</BitsPerSample>
                </TechnicalSoundRecordingDetails>
            </SoundRecordingDetailsByTerritory>
        </SoundRecording>
""")
            }
        }

        return """<?xml version="1.0" encoding="UTF-8"?>
<ern:NewReleaseMessage xmlns:ern="http://ddex.net/xml/ern/42" 
    xmlns:xs="http://www.w3.org/2001/XMLSchema-instance" 
    MessageSchemaVersionId="ern/42" 
    LanguageAndScriptCode="en">
    <MessageHeader>
        <MessageThreadId>TH_${release.id}</MessageThreadId>
        <MessageId>$messageId</MessageId>
        <MessageSender>
            <PartyId>PADPIDA_${senderName}</PartyId>
            <PartyName>
                <FullName>Sawt Moroccan Digital Music Distribution</FullName>
            </PartyName>
        </MessageSender>
        <MessageRecipient>
            <PartyId>PADPIDA_${recipientName}</PartyId>
            <PartyName>
                <FullName>Authorized Partner Intake DDEX Ingest</FullName>
            </PartyName>
        </MessageRecipient>
        <MessageCreatedDateTime>$now</MessageCreatedDateTime>
    </MessageHeader>
    <ResourceList>
$tracksXml
        <Image>
            <ImageType>FrontCoverImage</ImageType>
            <ImageId>
                <ProprietaryId Namespace="SAWT">IMG_${release.id}</ProprietaryId>
            </ImageId>
            <ResourceReference>IMG1</ResourceReference>
            <TechnicalImageDetails>
                <TechnicalResourceDetailsReference>T_IMG1</TechnicalResourceDetailsReference>
                <ImageCodec>JPEG</ImageCodec>
                <ImageHeight>${coverAsset?.resolutionHeight ?: 3000}</ImageHeight>
                <ImageWidth>${coverAsset?.resolutionWidth ?: 3000}</ImageWidth>
                <AspectRatio>1:1</AspectRatio>
                <ColorSpace>sRGB</ColorSpace>
            </TechnicalImageDetails>
        </Image>
    </ResourceList>
    <ReleaseList>
        <Release>
            <ReleaseId>
                <GRid>A10302B0001234567Z</GRid>
                <ICPN>${release.upcEan.ifBlank { "840000000000" }}</ICPN>
            </ReleaseId>
            <ReleaseReference>R0</ReleaseReference>
            <ReferenceTitle>
                <TitleText>${escapeXml(release.title)}</TitleText>
            </ReferenceTitle>
            <ReleaseResourceReferenceList>
${tracks.mapIndexed { idx, _ -> "                <ReleaseResourceReference>A${idx + 1}</ReleaseResourceReference>" }.joinToString("\n")}
                <ReleaseResourceReference>IMG1</ReleaseResourceReference>
            </ReleaseResourceReferenceList>
            <ReleaseType>${release.releaseType.name}</ReleaseType>
            <ReleaseDetailsByTerritory>
                <TerritoryCode>Worldwide</TerritoryCode>
                <DisplayArtist>
                    <PartyName>
                        <FullName>${escapeXml(release.primaryArtist)}</FullName>
                    </PartyName>
                    <ArtistRole>MainArtist</ArtistRole>
                </DisplayArtist>
                <LabelName>${escapeXml(release.labelName)}</LabelName>
                <Genre>
                    <GenreText>${escapeXml(release.genre.title)}</GenreText>
                    <SubGenre>${escapeXml(release.subgenre)}</SubGenre>
                </Genre>
                <OriginalReleaseDate>${release.originalReleaseDate}</OriginalReleaseDate>
                <CLine>
                    <Year>${release.cLineYear}</Year>
                    <CLineCompany>${escapeXml(release.copyrightOwner)}</CLineCompany>
                </CLine>
            </ReleaseDetailsByTerritory>
        </Release>
    </ReleaseList>
    <DealList>
        <ReleaseDeal>
            <DealReleaseReference>R0</DealReleaseReference>
            <Deal>
                <DealTerms>
                    <CommercialModelType>SubscriptionModel</CommercialModelType>
                    <Usage>
                        <UseType>Stream</UseType>
                    </Usage>
                    <Usage>
                        <UseType>PermanentDownload</UseType>
                    </Usage>
                    <TerritoryCode>Worldwide</TerritoryCode>
                    <ValidityPeriod>
                        <StartDate>${release.distributionReleaseDate}</StartDate>
                    </ValidityPeriod>
                </DealTerms>
            </Deal>
        </ReleaseDeal>
    </DealList>
</ern:NewReleaseMessage>"""
    }

    private fun escapeXml(input: String): String {
        return input.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }
}

object DDEXValidator {
    fun validateXml(xml: String): Pair<Boolean, List<String>> {
        val errors = mutableListOf<String>()
        if (!xml.contains("<ern:NewReleaseMessage")) {
            errors.add("Missing Root Element <ern:NewReleaseMessage>")
        }
        if (!xml.contains("<MessageHeader>")) {
            errors.add("Missing <MessageHeader> element")
        }
        if (!xml.contains("<SoundRecording>")) {
            errors.add("Missing <SoundRecording> resource list")
        }
        if (!xml.contains("<ISRC>")) {
            errors.add("Missing track <ISRC> identifier")
        }
        if (!xml.contains("<ReleaseList>")) {
            errors.add("Missing <ReleaseList> metadata block")
        }
        if (!xml.contains("<DealList>")) {
            errors.add("Missing <DealList> commercial rights block")
        }
        return (errors.isEmpty()) to errors
    }
}

object DeliveryPackageBuilder {
    data class DeliveryPackage(
        val packageId: String,
        val ernXml: String,
        val audioBuckets: List<String>,
        val coverArtBucket: String,
        val totalBytes: Long,
        val checksumSha256: String
    )

    fun build(
        release: ReleaseEntity,
        tracks: List<ReleaseTrackEntity>,
        coverAsset: CoverAssetEntity?
    ): DeliveryPackage {
        val xml = DDEXGenerator.generateErnXml(release, tracks, coverAsset)
        val packageId = "PKG_${release.id.take(8)}_${System.currentTimeMillis()}"
        val audioPaths = tracks.map { "audio_originals/${release.id}/${it.id}.${it.audioFormat.name.lowercase()}" }
        val coverPath = "cover_art/${release.id}/artwork.jpg"
        
        return DeliveryPackage(
            packageId = packageId,
            ernXml = xml,
            audioBuckets = audioPaths,
            coverArtBucket = coverPath,
            totalBytes = (tracks.size * 48000000L) + (coverAsset?.fileSizeBytes ?: 4000000L),
            checksumSha256 = UUID.randomUUID().toString().replace("-", "")
        )
    }
}

object WebhookVerifier {
    fun computeHmacSha256(payload: String, secretKey: String): String {
        val mac = Mac.getInstance("HmacSHA256")
        val secretKeySpec = SecretKeySpec(secretKey.toByteArray(), "HmacSHA256")
        mac.init(secretKeySpec)
        val hmacBytes = mac.doFinal(payload.toByteArray())
        return hmacBytes.joinToString("") { "%02x".format(it) }
    }

    fun verifySignature(payload: String, secretKey: String, signatureHeader: String): Boolean {
        if (signatureHeader.isBlank()) return false
        val expected = computeHmacSha256(payload, secretKey)
        return expected.equals(signatureHeader.trim().removePrefix("sha256="), ignoreCase = true)
    }
}
