package dev.apollointhehouse.network.packet.entity

import dev.apollointhehouse.network.packet.BufferedPacketFactory
import dev.apollointhehouse.network.packet.Packet
import io.ktor.utils.io.*
import kotlinx.io.Source

data class PacketRemoveEntity(
    val entityId: Int = 0,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeInt(entityId)
    }

    override val estimatedSize: Int
        get() = size

    companion object : BufferedPacketFactory<PacketRemoveEntity> {
        override val size: Int = 4

        override fun create(buffer: Source): PacketRemoveEntity {
            val entityId = buffer.readInt()

            return PacketRemoveEntity(entityId = entityId)
        }
    }
}
