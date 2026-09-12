package dev.apollointhehouse.network.packet

import io.ktor.utils.io.*
import kotlinx.io.Source

sealed interface PacketFactory<out T : Packet>

interface StreamingPacketFactory<out T : Packet> : PacketFactory<T> {
    suspend fun create(channel: ByteReadChannel): T
}

interface BufferedPacketFactory<out T : Packet> : PacketFactory<T> {
    val size: Int
    fun create(buffer: Source): T
}
