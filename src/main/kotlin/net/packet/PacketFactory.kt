package dev.apollointhehouse.net.packet

import io.ktor.utils.io.*

interface PacketFactory<out T : Packet> {
    suspend fun create(channel: ByteReadChannel): T
}