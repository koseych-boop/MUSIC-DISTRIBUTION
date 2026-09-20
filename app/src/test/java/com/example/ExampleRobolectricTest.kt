package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.AudioFormat
import com.example.data.model.CoverAssetEntity
import com.example.data.model.MusicGenre
import com.example.data.model.ReleaseEntity
import com.example.data.model.ReleaseStatus
import com.example.data.model.ReleaseTrackEntity
import com.example.data.model.ReleaseType
import com.example.data.model.RightsDeclarationEntity
import com.example.domain.dsp.DDEXGenerator
import com.example.domain.dsp.WebhookVerifier
import com.example.domain.validation.ReleaseValidationEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read app name from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Sawt Distribution", appName)
  }

  @Test
  fun `validate moroccan ISRC code generator`() {
    val isrc = ReleaseValidationEngine.generateIsrc("MAA01", 2026, 1)
    assertEquals("MAA012600001", isrc)
    assertTrue(ReleaseValidationEngine.isValidIsrc(isrc))
    assertFalse(ReleaseValidationEngine.isValidIsrc("INVALID123"))
  }

  @Test
  fun `validate UPC EAN barcode generator`() {
    val upc = ReleaseValidationEngine.generateUpc("611")
    assertEquals(13, upc.length)
    assertTrue(upc.startsWith("611")) // Moroccan GS1 prefix
    assertTrue(ReleaseValidationEngine.isValidUpcEan(upc))
  }

  @Test
  fun `validate DDEX ERN 4_2 generator creates valid XML structure`() {
    val release = ReleaseEntity(
      id = "test_rel_01",
      artistId = "art_01",
      title = "Casablanca Sunset",
      primaryArtist = "OudWave",
      releaseType = ReleaseType.SINGLE,
      genre = MusicGenre.MOROCCAN_RAP,
      copyrightOwner = "OudWave Music",
      upcEan = "6119876543210",
      status = ReleaseStatus.PENDING_REVIEW
    )

    val track = ReleaseTrackEntity(
      id = "test_trk_01",
      releaseId = release.id,
      trackNumber = 1,
      title = "Casablanca Sunset",
      isrc = "MAA012600001",
      primaryArtist = "OudWave",
      composer = "Youssef Bennani",
      lyricist = "Youssef Bennani",
      producer = "Casablanca BeatLab",
      durationSeconds = 210,
      audioFormat = AudioFormat.WAV
    )

    val cover = CoverAssetEntity(
      releaseId = release.id,
      filePath = "covers/casablanca_sunset.png",
      resolutionWidth = 3000,
      resolutionHeight = 3000
    )

    val xml = DDEXGenerator.generateErnXml(release, listOf(track), cover)
    assertTrue(xml.contains("<ern:NewReleaseMessage"))
    assertTrue(xml.contains("<ICPN>6119876543210</ICPN>"))
    assertTrue(xml.contains("<ISRC>MAA012600001</ISRC>"))
    assertTrue(xml.contains("<TitleText>Casablanca Sunset</TitleText>"))
    assertTrue(xml.contains("<FullName>OudWave</FullName>"))
  }

  @Test
  fun `validate partner webhook HMAC-SHA256 signature verification`() {
    val payload = "{\"event\":\"RELEASE_LIVE\",\"release_id\":\"rel_123\"}"
    val secretKey = "morocco_partner_secret"
    val hmac = WebhookVerifier.computeHmacSha256(payload, secretKey)
    assertTrue(WebhookVerifier.verifySignature(payload, secretKey, "sha256=$hmac"))
    assertFalse(WebhookVerifier.verifySignature(payload, "wrong_secret", "sha256=$hmac"))
  }
}
