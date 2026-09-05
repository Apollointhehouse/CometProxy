package dev.apollointhehouse.packet

import io.ktor.utils.io.*

class PacketPlayerConfig(
    val entityId: Int = 0,
    val config: Short = 0,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeInt(entityId)
        channel.writeShort(config)
    }

    override val estimatedSize: Int
        get() = 5

    companion object : PacketFactory<PacketPlayerConfig> {
		override val packetID = 36
        override suspend fun create(channel: ByteReadChannel): PacketPlayerConfig {
            val entityId = channel.readInt()
            val config = channel.readShort()

            return PacketPlayerConfig(entityId = entityId, config = config)
        }
    }
}
