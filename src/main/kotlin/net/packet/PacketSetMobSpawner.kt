package dev.apollointhehouse.net.packet

import io.ktor.utils.io.*
import kotlin.experimental.and

data class PacketSetMobSpawner(
    val x: Int = 0,
    val y: Short = 0,
    val z: Int = 0,
    val dispatcherEntry: Short = -1,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeInt(x)
        channel.writeShort(y)
        channel.writeInt(z)
        if (dispatcherEntry.toInt() != -1) {
            channel.writeShort(dispatcherEntry)
        } else {
            channel.writeShort(65535.toShort())
        }
    }

    override val estimatedSize: Int
        get() = 14

    companion object : PacketFactory<PacketSetMobSpawner> {
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