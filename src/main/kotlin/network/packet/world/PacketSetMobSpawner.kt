package dev.apollointhehouse.network.packet.world

import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.StreamingPacketFactory
import io.ktor.utils.io.*
import kotlinx.io.Sink
import kotlin.experimental.and

data class PacketSetMobSpawner(
    val x: Int = 0,
    val y: Short = 0,
    val z: Int = 0,
    val dispatcherEntry: Short = -1,
) : Packet {
    override fun write(sink: Sink) {
        sink.writeInt(x)
        sink.writeShort(y)
        sink.writeInt(z)
        if (dispatcherEntry.toInt() != -1) {
            sink.writeShort(dispatcherEntry)
        } else {
            sink.writeShort(65535.toShort())
        }
    }

    override val estimatedSize: Int
        get() = 14

    companion object : StreamingPacketFactory<PacketSetMobSpawner> {
        override suspend fun create(channel: ByteReadChannel): PacketSetMobSpawner {
            val x = channel.readInt()
            val y = channel.readShort()
            val z = channel.readInt()

            val type = (channel.readShort() and '\uffff'.code.toShort())
            val dispatcherEntry = if (type == 65535.toShort()) (-1).toShort() else type

            return PacketSetMobSpawner(x = x, y = y, z = z, dispatcherEntry = dispatcherEntry)
        }
    }
}