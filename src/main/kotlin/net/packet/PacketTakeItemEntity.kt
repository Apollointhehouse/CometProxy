package dev.apollointhehouse.net.packet

import io.ktor.utils.io.*

class PacketTakeItemEntity(
    val collectedEntityId: Int = 0,
    val collectorEntityId: Int = 0,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeInt(collectedEntityId)
        channel.writeInt(collectorEntityId)
    }

    override val estimatedSize: Int
        get() = 8

    companion object : PacketFactory<PacketTakeItemEntity> {
		override val packetID = 22
        override suspend fun create(channel: ByteReadChannel): PacketTakeItemEntity {
            val collectedEntityId = channel.readInt()
            val collectorEntityId = channel.readInt()

            return PacketTakeItemEntity(collectedEntityId = collectedEntityId, collectorEntityId = collectorEntityId)
        }
    }
}
