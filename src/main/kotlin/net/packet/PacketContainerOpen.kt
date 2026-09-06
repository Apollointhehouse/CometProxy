package dev.apollointhehouse.net.packet

import dev.apollointhehouse.net.packet.Packet.Companion.readJavaStringUTF8
import dev.apollointhehouse.net.packet.Packet.Companion.writeJavaStringUTF8
import io.ktor.utils.io.*

class PacketContainerOpen(
    val windowId: Byte = 0,
    val inventoryType: Byte = 0,
    val windowTitle: String = "",
    val slotsCount: Byte = 0,
) : Packet {
    

    override suspend fun write(channel: ByteWriteChannel) {
      channel.writeByte(windowId)
      channel.writeByte(inventoryType)
      channel.writeJavaStringUTF8(windowTitle)
      channel.writeByte(slotsCount)
    }

    override val estimatedSize: Int
        get() = 3 + this.windowTitle.length

    companion object : PacketFactory<PacketContainerOpen> {
		override val packetID = 100
        override suspend fun create(channel: ByteReadChannel): PacketContainerOpen {
            val windowId = channel.readByte()
            val inventoryType = channel.readByte()
            val windowTitle = channel.readJavaStringUTF8(50)
            val slotsCount = channel.readByte()

            return PacketContainerOpen(windowId = windowId, inventoryType = inventoryType, windowTitle = windowTitle, slotsCount = slotsCount)
        }
    }
}
