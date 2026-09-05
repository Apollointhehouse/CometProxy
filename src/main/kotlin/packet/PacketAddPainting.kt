package dev.apollointhehouse.packet

import dev.apollointhehouse.packet.Packet.Companion.readJavaStringUTF8
import dev.apollointhehouse.packet.Packet.Companion.writeJavaStringUTF8
import io.ktor.utils.io.*

class PacketAddPainting(
    val entityId: Int = 0,
    val xPosition: Int = 0,
    val yPosition: Int = 0,
    val zPosition: Int = 0,
    val direction: Int = 0,
    val key: String = "",
    val itemID: Int = 0,
    val meta: Int = 0,
) : Packet {
    

    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeInt(entityId)
        channel.writeJavaStringUTF8(key)
        channel.writeInt(xPosition)
        channel.writeInt(yPosition)
        channel.writeInt(zPosition)
        channel.writeInt(direction)
        channel.writeInt(itemID)
        channel.writeInt(meta)
    }

    override val estimatedSize: Int
        get() = 24

    companion object : PacketFactory<PacketAddPainting> {
		override val packetID = 25
        override suspend fun create(channel: ByteReadChannel): PacketAddPainting {
            val entityId = channel.readInt()
            val key = channel.readJavaStringUTF8(30)
            val xPosition = channel.readInt()
            val yPosition = channel.readInt()
            val zPosition = channel.readInt()
            val direction = channel.readInt()
            val itemID = channel.readInt()
            val meta = channel.readInt()

            return PacketAddPainting(entityId = entityId, xPosition = xPosition, yPosition = yPosition, zPosition = zPosition, direction = direction, key = key, itemID = itemID, meta = meta)
        }
    }
}
