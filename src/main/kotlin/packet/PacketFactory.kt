package dev.apollointhehouse.packet

import io.ktor.utils.io.*

interface PacketFactory<T : Packet> {
    val packetID: Int
    suspend fun create(channel: ByteReadChannel): T
}