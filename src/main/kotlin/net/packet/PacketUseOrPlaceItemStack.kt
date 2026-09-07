package dev.apollointhehouse.net.packet

import io.ktor.utils.io.*

class PacketUseOrPlaceItemStack(
    val xPosition: Int = 0,
    val yPosition: Int = 0,
    val zPosition: Int = 0,
    val xPlaced: Double = 0.0,
    val yPlaced: Double = 0.0,
    val type: Byte = 0,
    val direction: Int = 0,
    val itemID: Short = -1,
    val stackSize: Byte = -1,
    val meta: Short = -1
) : Packet {
    

    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeInt(xPosition)
        channel.writeByte(yPosition.toByte())
        channel.writeInt(zPosition)
        channel.writeByte(direction.toByte())
        channel.writeDouble(xPlaced)
        channel.writeDouble(yPlaced)
        channel.writeByte(type)

        if (itemID < 0) {
           channel.writeShort(-1)
        } else {
           channel.writeShort(itemID)
           channel.writeByte(stackSize)
           channel.writeShort(meta)
        }
    }

    override val estimatedSize: Int
        get() = 20

    companion object : PacketFactory<PacketUseOrPlaceItemStack> {
		override suspend fun create(channel: ByteReadChannel): PacketUseOrPlaceItemStack {
            val xPosition = channel.readInt()
            val yPosition = channel.readByte().toUByte().toInt()
            val zPosition = channel.readInt()
            val direction = channel.readByte().toUByte().toInt()
            val xPlaced = channel.readDouble()
            val yPlaced = channel.readDouble()
            val type = channel.readByte()

            var stackSize: Byte = -1
            var meta: Short = -1

            val itemID = channel.readShort()
            if (itemID >= 0) {
                stackSize = channel.readByte()
                meta = channel.readShort()
            }

            return PacketUseOrPlaceItemStack(xPosition, yPosition, zPosition, xPlaced, yPlaced, type, direction, itemID, stackSize, meta)
        }
    }
}
