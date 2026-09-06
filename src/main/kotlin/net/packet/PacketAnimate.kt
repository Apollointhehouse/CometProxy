package dev.apollointhehouse.net.packet

import io.ktor.utils.io.*

class PacketAnimate(
    val entityId: Int = 0,
    val animate: Byte = 0,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeInt(entityId)
        channel.writeByte(animate)
    }

    override val estimatedSize: Int
        get() = 5

    companion object : PacketFactory<PacketAnimate> {
		override val packetID = 18
        override suspend fun create(channel: ByteReadChannel): PacketAnimate {
            val entityId = channel.readInt()
            val animate = channel.readByte()

            return PacketAnimate(entityId = entityId, animate = animate)
        }
    }
}