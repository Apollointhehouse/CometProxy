package dev.apollointhehouse.network.packet.entity

import dev.apollointhehouse.network.packet.BufferedPacketFactory
import dev.apollointhehouse.network.packet.Packet
import io.ktor.utils.io.*
import kotlinx.io.Source

data class PacketEntityInteract(
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
        get() = size

    companion object : BufferedPacketFactory<PacketEntityInteract> {
        override val size: Int = 9

        override fun create(buffer: Source): PacketEntityInteract {
            val sourceEntityID = buffer.readInt()
            val targetEntityID = buffer.readInt()
            val action = buffer.readByte()

            return PacketEntityInteract(
                sourceEntityID = sourceEntityID,
                targetEntityID = targetEntityID,
                action = action
            )
        }
    }
}
