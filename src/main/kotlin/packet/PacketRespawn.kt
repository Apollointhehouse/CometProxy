package dev.apollointhehouse.packet

import io.ktor.utils.io.*

class PacketRespawn(
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
		override val packetID = 9
        override suspend fun create(channel: ByteReadChannel): PacketRespawn {
            val respawnDimensionId = channel.readInt()
            val respawnWorldTypeId = channel.readInt()

            return PacketRespawn(respawnDimensionId = respawnDimensionId, respawnWorldTypeId = respawnWorldTypeId)
        }
    }
}
