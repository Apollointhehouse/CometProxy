package dev.apollointhehouse.net.packet

import io.ktor.utils.io.*

data class PacketTeleportEntity(
    val id: Int = 0,
    val x: Int = 0,
    val y: Int = 0,
    val z: Int = 0,
    val yaw: Int = 0,
    val pitch: Int = 0,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeInt(id)
        channel.writeInt(x)
        channel.writeInt(y)
        channel.writeInt(z)
        channel.writeByte(yaw.toByte())
        channel.writeByte(pitch.toByte())
    }

    override val estimatedSize: Int
        get() = 34

    companion object : PacketFactory<PacketTeleportEntity> {
		override suspend fun create(channel: ByteReadChannel): PacketTeleportEntity {
            val id = channel.readInt()
            val x = channel.readInt()
            val y = channel.readInt()
            val z = channel.readInt()
            val yaw = channel.readByte().toUByte().toInt()
            val pitch = channel.readByte().toUByte().toInt()

            return PacketTeleportEntity(id = id, x = x, y = y, z = z, yaw = yaw, pitch = pitch)
        }
    }
}
