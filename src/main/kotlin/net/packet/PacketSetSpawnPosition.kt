package dev.apollointhehouse.net.packet

import io.ktor.utils.io.*

class PacketSetSpawnPosition(
    val x: Int = 0,
    val y: Int = 0,
    val z: Int = 0,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeInt(x)
        channel.writeInt(y)
        channel.writeInt(z)
    }

    override val estimatedSize: Int
        get() = 12

    companion object : PacketFactory<PacketSetSpawnPosition> {
        override suspend fun create(channel: ByteReadChannel): PacketSetSpawnPosition {
            val x = channel.readInt()
            val y = channel.readInt()
            val z = channel.readInt()

            return PacketSetSpawnPosition(x = x, y = y, z = z)
        }
    }
}
