package dev.apollointhehouse.packet

import io.ktor.utils.io.*

class PacketPlaySound(
    val soundID: Int = 0,
    val data: Int = 0,
    val x: Int = 0,
    val y: Int = 0,
    val z: Int = 0,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeInt(soundID)
        channel.writeInt(x)
        channel.writeInt(y)
        channel.writeInt(z)
        channel.writeInt(data)
    }

    override val estimatedSize: Int
        get() = 20

    companion object : PacketFactory<PacketPlaySound> {
		override val packetID = 61
        override suspend fun create(channel: ByteReadChannel): PacketPlaySound {
            val soundID = channel.readInt()
            val x = channel.readInt()
            val y = channel.readInt()
            val z = channel.readInt()
            val data = channel.readInt()

            return PacketPlaySound(soundID = soundID, data = data, x = x, y = y, z = z)
        }
    }
}
