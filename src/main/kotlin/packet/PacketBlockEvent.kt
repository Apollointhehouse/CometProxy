package dev.apollointhehouse.packet

import io.ktor.utils.io.*

class PacketBlockEvent(
    val xLocation: Int = 0,
    val yLocation: Short = 0,
    val zLocation: Int = 0,
    val index: Byte = 0,
    val data: Byte = 0,
) : Packet {
    

    override suspend fun write(channel: ByteWriteChannel) {
      channel.writeInt(xLocation)
      channel.writeShort(yLocation)
      channel.writeInt(zLocation)
      channel.writeByte(index)
      channel.writeByte(data)
    }

    override val estimatedSize: Int
        get() = 12

    companion object : PacketFactory<PacketBlockEvent> {
		override val packetID = 54
        override suspend fun create(channel: ByteReadChannel): PacketBlockEvent {
            val xLocation = channel.readInt()
            val yLocation = channel.readShort()
            val zLocation = channel.readInt()
            val index = channel.readByte()
            val data = channel.readByte()

            return PacketBlockEvent(xLocation = xLocation, yLocation = yLocation, zLocation = zLocation, index = index, data = data)
        }
    }
}
