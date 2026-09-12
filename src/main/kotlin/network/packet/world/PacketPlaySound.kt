package dev.apollointhehouse.network.packet.world

import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.StreamingPacketFactory
import io.ktor.utils.io.*
import kotlinx.io.Sink

data class PacketPlaySound(
    val soundID: Int = 0,
    val data: Int = 0,
    val x: Int = 0,
    val y: Int = 0,
    val z: Int = 0,
) : Packet {
    override fun write(sink: Sink) {
        sink.writeInt(soundID)
        sink.writeInt(x)
        sink.writeInt(y)
        sink.writeInt(z)
        sink.writeInt(data)
    }

    override val estimatedSize: Int
        get() = 20

    companion object : StreamingPacketFactory<PacketPlaySound> {
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
