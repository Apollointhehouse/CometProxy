package dev.apollointhehouse.network.packet.player

import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.PacketFactory
import io.ktor.utils.io.*

data class PacketRespawn(
    val respawnDimensionId: Int = 0,
    val respawnWorldTypeId: Int = 0,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeInt(respawnDimensionId)
        channel.writeInt(respawnWorldTypeId)
    }

    override val estimatedSize: Int
        get() = 8

    companion object : PacketFactory<PacketRespawn> {
		override suspend fun create(channel: ByteReadChannel): PacketRespawn {
            val respawnDimensionId = channel.readInt()
            val respawnWorldTypeId = channel.readInt()

            return PacketRespawn(respawnDimensionId = respawnDimensionId, respawnWorldTypeId = respawnWorldTypeId)
        }
    }
}
