package dev.apollointhehouse.net.packet

import io.ktor.utils.io.*

class PacketEntityInteract(
    val sourceEntityID: Int = 0,
    val targetEntityID: Int = 0,
    val action: Byte = 0,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeInt(sourceEntityID)
        channel.writeInt(targetEntityID)
        channel.writeByte(action)
    }

    override val estimatedSize: Int
        get() = 9

    companion object : PacketFactory<PacketEntityInteract> {
		override val packetID = 7
        override suspend fun create(channel: ByteReadChannel): PacketEntityInteract {
            val sourceEntityID = channel.readInt()
            val targetEntityID = channel.readInt()
            val action = channel.readByte()

            return PacketEntityInteract(sourceEntityID = sourceEntityID, targetEntityID = targetEntityID, action = action)
        }
    }
}
