package dev.apollointhehouse.network.packet.world

import dev.apollointhehouse.network.packet.BufferedPacketFactory
import dev.apollointhehouse.network.packet.Packet
import io.ktor.utils.io.*
import kotlinx.io.Source

data class PacketSetSpawnPosition(
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
        get() = size

    companion object : BufferedPacketFactory<PacketSetSpawnPosition> {
        override val size: Int = 12

        override fun create(buffer: Source): PacketSetSpawnPosition {
            val x = buffer.readInt()
            val y = buffer.readInt()
            val z = buffer.readInt()

            return PacketSetSpawnPosition(x = x, y = y, z = z)
        }
    }
}
