package dev.apollointhehouse.network.packet.player

import dev.apollointhehouse.network.packet.BufferedPacketFactory
import dev.apollointhehouse.network.packet.Packet
import io.ktor.utils.io.*
import kotlinx.io.Source

data class PacketRespawn(
    val respawnDimensionId: Int = 0,
    val respawnWorldTypeId: Int = 0,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeInt(respawnDimensionId)
        channel.writeInt(respawnWorldTypeId)
    }

    override val estimatedSize: Int
        get() = size

    companion object : BufferedPacketFactory<PacketRespawn> {
        override val size: Int = 8

        override fun create(buffer: Source): PacketRespawn {
            val respawnDimensionId = buffer.readInt()
            val respawnWorldTypeId = buffer.readInt()

            return PacketRespawn(respawnDimensionId = respawnDimensionId, respawnWorldTypeId = respawnWorldTypeId)
        }
    }
}
