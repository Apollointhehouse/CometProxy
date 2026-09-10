package dev.apollointhehouse.network.packet.container


import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.PacketFactory
import io.ktor.utils.io.*

data class PacketContainerClose(
    val windowId: Byte = 0,
) : Packet {
    

    override suspend fun write(channel: ByteWriteChannel) {
      channel.writeByte(windowId)
    }

    override val estimatedSize: Int
        get() = 1

    companion object : PacketFactory<PacketContainerClose> {
		override suspend fun create(channel: ByteReadChannel): PacketContainerClose {
            val windowId = channel.readByte()

            return PacketContainerClose(windowId = windowId)
        }
    }
}
