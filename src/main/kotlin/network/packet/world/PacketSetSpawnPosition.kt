package dev.apollointhehouse.network.packet.world

import dev.apollointhehouse.network.packet.BufferedPacketFactory
import dev.apollointhehouse.network.packet.Packet
import kotlinx.io.Sink
import kotlinx.io.Source

data class PacketSetSpawnPosition(
    val x: Int = 0,
    val y: Int = 0,
    val z: Int = 0,
) : Packet {
    override fun write(sink: Sink) {
        sink.writeInt(x)
        sink.writeInt(y)
        sink.writeInt(z)
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
