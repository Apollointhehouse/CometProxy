package dev.apollointhehouse.network.packet.player

import dev.apollointhehouse.network.packet.BufferedPacketFactory
import dev.apollointhehouse.network.packet.Packet
import io.ktor.utils.io.*
import kotlinx.io.Source

data class PacketPlayerConfig(
    val entityId: Int = 0,
    val config: Short = 0,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeInt(entityId)
        channel.writeShort(config)
    }

    override val estimatedSize: Int
        get() = size

    companion object : BufferedPacketFactory<PacketPlayerConfig> {
        override val size: Int = 6

        override fun create(buffer: Source): PacketPlayerConfig {
            val entityId = buffer.readInt()
            val config = buffer.readShort()

            return PacketPlayerConfig(entityId = entityId, config = config)
        }
    }
}
