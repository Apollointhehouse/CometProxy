package dev.apollointhehouse.network.packet.entity

import dev.apollointhehouse.network.extensions.readJavaStringUTF8
import dev.apollointhehouse.network.extensions.writeJavaStringUTF8
import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.StreamingPacketFactory
import io.ktor.utils.io.*
import kotlinx.io.Sink

data class PacketAddPainting(
    val entityId: Int = 0,
    val xPosition: Int = 0,
    val yPosition: Int = 0,
    val zPosition: Int = 0,
    val direction: Int = 0,
    val key: String = "",
    val itemID: Int = 0,
    val meta: Int = 0,
) : Packet {


    override fun write(sink: Sink) {
        sink.writeInt(entityId)
        sink.writeJavaStringUTF8(key)
        sink.writeInt(xPosition)
        sink.writeInt(yPosition)
        sink.writeInt(zPosition)
        sink.writeInt(direction)
        sink.writeInt(itemID)
        sink.writeInt(meta)
    }

    override val estimatedSize: Int
        get() = 24

    companion object : StreamingPacketFactory<PacketAddPainting> {
        override suspend fun create(channel: ByteReadChannel): PacketAddPainting {
            val entityId = channel.readInt()
            val key = channel.readJavaStringUTF8(30)
            val xPosition = channel.readInt()
            val yPosition = channel.readInt()
            val zPosition = channel.readInt()
            val direction = channel.readInt()
            val itemID = channel.readInt()
            val meta = channel.readInt()

            return PacketAddPainting(
                entityId = entityId,
                xPosition = xPosition,
                yPosition = yPosition,
                zPosition = zPosition,
                direction = direction,
                key = key,
                itemID = itemID,
                meta = meta
            )
        }
    }
}
