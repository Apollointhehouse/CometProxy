package dev.apollointhehouse.network.packet.handshake

import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.PacketFactory
import io.ktor.utils.io.*

data object PacketKeepAlive : Packet, PacketFactory<PacketKeepAlive> {
    override suspend fun write(channel: ByteWriteChannel) {}

    override val estimatedSize: Int
        get() = 0

    override suspend fun create(channel: ByteReadChannel): PacketKeepAlive = PacketKeepAlive
}