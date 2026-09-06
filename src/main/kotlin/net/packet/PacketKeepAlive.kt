package dev.apollointhehouse.net.packet

import io.ktor.utils.io.*

class PacketKeepAlive : Packet {
    override suspend fun write(channel: ByteWriteChannel) {}

    override val estimatedSize: Int
        get() = 0

    companion object : PacketFactory<PacketKeepAlive> {
		override val packetID = 0
        override suspend fun create(channel: ByteReadChannel): PacketKeepAlive = PacketKeepAlive()
    }
}