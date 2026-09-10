package dev.apollointhehouse.network

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import org.jetbrains.skia.Image
import kotlin.uuid.Uuid

object API {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    private val heads: MutableMap<Uuid, ImageBitmap> = mutableMapOf()

    suspend fun fetchHead(uuid: Uuid): ImageBitmap = heads.getOrPut(uuid) {
        val imgBytes = client.get("https://mc-heads.net/avatar/${uuid.toHexString()}").body<ByteArray>()
        val img = Image.makeFromEncoded(imgBytes).toComposeImageBitmap()

        img
    }
}