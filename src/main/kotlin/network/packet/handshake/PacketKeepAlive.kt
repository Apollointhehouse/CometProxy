package dev.apollointhehouse.network.packet.handshake

import dev.apollointhehouse.network.packet.BufferedPacketFactory
import dev.apollointhehouse.network.packet.Packet
import kotlinx.io.Sink
import kotlinx.io.Source

data object PacketKeepAlive : Packet, BufferedPacketFactory<PacketKeepAlive> {
    override val size: Int = 0

    override fun write(sink: Sink) {}

    override val estimatedSize: Int
        get() = size

    override fun create(buffer: Source): PacketKeepAlive = PacketKeepAlive
}