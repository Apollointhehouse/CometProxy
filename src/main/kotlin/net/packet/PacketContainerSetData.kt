package dev.apollointhehouse.net.packet

import io.ktor.utils.io.*

class PacketContainerSetData(
    val windowId: Byte = 0,
    val progressBar: Short = 0,
    val progressBarValue: Short = 0,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeByte(windowId)
        channel.writeShort(progressBar)
        channel.writeShort(progressBarValue)
    }

    override val estimatedSize: Int
        get() = 7

    companion object : PacketFactory<PacketContainerSetData> {
		override suspend fun create(channel: ByteReadChannel): PacketContainerSetData {
            val windowId = channel.readByte()
            val progressBar = channel.readShort()
            val progressBarValue = channel.readShort()

            return PacketContainerSetData(windowId = windowId, progressBar = progressBar, progressBarValue = progressBarValue)
        }
    }
}
