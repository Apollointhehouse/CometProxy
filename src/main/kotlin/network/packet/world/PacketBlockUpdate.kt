package dev.apollointhehouse.network.packet.world

import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.PacketFactory
import io.ktor.utils.io.*

data class PacketBlockUpdate(
    val xPosition: Int = 0,
    val yPosition: Short = 0,
    val zPosition: Int = 0,
    val blockId: Short = 0,
    val metadata: Int = 0,
) : Packet {
    

    override suspend fun write(channel: ByteWriteChannel) {
      channel.writeInt(xPosition)
      channel.writeShort(yPosition)
      channel.writeInt(zPosition)
      channel.writeShort(blockId)
      channel.writeByte(metadata.toByte())
    }

    override val estimatedSize: Int
        get() = 11

    companion object : PacketFactory<PacketBlockUpdate> {
		override suspend fun create(channel: ByteReadChannel): PacketBlockUpdate {
            val xPosition = channel.readInt()
            val yPosition = channel.readShort()
            val zPosition = channel.readInt()
            val blockId = channel.readShort()
            val metadata = channel.readByte().toUByte().toInt()

            return PacketBlockUpdate(xPosition = xPosition, yPosition = yPosition, zPosition = zPosition, blockId = blockId, metadata = metadata)
        }
    }
}
