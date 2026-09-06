package dev.apollointhehouse.net.packet

import io.ktor.utils.io.*

class PacketEntityEvent(
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
        get() = 9

    companion object : PacketFactory<PacketEntityEvent> {
		override val packetID = 38
        override suspend fun create(channel: ByteReadChannel): PacketEntityEvent {
            val entityId = channel.readInt()
            val entityStatus = channel.readByte()
            val attackedAtYaw = channel.readFloat()

            return PacketEntityEvent(entityId = entityId, entityStatus = entityStatus, attackedAtYaw = attackedAtYaw)
        }
    }
}
