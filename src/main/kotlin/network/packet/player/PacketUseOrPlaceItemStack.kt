package dev.apollointhehouse.network.packet.player

import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.StreamingPacketFactory
import io.ktor.utils.io.*
import kotlinx.io.Sink
import kotlinx.io.writeDouble

data class PacketUseOrPlaceItemStack(
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
    override fun write(sink: Sink) {
        sink.writeInt(xPosition)
        sink.writeByte(yPosition.toByte())
        sink.writeInt(zPosition)
        sink.writeByte(direction.toByte())
        sink.writeDouble(xPlaced)
        sink.writeDouble(yPlaced)
        sink.writeByte(type)

        if (itemID < 0) {
            sink.writeShort(-1)
        } else {
            sink.writeShort(itemID)
            sink.writeByte(stackSize)
            sink.writeShort(meta)
        }
    }

    override val estimatedSize: Int
        get() = 20

    companion object : StreamingPacketFactory<PacketUseOrPlaceItemStack> {
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

            return PacketUseOrPlaceItemStack(
                xPosition,
                yPosition,
                zPosition,
                xPlaced,
                yPlaced,
                type,
                direction,
                itemID,
                stackSize,
                meta
            )
        }
    }
}
