package dev.apollointhehouse.packet

import io.ktor.utils.io.*

class PacketRemoveEntity(
    val entityId: Int = 0,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeInt(entityId)
    }

    override val estimatedSize: Int
        get() = 4

    companion object : PacketFactory<PacketRemoveEntity> {
		override val packetID = 29
        override suspend fun create(channel: ByteReadChannel): PacketRemoveEntity {
            val entityId = channel.readInt()

            return PacketRemoveEntity(entityId = entityId)
        }
    }
}
