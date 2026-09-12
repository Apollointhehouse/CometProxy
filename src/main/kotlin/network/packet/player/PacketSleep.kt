package dev.apollointhehouse.network.packet.player

import dev.apollointhehouse.network.packet.BufferedPacketFactory
import dev.apollointhehouse.network.packet.Packet
import kotlinx.io.Sink
import kotlinx.io.Source

data class PacketSleep(
    val entityID: Int = 0,
    val x: Int = 0,
    val y: Int = 0,
    val z: Int = 0,
    val wtf: Byte = 0,
) : Packet {
    override fun write(sink: Sink) {
        sink.writeInt(entityID)
        sink.writeByte(wtf)
        sink.writeInt(x)
        sink.writeInt(y)
        sink.writeInt(z)
    }

    override val estimatedSize: Int
        get() = size

    companion object : BufferedPacketFactory<PacketSleep> {
        override val size: Int = 17

        override fun create(buffer: Source): PacketSleep {
            val entityID = buffer.readInt()
            val wtf = buffer.readByte()
            val x = buffer.readInt()
            val y = buffer.readInt()
            val z = buffer.readInt()

            return PacketSleep(entityID = entityID, x = x, y = y, z = z, wtf = wtf)
        }
    }
}
