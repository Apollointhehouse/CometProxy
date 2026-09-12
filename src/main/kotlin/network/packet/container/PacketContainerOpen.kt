package dev.apollointhehouse.network.packet.container

import dev.apollointhehouse.network.extensions.readJavaStringUTF8
import dev.apollointhehouse.network.extensions.writeJavaStringUTF8
import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.StreamingPacketFactory
import io.ktor.utils.io.*
import kotlinx.io.Sink

data class PacketContainerOpen(
    val windowId: Byte = 0,
    val inventoryType: Byte = 0,
    val windowTitle: String = "",
    val slotsCount: Byte = 0,
) : Packet {


    override fun write(sink: Sink) {
        sink.writeByte(windowId)
        sink.writeByte(inventoryType)
        sink.writeJavaStringUTF8(windowTitle)
        sink.writeByte(slotsCount)
    }

    override val estimatedSize: Int
        get() = 3 + this.windowTitle.length

    companion object : StreamingPacketFactory<PacketContainerOpen> {
        override suspend fun create(channel: ByteReadChannel): PacketContainerOpen {
            val windowId = channel.readByte()
            val inventoryType = channel.readByte()
            val windowTitle = channel.readJavaStringUTF8(50)
            val slotsCount = channel.readByte()

            return PacketContainerOpen(
                windowId = windowId,
                inventoryType = inventoryType,
                windowTitle = windowTitle,
                slotsCount = slotsCount
            )
        }
    }
}
