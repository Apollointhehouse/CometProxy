package dev.apollointhehouse.network.packet.player

import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.PacketFactory
import io.ktor.utils.io.*

data class PacketSetCarriedItem(
    val id: Short = 0,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeShort(id)
    }

    override val estimatedSize: Int
        get() = 2

    companion object : PacketFactory<PacketSetCarriedItem> {
		override suspend fun create(channel: ByteReadChannel): PacketSetCarriedItem {
            val id = channel.readShort()

            return PacketSetCarriedItem(id = id)
        }
    }
}
