package dev.apollointhehouse.network.packet.player

import dev.apollointhehouse.network.packet.BufferedPacketFactory
import dev.apollointhehouse.network.packet.Packet
import io.ktor.utils.io.*
import kotlinx.io.Source

data class PacketAnimate(
    val entityId: Int = 0,
    val animate: Byte = 0,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeInt(entityId)
        channel.writeByte(animate)
    }

    override val estimatedSize: Int
        get() = size

    companion object : BufferedPacketFactory<PacketAnimate> {
        override val size: Int = 5

        override fun create(buffer: Source): PacketAnimate {
            val entityId = buffer.readInt()
            val animate = buffer.readByte()

            return PacketAnimate(entityId = entityId, animate = animate)
        }
    }
}