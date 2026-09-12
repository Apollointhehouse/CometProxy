package dev.apollointhehouse.network.packet.entity

import dev.apollointhehouse.network.packet.BufferedPacketFactory
import dev.apollointhehouse.network.packet.Packet
import io.ktor.utils.io.*
import kotlinx.io.Source
import kotlinx.io.readFloat

data class PacketEntityEvent(
    val entityId: Int = 0,
    val entityStatus: Byte = 0,
    val attackedAtYaw: Float = 0f,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeInt(entityId)
        channel.writeByte(entityStatus)
        channel.writeFloat(attackedAtYaw)
    }

    override val estimatedSize: Int
        get() = size

    companion object : BufferedPacketFactory<PacketEntityEvent> {
        override val size: Int = 9

        override fun create(buffer: Source): PacketEntityEvent {
            val entityId = buffer.readInt()
            val entityStatus = buffer.readByte()
            val attackedAtYaw = buffer.readFloat()

            return PacketEntityEvent(entityId = entityId, entityStatus = entityStatus, attackedAtYaw = attackedAtYaw)
        }
    }
}
